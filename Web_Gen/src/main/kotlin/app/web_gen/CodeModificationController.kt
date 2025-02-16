package app.web_gen

import app.web_gen.response.ModelResponse
import com.google.gson.Gson
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/code")
class CodeModificationController(
    private val codeRepository: CodeSnippetRepository,
    private val openAiService: OpenAiService,
    private val modificationService: CodeModificationService
) {
    val gson = Gson().newBuilder().create()

    @PostMapping("/modify")
    fun modifyCode(@RequestParam query: String): ResponseEntity<String> {
        val queryVector = openAiService.generateEmbedding(query)
        val relevantSnippets = codeRepository.findRelevantSnippets(queryVector.toString(), 3)

        val modifiedCode = openAiService.modifyCode(query, relevantSnippets)

        relevantSnippets.forEach { snippet ->
            modificationService.applyChanges("path/to/codebase/${snippet.filename}", snippet.content, modifiedCode)
        }

        return ResponseEntity.ok("Code modified successfully!")
    }

    @PostMapping("/generate")
    fun generateCode(@RequestParam prompt: String): ResponseEntity<String> {

        val response = openAiService.generateCompletion(prompt)
        val modelResponse = gson.fromJson(response, ModelResponse::class.java)


        return ResponseEntity.ok(response)

    }
}
