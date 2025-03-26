package app.web_gen.code_generation

import app.web_gen.code_generation.response.ProjectCreationResponse
import app.web_gen.code_generation.response.ProjectModificationResponse
import app.web_gen.code_running.CodeRunnerService
import app.web_gen.code_snippet.CodeSnippetRepository
import app.web_gen.project.GeneratedProjectRepository
import app.web_gen.project.ProjectPathResolver
import app.web_gen.security.AuthService
import app.web_gen.test_response.testGenerateResponse
import app.web_gen.test_response.testModifyResponse
import app.web_gen.zip.ZipDirectory
import com.google.gson.Gson
import org.apache.tomcat.util.http.fileupload.IOUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.ZipOutputStream


@RestController
@RequestMapping("/api/code")
class CodeGenerationController(
    private val codeRepository: CodeSnippetRepository,
    private val projectRepository: GeneratedProjectRepository,
    private val codeGenerationService: CodeGenerationService,
    private val codeRunnerService: CodeRunnerService,
    private val openAiService: OpenAiService,
    private val projectPathResolver: ProjectPathResolver,
    private val zipDirectory: ZipDirectory,
    private val authService: AuthService,
) {
    private val gson: Gson = Gson().newBuilder().create()

    @Value("\${spring.ai.openai.use-test-responses}")
    var useTestResponses: Boolean = true


    @PostMapping("/{projectId}/modify")
    fun modifyCode(
        @RequestParam query: String,
        @PathVariable projectId: Long
    ): ResponseEntity<ProjectModificationResponse> {

        try {
            val currentUserId = authService.getCurrentUser().id ?: -1
            val project = projectRepository.findByIdAndUserId(projectId, currentUserId).get()
            val queryVector = openAiService.generateEmbedding(query)
            val relevantSnippets = codeRepository.findRelevantSnippets(projectId, queryVector, 3)
            val modifiedCode = if (useTestResponses) {
                gson.fromJson(testModifyResponse, ProjectModificationResponse::class.java)
            } else {
                openAiService.modifyCode(query, relevantSnippets)
            }
            codeGenerationService.updateProjectFiles(project, modifiedCode, relevantSnippets)
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

    @GetMapping("/{projectId}/query")
    fun findRelevantSnippets(
        @RequestParam query: String,
        @PathVariable projectId: Long
    ): ResponseEntity<List<String>> {
        val transformedQuery = openAiService.generateEmbedding(query)
        val relevantSnippets = codeRepository.findRelevantSnippets(projectId, transformedQuery, limit = 2)
        return ResponseEntity.ok(relevantSnippets.map { it.filename })
    }

    @PostMapping("/{projectId}/start")
    fun startProject(@PathVariable projectId: Long): ResponseEntity<String> {
        try {
            val currentUserId = authService.getCurrentUser().id ?: -1
            val project = projectRepository.findByIdAndUserId(projectId, currentUserId).get()
            codeRunnerService.runApplication(project)
            return ResponseEntity.ok(project.name)
        } catch (e: Exception) {
            return ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{projectId}/terminate")
    fun terminateProject(@PathVariable projectId: Long): ResponseEntity<String> {
        try {
            val currentUserId = authService.getCurrentUser().id ?: -1
            val project = projectRepository.findByIdAndUserId(projectId, currentUserId).get()
            codeRunnerService.terminateApplication(project)
            return ResponseEntity.ok(project.name)
        } catch (e: Exception) {
            return ResponseEntity.notFound().build()
        }
    }

    @GetMapping(value = ["{projectId}/zip"], produces = ["application/zip"])
    fun downloadProject(@PathVariable projectId: Long): ResponseEntity<ByteArray> {
        val currentUserId = authService.getCurrentUser().id ?: -1
        val project = projectRepository.findByIdAndUserId(projectId, currentUserId).get()

        val byteArrayOutputStream = ByteArrayOutputStream()
        val bufferedOutputStream = BufferedOutputStream(byteArrayOutputStream)
        val zipOutputStream = ZipOutputStream(bufferedOutputStream)

        val file = File(projectPathResolver.getProjectPath(project.name))
        println("path: ${file.absolutePath}")
        zipDirectory.zip(file, project.name, zipOutputStream)

        IOUtils.closeQuietly(bufferedOutputStream)
        IOUtils.closeQuietly(byteArrayOutputStream)
        val response = ResponseEntity
            .ok()
            .header("Content-Disposition", "attachment; filename=\"files.zip\"")
            .body(byteArrayOutputStream.toByteArray())

        return response
    }
}
