package app.web_gen

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/code")
class CodeModificationController(
    private val codeRepository: CodeSnippetRepository,
    private val openAiService: OpenAiService,
    private val modificationService: CodeModificationService
) {
    @PostMapping("/modify")
    fun modifyCode(@RequestParam query: String): String {
        val queryVector = openAiService.generateEmbedding(query)
        val relevantSnippets = codeRepository.findRelevantSnippets(queryVector, 3)

        val modifiedCode = openAiService.modifyCode(query, relevantSnippets)

        relevantSnippets.forEach { snippet ->
            modificationService.applyChanges("path/to/codebase/${snippet.filename}", snippet.content, modifiedCode)
        }

        return "Code modified successfully!"
    }
}
