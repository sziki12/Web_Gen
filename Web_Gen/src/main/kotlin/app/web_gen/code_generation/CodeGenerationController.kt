package app.web_gen.code_generation

import app.web_gen.code_generation.response.ProjectCreationResponse
import app.web_gen.code_generation.response.ProjectModificationResponse
import app.web_gen.code_running.CodeRunnerService
import app.web_gen.code_snippet.CodeSnippetRepository
import app.web_gen.project.GeneratedProjectRepository
import app.web_gen.test_response.testGenerateResponse
import app.web_gen.test_response.testModifyResponse
import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlin.properties.Delegates

@RestController
@RequestMapping("/api/code")
class CodeGenerationController(
        private val codeRepository: CodeSnippetRepository,
        private val projectRepository: GeneratedProjectRepository,
        private val codeGenerationService: CodeGenerationService,
        private val codeRunnerService: CodeRunnerService,
        private val openAiService: OpenAiService,
) {
    private val gson: Gson = Gson().newBuilder().create()

    @Value("\${spring.ai.openai.use-test-responses}")
    var useTestResponses:Boolean = true


    @PostMapping("/{projectName}/modify")
    fun modifyCode(
            @RequestParam query: String,
            @PathVariable projectName: String
    ): ResponseEntity<ProjectModificationResponse> {

        try {
            val queryVector = openAiService.generateEmbedding(query)
            val relevantSnippets = codeRepository.findRelevantSnippets(projectName, queryVector, 3)
            val modifiedCode = if (useTestResponses) {
                gson.fromJson(testModifyResponse, ProjectModificationResponse::class.java)
            } else {
                openAiService.modifyCode(query, relevantSnippets)
            }
            codeGenerationService.updateProjectFiles(projectName, modifiedCode, relevantSnippets)
            return ResponseEntity.ok(modifiedCode)
        } catch (e: Exception) {
            return ResponseEntity.notFound().build()
        }
    }

    @PostMapping("{projectName}/generate")
    fun generateCode(
            @RequestParam prompt: String,
            @PathVariable projectName: String
    ): ResponseEntity<ProjectCreationResponse> {
        val generatedCode = if (useTestResponses) {
            gson.fromJson(testGenerateResponse, ProjectCreationResponse::class.java)
        } else {
            openAiService.generateProject(projectName, prompt)
        }
        codeGenerationService.generateProjectFiles(projectName, generatedCode)
        return ResponseEntity.ok(generatedCode)

    }

    @GetMapping("/{projectName}/query")
    fun findRelevantSnippets(
            @RequestParam query: String,
            @PathVariable projectName: String
    ): ResponseEntity<List<String>> {
        val transformedQuery = openAiService.generateEmbedding(query)
        val relevantSnippets = codeRepository.findRelevantSnippets(projectName, transformedQuery, limit = 2)
        return ResponseEntity.ok(relevantSnippets.map { it.filename })
    }

    @PostMapping("/{projectName}/start")
    fun startProject(@PathVariable projectName: String): ResponseEntity<String> {
        try {
            projectRepository.findByName(projectName).get()
            codeRunnerService.runApplication(projectName)
            return ResponseEntity.ok(projectName)
        } catch (e: Exception) {
            return ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{projectName}/terminate")
    fun terminateProject(@PathVariable projectName: String): ResponseEntity<String> {
        try {
            projectRepository.findByName(projectName).get()
            codeRunnerService.terminateApplication(projectName)
            return ResponseEntity.ok(projectName)
        } catch (e: Exception) {
            return ResponseEntity.notFound().build()
        }
    }
}
