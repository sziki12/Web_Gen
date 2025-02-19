package app.web_gen.code_generation

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Service
class CodeIngestionService(
    private val openAiService: OpenAiService,
    private val codeRepository: CodeSnippetRepository
) {

    fun checkAcceptedFiles(files: Path): Boolean {
        return files.let {
            it.toString().endsWith(".js") ||
                    it.toString().endsWith(".jsx") ||
                    it.toString().endsWith(".ts") ||
                    it.toString().endsWith(".tsx")

        }
    }

    @Transactional
    fun ingestCode(directory: String) {
        Files.walk(Paths.get(directory))
            .filter { Files.isRegularFile(it) && checkAcceptedFiles(it) }
            .forEach { file ->
                val content = Files.readString(file)
                val embedding = openAiService.generateEmbedding(file.fileName.toString(), content)

                val snippet = CodeSnippet(
                    filename = file.fileName.toString(),
                    content = content,
                    embedding = embedding.toString()
                )
                codeRepository.save(snippet)
            }
    }
}
