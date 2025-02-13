package app.web_gen

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class OpenAiService {

    private val apiKey = "your-openai-api-key"
    private val endpoint = "https://api.openai.com/v1/embeddings"
    private val restTemplate = RestTemplate()

    fun modifyCode(query: String, relevantCode: List<CodeSnippet>): String {
        val prompt = """
            You are an expert Kotlin developer. Modify the following code based on this request: $query.
            Make only necessary changes and return the updated code.

            Relevant code:
            ${relevantCode.joinToString("\n\n") { "File: ${it.filename}\n${it.content}" }}
        """.trimIndent()

        val response = generateCompletion(prompt)
        return response
    }

    fun generateCompletion(prompt: String): String {


        val requestBody = mapOf(
            "model" to "gpt-4",
            "messages" to listOf(
                mapOf("role" to "system", "content" to "You are an expert Kotlin developer."),
                mapOf("role" to "user", "content" to prompt)
            ),
            "temperature" to 0.7
        )

        val request = HttpEntity(requestBody, getHeaders())
        val response = restTemplate.exchange(endpoint, HttpMethod.POST, request, String::class.java)

        val jsonNode: JsonNode = jacksonObjectMapper().readTree(response.body)
        return jsonNode["choices"][0]["message"]["content"].asText()
    }

    fun generateEmbedding(content: String): String {

        val requestBody = mapOf(
            "input" to content,
            "model" to "text-embedding-ada-002"
        )

        val request = HttpEntity(requestBody, getHeaders())
        val response = restTemplate.exchange(endpoint, HttpMethod.POST, request, String::class.java)

        val jsonNode: JsonNode = jacksonObjectMapper().readTree(response.body)
        val embeddingArray = jsonNode["data"][0]["embedding"]

        return embeddingArray.toString() // Convert to string for database storage
    }

    fun getHeaders(): HttpHeaders {
        return HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $apiKey")
        }
    }
}
