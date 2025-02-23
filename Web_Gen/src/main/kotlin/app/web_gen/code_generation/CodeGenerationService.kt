package app.web_gen.code_generation

import app.web_gen.code_generation.response.ModelResponse
import app.web_gen.code_snippet.CodeSnippet
import app.web_gen.code_snippet.CodeSnippetRepository
import app.web_gen.project.GeneratedProject
import app.web_gen.project.GeneratedProjectRepository
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
) {
    private val baseFilePath: String =  //Path(System.getProperty("user.dir"), "src/main/resources/generated").toString()
        Path("D:\\Web_Gen_Projects").toString()

    fun applyChanges(filePath: String, oldCode: String, newCode: String) {
        val path = Paths.get(filePath)
        val content = Files.readString(path)
        val updatedContent = content.replace(oldCode, newCode)
        Files.writeString(path, updatedContent)
    }

    fun generateAllFiles(modelResponse: ModelResponse) {
        this.generateFiles(modelResponse)
        //TODO modified  files
        //this.runApplication(modelResponse)
    }


    private fun generateFiles(modelResponse: ModelResponse) {

        var project = GeneratedProject(
            name = modelResponse.projectName
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
        commands = commands.map { it.replace("npx", "C:\\Program Files\\nodejs\\npx.cmd") }
        commands = commands.map { it.replace("npm", "C:\\Program Files\\nodejs\\npm.cmd") }
        val codeGeneration = ProcessBuilder()
            .command(commands)
            .directory(File(this.baseFilePath)).inheritIO()
        codeGeneration.start().waitFor()
    }

    private fun createGeneratedFiles(modelResponse: ModelResponse):List<CodeSnippet> {
        val projectPath = Path(this.baseFilePath,modelResponse.projectName).pathString
        var writer: PrintWriter
        val createdSnippets =  mutableListOf<CodeSnippet>()
        for (newFile in modelResponse.newFiles) {

            val file = File(Path(projectPath, newFile.path).toString())
            val parent = File(file.parent)
            val codeSnippet = CodeSnippet(
                file.name, newFile.content,
                openAiService.generateEmbedding(file.name, newFile.content),
            )
            createdSnippets.add(codeSnippetRepository.save(codeSnippet))

            if (!file.exists()) {
                parent.mkdirs()
                file.createNewFile()
                writer = PrintWriter(file)
                writer.println(newFile.content)
                writer.close()
            }
        }
        return createdSnippets
    }

    private fun runApplication(modelResponse: ModelResponse) {
        val projectPath = Path(this.baseFilePath,modelResponse.projectName).pathString
        //Run App
        println("Starting")

        var runCommands = modelResponse.codeToRun.split(" ")
        runCommands = runCommands.map { it.replace("npm", "C:\\Program Files\\nodejs\\npm.cmd") }
        println(runCommands)
        val runnable = ProcessBuilder()
            .command(runCommands)
            .directory(File(projectPath)).inheritIO()
        runnable.start().waitFor()
        println("Finished")
    }
}
