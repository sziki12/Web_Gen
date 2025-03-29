package app.web_gen.code_running

import app.web_gen.code_generation.CommandSubstitutionService
import app.web_gen.code_generation.OpenAiService
import app.web_gen.code_generation.enumeration.ProjectStatus
import app.web_gen.code_snippet.CodeSnippetRepository
import app.web_gen.project.GeneratedProject
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
    private val runningProcesses = mutableMapOf<Long, Process>()

    fun runApplication(project: GeneratedProject) {
        val projectPath = projectPathResolver.getProjectPath(project.name)
        println(projectPath)
        //Run App
        println("Starting")

        var runCommands = project.codeToRun.split(" ")
        runCommands = commandSubstitutionService.substituteCommands(runCommands)

        println(runCommands)

        val runnable = ProcessBuilder()
            .command(runCommands)
            .directory(File(projectPath)).inheritIO()
        project.id?.let {
            val process = runnable.start()
            runningProcesses[it] = process
            println("${project.id} Started")
            generatedProjectRepository.save(project.also {
                it.status = ProjectStatus.Started
            })
        }
    }

    fun terminateApplication(project: GeneratedProject) {
        val process = runningProcesses[project.id]
        process?.let { parent ->
            parent.descendants().forEach { descendant -> descendant.destroy() }
            parent.destroy()
            println("Destroyed")
            generatedProjectRepository.save(project.also {
                it.status = ProjectStatus.Stopped
            })
        }
    }

    fun terminateAllApplication() {
        for (process in runningProcesses) {
            try {
                val project = generatedProjectRepository.findById(process.key).get()
                terminateApplication(project)
            }
            catch (e:Exception)
            {
                println("Failed to terminate id: ${process.key} project.")
                e.printStackTrace()
            }

        }
    }
}