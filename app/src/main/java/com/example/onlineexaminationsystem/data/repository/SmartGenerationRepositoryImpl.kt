package com.example.onlineexaminationsystem.data.repository

import android.util.Log
import com.example.onlineexaminationsystem.data.remote.QuestionDto
import com.example.onlineexaminationsystem.domain.repository.SmartGenerationRepository
import com.example.onlineexaminationsystem.domain.util.Resource // Assuming you use a state wrapper
import com.google.ai.client.generativeai.GenerativeModel
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SmartGenerationRepositoryImpl @Inject constructor(
    private val generativeModel: GenerativeModel,
    private val gson: Gson
) : SmartGenerationRepository {


    override suspend fun generateQuestionsForTopic(topic: String): Resource<List<QuestionDto>> {

        if (topic.trim().length < 2) {
            return Resource.Error("Topic is too short to generate questions.")
        }

        val prompt = """
            You are an expert educational examiner. The user has requested questions for the topic: "$topic".
            First, evaluate if this topic is a valid, logical subject for an academic or professional examination. 
            If the topic is nonsensical, gibberish, offensive, or inappropriate, YOU MUST STRICTLY return an empty JSON array: []
            
            If it IS a valid topic, generate 5 multiple-choice questions at a medium level.
            Return the result STRICTLY as a JSON array of objects.
            Each object MUST have the following exact keys:
            - 'text' (string)
            - 'options' (array of exactly 4 strings)
            - 'correctAnswer' (integer, the 0-based index of the correct option in the 'options' array)
            - 'mark' (integer, assign a default value of 1)
            
            Do not include any markdown formatting, backticks, or extra text. Output ONLY valid JSON.
        """.trimIndent()


        return withContext(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(prompt)
                val responseText = response.text ?: "[]"


                val cleanJson = responseText
                    .replace("```json", "")
                    .replace("```", "")
                    .trim()

                val listType = object : TypeToken<List<QuestionDto>>() {}.type
                val questionsDto: List<QuestionDto> = gson.fromJson(cleanJson, listType)

                // Handle the LLM's evaluation of the topic
                if (questionsDto.isEmpty()) {
                    Resource.Error("The provided topic is invalid or not recognized as an educational subject.")
                } else {
                    Resource.Success(questionsDto)
                }

            } catch (e: JsonSyntaxException) {
                Log.e("SmartGenRepo", "Failed to parse JSON from AI response", e)
                Resource.Error("Received malformed data from the AI.", e)
            } catch (e: Exception) {
                Log.e("SmartGenRepo", "Failed to generate questions", e)
                Resource.Error("A network or generation error occurred.", e)
            }
        }
    }
}