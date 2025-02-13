package app.web_gen

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.file.Files
import java.nio.file.Paths

@Service
class CodeIngestionService(
    private val openAiService: OpenAiService,
    private val codeRepository: CodeSnippetRepository
) {
    @Transactional
    fun ingestCode(directory: String) {
        Files.walk(Paths.get(directory))
            .filter { Files.isRegularFile(it) && it.toString().endsWith(".kt") }
            .forEach { file ->
                val content = Files.readString(file)
                val embedding = openAiService.generateEmbedding(content)

                val snippet = CodeSnippet(filename = file.fileName.toString(), content = content, embedding = embedding)
                codeRepository.save(snippet)
            }
    }
}
