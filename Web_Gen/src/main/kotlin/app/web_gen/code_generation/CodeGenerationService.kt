package app.web_gen.code_generation

import app.web_gen.code_generation.response.FileContent
import app.web_gen.code_generation.response.NewAndExistingFiles
import app.web_gen.code_generation.response.ProjectCreationResponse
import app.web_gen.code_generation.response.ProjectModificationResponse
import app.web_gen.code_snippet.CodeSnippet
import app.web_gen.code_snippet.CodeSnippetRepository
import app.web_gen.project.GeneratedProject
import app.web_gen.project.GeneratedProjectRepository
import okhttp3.internal.notifyAll
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.pathString

@Service
class CodeGenerationService(
    private val codeSnippetRepository: CodeSnippetRepository,
    private val generatedProjectRepository: GeneratedProjectRepository,
    private val openAiService: OpenAiService,
    private val commandSubstitutionService: CommandSubstitutionService
) {
    @Value(value = "\${generated.project.path}")
    private lateinit var baseFilePath: String

    private val runningProcesses = mutableMapOf<String, Process>()
    fun applyChanges(project: GeneratedProject, oldSnippet: CodeSnippet, replacedCode: String, newCode: String) {
        val path = Path(baseFilePath, project.name, oldSnippet.relativePath)
        val updatedContent = oldSnippet.content.replace(replacedCode, newCode)
        //TODO Update Snippet in DB
        codeSnippetRepository.save(oldSnippet)
        Files.writeString(path, updatedContent)
    }

    fun generateProjectFiles(projectName: String, creationResponse: ProjectCreationResponse) {

        var project = GeneratedProject(
            name = projectName,
            codeToGenerate = creationResponse.codeToGenerate,
            codeToRun = creationResponse.codeToRun
        )
        println("${project.name}\n---\n" +
                "${project.codeToGenerate}\n---\n" +
                project.codeToRun
        )
        project = generatedProjectRepository.save(project)


        println("Generating")
        println(creationResponse.codeToGenerate)

        runGenerationCommand(creationResponse.codeToGenerate)

        println("Completed")

        for(snippet in generateFiles(projectName,creationResponse.newFiles)){
            codeSnippetRepository.save(snippet.also { it.project = project  } )
        }
    }

    fun updateProjectFiles(
        projectName: String,
        modificationResponse: ProjectModificationResponse,
        relevantSnippets: List<CodeSnippet>
    ) {
        //Find project
        val project = generatedProjectRepository.findByName(projectName).get()
        //Modify existing files
        modificationResponse.modifiedFiles.forEach { modifiedFile ->
            val snippet = relevantSnippets.find { it.relativePath == modifiedFile.path }
            snippet?.let {
                println("Modified: ${it.relativePath}")
                this.applyChanges(project, snippet, modifiedFile.oldContent, modifiedFile.newContent)
            }
        }
        //Create new files if they to not exist
        val files = separateNewAndExistingFiles(project, modificationResponse)
        generateFiles(projectName, files.newFiles)
        //TODO Log Created files
        //TODO Return or request solution for conflicting files
        files.existingFiles
    }

    private fun runGenerationCommand(codeToGenerate: String) {
        var commands = codeToGenerate.split(" ")
        commands = commandSubstitutionService.substituteCommands(commands)
        val codeGeneration = ProcessBuilder()
            .command(commands)
            .directory(File(this.baseFilePath)).inheritIO()
        codeGeneration.start().waitFor()
    }

    private fun separateNewAndExistingFiles(
        project: GeneratedProject,
        modificationResponse: ProjectModificationResponse
    ): NewAndExistingFiles {
        val out = NewAndExistingFiles()
        for (potentialNewFile in modificationResponse.newFiles) {
            val isFileExists =
                project.id?.let { codeSnippetRepository.existsByProjectIdAndFilename(it, potentialNewFile.path) }
                    ?: throw NullPointerException("Project with name: ${project.name} is not saved yet")
            if (isFileExists) {
                out.existingFiles.add(potentialNewFile)
            } else {
                out.newFiles.add(potentialNewFile)
            }
        }
        return out
    }

    private fun generateFiles(projectName: String, newFiles: List<FileContent>): List<CodeSnippet> {
        val projectPath = Path(this.baseFilePath, projectName).pathString
        var writer: PrintWriter
        val createdSnippets = mutableListOf<CodeSnippet>()
        for (newFile in newFiles) {

            val file = File(Path(projectPath, newFile.path).toString())
            val parent = File(file.parent)
            val codeSnippet = CodeSnippet(
                file.name,
                newFile.path,
                newFile.content,
                openAiService.generateEmbedding(file.name, newFile.content),
            )
            createdSnippets.add(codeSnippetRepository.save(codeSnippet))

            if (!file.exists()) {
                parent.mkdirs()
            } else {
                file.delete()
            }
            file.createNewFile()
            writer = PrintWriter(file)
            writer.println(newFile.content)
            writer.close()
        }
        return createdSnippets
    }

    fun runApplication(projectName: String) {
        val project = generatedProjectRepository.findByName(projectName).get()
        val projectPath = Path(this.baseFilePath, project.name).pathString
        //Run App
        println("Starting")

        var runCommands = project.codeToRun.split(" ")
        runCommands = commandSubstitutionService.substituteCommands(runCommands)

        println(runCommands)

        val runnable = ProcessBuilder()
            .command(runCommands)
            .directory(File(projectPath)).inheritIO()
        val process = runnable.start()
        runningProcesses[projectName] = process
        println("Started")
    }

    fun terminateApplication(projectName: String) {
        val process = runningProcesses[projectName]
        process?.let { parent ->
            parent.descendants().forEach { descendant -> descendant.destroy() }
            parent.destroy()
            println("Destroyed")
        }
    }
}
