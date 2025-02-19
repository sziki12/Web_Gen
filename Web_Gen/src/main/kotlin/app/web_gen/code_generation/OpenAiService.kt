package app.web_gen.code_generation

import app.web_gen.code_generation.response.ModelResponse
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.embedding.EmbeddingResponse
import org.springframework.ai.embedding.EmbeddingRequest
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.ai.openai.OpenAiEmbeddingModel
import org.springframework.ai.openai.OpenAiEmbeddingOptions
import org.springframework.ai.openai.api.OpenAiApi
import org.springframework.ai.openai.api.ResponseFormat
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service


@Service
class OpenAiService(
    @Value("\${spring.ai.openai.api-key}")
    val apiKey: String
) {

    val chatModel = OpenAiChatModel(OpenAiApi(apiKey), OpenAiChatOptions().apply {
        this.model = "gpt-4o"
        this.responseFormat = ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, ModelResponse.responseFormat)
    })
    val embeddingModel = OpenAiEmbeddingModel(OpenAiApi(apiKey))
    fun modifyCode(query: String, relevantCode: List<CodeSnippet>): String {
        val prompt = """
            You are an expert developer. Modify the following code based on this request: $query.
            Make only necessary changes and return the updated code.

            Relevant code:
            ${relevantCode.joinToString("\n\n") { "File: ${it.filename}\n${it.content}" }}
        """.trimIndent()

        val response = generateCompletion(prompt)
        return response
    }

    fun generateCompletion(prompt: String): String {
        println("Call")
        val response: ChatResponse = chatModel.call(
            Prompt(
                listOf(
                    SystemMessage("You are an expert web app developer. Create or modify an application based on given input."),
                    UserMessage(prompt)
                ),
            )
        )
        //TODO Generate cmd commands to create missing files, and run the application.
        println("Response")
        return response.result.output.text
    }

    fun generateEmbedding(vararg query: String): FloatArray {
        val embeddingResponse: EmbeddingResponse = embeddingModel.call(
            EmbeddingRequest(
                listOf(*query),
                OpenAiEmbeddingOptions.builder().build()
            )
        )
        return embeddingResponse.result.output
    }
}
