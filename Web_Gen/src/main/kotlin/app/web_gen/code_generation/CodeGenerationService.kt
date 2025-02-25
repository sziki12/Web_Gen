package app.web_gen.code_generation

import app.web_gen.code_generation.response.ModelResponse
import app.web_gen.code_snippet.CodeSnippet
import app.web_gen.code_snippet.CodeSnippetRepository
import app.web_gen.project.GeneratedProject
import app.web_gen.project.GeneratedProjectRepository
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

    private val runningProcesses = mutableMapOf<String,Process>()
    fun applyChanges(project:GeneratedProject, oldSnippet:CodeSnippet,replacedCode:String, newCode: String) {
        val path = Path(baseFilePath,project.name,oldSnippet.relativePath)
        val updatedContent = oldSnippet.content.replace(replacedCode, newCode)
        //TODO Update Snippet in DB
        codeSnippetRepository.save(oldSnippet)
        Files.writeString(path, updatedContent)
    }

    fun generateFiles(modelResponse: ModelResponse) {

        var project = GeneratedProject(
            name = modelResponse.projectName,
            codeToGenerate = modelResponse.codeToGenerate,
            codeToRun = modelResponse.codeToRun
        )
        project = generatedProjectRepository.save(project)

        println("Generating")
        println(modelResponse.codeToGenerate)

        runGenerationCommand(modelResponse)

        println("Completed")

        project.snippets.addAll(createGeneratedFiles(modelResponse))
        generatedProjectRepository.save(project)
    }

    private fun runGenerationCommand(modelResponse: ModelResponse) {
        var commands = modelResponse.codeToGenerate.split(" ")
        commands = commandSubstitutionService.substituteCommands(commands)
        val codeGeneration = ProcessBuilder()
            .command(commands)
            .directory(File(this.baseFilePath)).inheritIO()
        codeGeneration.start().waitFor()
    }

    private fun createGeneratedFiles(modelResponse: ModelResponse): List<CodeSnippet> {
        val projectPath = Path(this.baseFilePath, modelResponse.projectName).pathString
        var writer: PrintWriter
        val createdSnippets = mutableListOf<CodeSnippet>()
        for (newFile in modelResponse.newFiles) {

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
        val project = generatedProjectRepository.findByName(projectName)
        project?.let {
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
    }

    fun terminateApplication(projectName: String){
        val process = runningProcesses[projectName]
        process?.let { parent ->
            parent.descendants().forEach{descendant->descendant.destroy()}
            parent.destroy()
            println("Destroyed")
        }
    }
}
