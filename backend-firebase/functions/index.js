const {onCall, HttpsError} = require("firebase-functions/v2/https");
const {defineSecret} = require("firebase-functions/params");
const {OpenAI} = require("openai");
const admin = require("firebase-admin");
const path = require("path");
const os = require("os");
const fs = require("fs-extra");

// Inicializamos Firebase Admin para acceder a Storage
admin.initializeApp();

const openaiApiKey = defineSecret("OPENAI_API_KEY");

// Helper to filter Whisper silence/noise hallucinations
function isWhisperHallucination(text) {
  if (!text) return true;
  const lower = text.toLowerCase().trim();
  if (lower.length === 0) return true;
  
  const hallucinations = [
    "amara.org",
    "subtítulo",
    "subtitulado",
    "gracias por ver",
    "thank you for watching",
    "transcripción por",
    "traducido por",
    "asociación de la comunidad",
    "reproducción de vídeo",
    "este vídeo"
  ];
  
  for (const phrase of hallucinations) {
    if (lower.includes(phrase)) {
      return true;
    }
  }
  return false;
}

// --- 1. FLUJO DE TEXTO ---
exports.getOpenAIResponse = onCall(
    {
      secrets: [openaiApiKey],
      region: "us-central1",
    },
    async (request) => {
      console.log("--- Nueva Petición de Texto ---");

      if (!request.auth) {
        throw new HttpsError("unauthenticated", "Sesión no detectada.");
      }

      const prompt = request.data.prompt;

      if (!prompt) {
        throw new HttpsError(
            "invalid-argument",
            "El prompt es obligatorio.",
        );
      }

      const openai = new OpenAI({
        apiKey: openaiApiKey.value(),
      });

      try {
        const completion = await openai.chat.completions.create({
          model: "gpt-4o-mini",
          messages: [
            {
              role: "system",
              content:
                "Eres un tutor experto. Responde siempre en:\n" +
                "Español: [res]\n" +
                "Quechua: [res]\n" +
                "Aimara: [res]",
            },
            {
              role: "user",
              content: prompt,
            },
          ],
        });

        return {
          response: completion.choices[0].message.content,
        };
      } catch (error) {
        console.error("OpenAI Error:", error);

        throw new HttpsError(
            "internal",
            "Error al conectar con la IA.",
        );
      }
    },
);

// --- 2. FLUJO DE AUDIO ---
exports.processAudioMessage = onCall(
    {
      secrets: [openaiApiKey],
      region: "us-central1",
      timeoutSeconds: 300,
    },
    async (request) => {
      console.log(
          "Nueva petición de audio. Usuario:",
          request.auth ? request.auth.uid : "ANONIMO",
      );

      // Ahora recibimos audioPath
      const audioPath = request.data.audioPath;

      if (!audioPath) {
        throw new HttpsError(
            "invalid-argument",
            "audioPath es requerido.",
        );
      }

      const openai = new OpenAI({
        apiKey: openaiApiKey.value(),
      });

      const tempFilePath = path.join(
          os.tmpdir(),
          `audio_${Date.now()}.m4a`,
      );

      try {
        // A. Descargar audio desde Firebase Storage
        console.log("Descargando archivo:", audioPath);

        const bucket = admin.storage().bucket();

        await bucket.file(audioPath).download({
          destination: tempFilePath,
        });

        // B. Transcribir Audio usando Whisper
        console.log("Enviando a Whisper...");

        const transcription =
          await openai.audio.transcriptions.create({
            file: fs.createReadStream(tempFilePath),
            model: "whisper-1",
            language: "es",
          });

        const userText = transcription.text;

        console.log("Transcripción exitosa:", userText);

        const cleanText = userText.trim().replace(/[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ]/g, "");
        if (cleanText.length === 0 || isWhisperHallucination(userText)) {
          console.log("Audio sin contenido de voz o alucinación de Whisper detectada.");
          return {
            transcription: "",
            response: "Disculpa, no logré escuchar tu voz en el audio. ¿Podrías intentar hablar de nuevo?",
            feedback: "Audio en silencio o sin voz detectable."
          };
        }

        // C. Procesar texto con GPT
        console.log("Generando respuesta y feedback con GPT...");

        const completion =
          await openai.chat.completions.create({
            model: "gpt-4o-mini",
            messages: [
              {
                role: "system",
                content:
                  "Eres un tutor experto en lenguas andinas. " +
                  "El usuario te envió un mensaje de voz.\n" +
                  "Analiza la transcripción y responde " +
                  "siguiendo estrictamente este formato JSON:\n" +
                  "{\n" +
                  "  \"response\": " +
                  "\"Español: [res]\\n" +
                  "Quechua: [res]\\n" +
                  "Aimara: [res]\",\n" +
                  "  \"feedback\": " +
                  "\"Retroalimentación sobre tu pronunciación " +
                  "(según transcripción), gramática y " +
                  "sugerencias de mejora.\"\n" +
                  "}",
              },
              {
                role: "user",
                content: userText,
              },
            ],
            response_format: {
              type: "json_object",
            },
          });

        const aiResult = JSON.parse(
            completion.choices[0].message.content,
        );

        return {
          transcription: userText,
          response: aiResult.response,
          feedback: aiResult.feedback,
        };
      } catch (error) {
        console.error("Critical Audio Error:", error);

        throw new HttpsError(
            "internal",
            `No pudimos procesar tu audio: ${error.message}`,
        );
      } finally {
        if (fs.existsSync(tempFilePath)) {
          await fs.remove(tempFilePath);
        }
      }
    },
);

// --- 3. EVALUACIÓN DE PRONUNCIACIÓN DE AUDIO ---
exports.assessPronunciation = onCall(
    {
      secrets: [openaiApiKey],
      region: "us-central1",
      timeoutSeconds: 300,
    },
    async (request) => {
      console.log(
          "Nueva petición de evaluación de pronunciación. Usuario:",
          request.auth ? request.auth.uid : "ANONIMO",
      );

      const { audioPath, targetWord, language, translation } = request.data;

      if (!audioPath || !targetWord || !language) {
        throw new HttpsError(
            "invalid-argument",
            "audioPath, targetWord y language son obligatorios.",
        );
      }

      const openai = new OpenAI({
        apiKey: openaiApiKey.value(),
      });

      const tempFilePath = path.join(
          os.tmpdir(),
          `audio_assess_${Date.now()}.m4a`,
      );

      try {
        // A. Descargar audio desde Firebase Storage
        console.log("Descargando archivo para evaluación:", audioPath);
        const bucket = admin.storage().bucket();
        await bucket.file(audioPath).download({
          destination: tempFilePath,
        });

        // B. Transcribir Audio usando Whisper
        console.log("Enviando a Whisper...");
        const transcription = await openai.audio.transcriptions.create({
          file: fs.createReadStream(tempFilePath),
          model: "whisper-1",
          language: "es", // Whisper transcribirá lo que entiende en formato español
        });

        const userText = transcription.text;
        console.log("Transcripción de Whisper:", userText);

        const cleanText = userText.trim().replace(/[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ]/g, "");
        if (cleanText.length === 0 || isWhisperHallucination(userText)) {
          console.log("Audio sin contenido de voz o alucinación de Whisper detectada en evaluación.");
          return {
            transcription: "",
            isCorrect: false,
            feedback: "No se detectó tu voz o la pronunciación de la palabra. Por favor, habla más fuerte y claro cerca del micrófono."
          };
        }

        // C. Evaluar pronunciación con GPT-4
        console.log("Evaluando con GPT-4...");
        const completion = await openai.chat.completions.create({
          model: "gpt-4o", // Usamos gpt-4o de alto nivel para un análisis acústico y lingüístico riguroso
          messages: [
            {
              role: "system",
              content:
                "Eres un evaluador lingüístico experto en lenguas andinas (Quechua y Aimara).\n" +
                "El usuario está tratando de pronunciar la palabra: \"" + targetWord + "\"" + (translation ? " (que significa \"" + translation + "\" en español)" : "") + " en el idioma " + language + ".\n" +
                "El sistema de transcripción escuchó y escribió lo siguiente: \"" + userText + "\".\n" +
                "Analiza la similitud fonética de lo escuchado con respecto a la palabra objetivo y determina si la pronunciación es correcta o aceptable. Ten en cuenta que el transcriptor está configurado en español, por lo que adaptará sonidos nativos a letras en español (por ejemplo, transcribir 'puka' como 'puca' o 'poca' es fonéticamente aceptable).\n" +
                "Responde estrictamente en formato JSON con los siguientes campos:\n" +
                "{\n" +
                "  \"isCorrect\": true o false,\n" +
                "  \"feedback\": \"Una explicación breve, amigable y constructiva sobre cómo sonó y consejos de pronunciación si falló.\"\n" +
                "}"
            }
          ],
          response_format: {
            type: "json_object",
          },
        });

        const aiResult = JSON.parse(completion.choices[0].message.content);
        console.log("Resultado de evaluación:", aiResult);

        return {
          transcription: userText,
          isCorrect: aiResult.isCorrect === true || aiResult.isCorrect === "true",
          feedback: aiResult.feedback
        };
      } catch (error) {
        console.error("Critical Assessment Error:", error);
        throw new HttpsError(
            "internal",
            `Error al evaluar la pronunciación: ${error.message}`,
        );
      } finally {
        if (fs.existsSync(tempFilePath)) {
          await fs.remove(tempFilePath);
        }
      }
    },
);
