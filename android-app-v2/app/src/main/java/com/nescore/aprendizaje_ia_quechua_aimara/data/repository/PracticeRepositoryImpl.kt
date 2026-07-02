package com.nescore.aprendizaje_ia_quechua_aimara.data.repository

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.Achievement
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.Exam
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.Question
import com.nescore.aprendizaje_ia_quechua_aimara.domain.repository.PracticeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class PracticeRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: FirebaseDatabase
) : PracticeRepository {

    override suspend fun getExamsByLevel(language: String, level: String): List<Exam> {
        val levelKey = mapLevelToKey(level)
        val langKey = language.lowercase()

        try {
            val snapshot = database.getReference("practicas")
                .child(langKey)
                .child(levelKey)
                .get()
                .await()

            if (snapshot.exists()) {
                val exams = mutableListOf<Exam>()
                snapshot.children.forEach { examSnap ->
                    val exam = parseExamFromSnapshot(examSnap, langKey, levelKey)
                    if (exam != null) {
                        exams.add(exam)
                    }
                }
                if (exams.isNotEmpty()) {
                    return exams
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return loadExamsFromAssets(langKey, levelKey)
    }

    override suspend fun getExamByTitle(language: String, level: String, title: String): Exam? {
        val levelKey = mapLevelToKey(level)
        val langKey = language.lowercase()

        try {
            val safeTitle = title
                .replace(".", "_")
                .replace("#", "_")
                .replace("$", "_")
                .replace("[", "_")
                .replace("]", "_")
                .replace("/", "_")

            val snapshot = database.getReference("practicas")
                .child(langKey)
                .child(levelKey)
                .child(safeTitle)
                .get()
                .await()

            if (snapshot.exists()) {
                val exam = parseExamFromSnapshot(snapshot, langKey, levelKey)
                if (exam != null) {
                    return exam
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return loadExamsFromAssets(langKey, levelKey).find { it.examTitle == title }
    }

    private fun parseExamFromSnapshot(snapshot: com.google.firebase.database.DataSnapshot, language: String, level: String): Exam? {
        try {
            val examTitle = snapshot.child("examTitle").value as? String ?: return null
            val description = snapshot.child("description").value as? String ?: ""
            
            val questions = mutableListOf<Question>()
            snapshot.child("questions").children.forEach { qSnap ->
                val questionText = qSnap.child("question").value as? String ?: ""
                val correctAnswer = qSnap.child("correctAnswer").value as? String ?: ""
                val explanation = qSnap.child("explanation").value as? String ?: ""
                
                val options = mutableListOf<String>()
                qSnap.child("options").children.forEach { optSnap ->
                    val opt = optSnap.value as? String
                    if (opt != null) {
                        options.add(opt)
                    }
                }
                questions.add(Question(questionText, options, correctAnswer, explanation))
            }
            
            val achSnap = snapshot.child("achievement")
            val achName = achSnap.child("name").value as? String ?: "Logro"
            val achDesc = achSnap.child("description").value as? String ?: "Completaste el examen"
            val achShare = achSnap.child("shareMessage").value as? String ?: "¡Logré superar el examen!"
            val achievement = Achievement(achName, achDesc, achShare)
            
            return Exam(language, level, examTitle, description, questions, achievement)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun mapLevelToKey(level: String): String {
        return when (level.lowercase()) {
            "fácil", "easy" -> "easy"
            "normal", "intermedio", "intermediate" -> "intermediate"
            "difícil", "hard" -> "hard"
            else -> level.lowercase()
        }
    }

    private fun loadExamsFromAssets(language: String, level: String): List<Exam> {
        val exams = mutableListOf<Exam>()
        try {
            val fileName = "${language}_$level.json"
            val jsonString = context.assets.open("exams/$fileName").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                
                val questionsArray = jsonObject.getJSONArray("questions")
                val questions = mutableListOf<Question>()
                for (j in 0 until questionsArray.length()) {
                    val qObj = questionsArray.getJSONObject(j)
                    val optionsArray = qObj.getJSONArray("options")
                    val options = mutableListOf<String>()
                    for (k in 0 until optionsArray.length()) {
                        options.add(optionsArray.getString(k))
                    }
                    questions.add(
                        Question(
                            question = qObj.getString("question"),
                            options = options,
                            correctAnswer = qObj.getString("correctAnswer"),
                            explanation = qObj.optString("explanation", "")
                        )
                    )
                }
                
                val achObj = jsonObject.getJSONObject("achievement")
                val achievement = Achievement(
                    name = achObj.getString("name"),
                    description = achObj.getString("description"),
                    shareMessage = achObj.getString("shareMessage")
                )
                
                exams.add(
                    Exam(
                        language = jsonObject.getString("language"),
                        level = jsonObject.getString("level"),
                        examTitle = jsonObject.getString("examTitle"),
                        description = jsonObject.optString("description", ""),
                        questions = questions,
                        achievement = achievement
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return exams
    }
}
