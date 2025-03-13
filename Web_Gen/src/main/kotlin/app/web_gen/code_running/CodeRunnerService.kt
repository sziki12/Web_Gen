package app.web_gen.code_running

import app.web_gen.code_generation.CommandSubstitutionService
import app.web_gen.code_generation.OpenAiService
import app.web_gen.code_snippet.CodeSnippetRepository
import app.web_gen.project.GeneratedProjectRepository
import app.web_gen.project.ProjectPathResolver
import org.springframework.stereotype.Service
import java.io.File


@Service
class CodeRunnerService(
        private val generatedProjectRepository: GeneratedProjectRepository,
        private val commandSubstitutionService: CommandSubstitutionService,
        private val projectPathResolver: ProjectPathResolver,
) {
    private val runningProcesses = mutableMapOf<String, Process>()

    fun runApplication(projectName: String) {
        val project = generatedProjectRepository.findByName(projectName).get()
        val projectPath = projectPathResolver.getProjectPath(projectName)
        println(projectPath)
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