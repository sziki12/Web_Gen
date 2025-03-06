package app.web_gen.code_generation

import app.web_gen.code_generation.request.FileConflictResolverRequest
import app.web_gen.code_generation.response.FileContent
import app.web_gen.code_generation.response.NewAndExistingFiles
import app.web_gen.code_generation.response.ProjectCreationResponse
import app.web_gen.code_generation.response.ProjectModificationResponse
import app.web_gen.code_snippet.CodeSnippet
import app.web_gen.code_snippet.CodeSnippetRepository
import app.web_gen.project.GeneratedProject
import app.web_gen.project.GeneratedProjectRepository
import app.web_gen.project.ProjectPathResolver
import groovy.json.StringEscapeUtils
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
    private val commandSubstitutionService: CommandSubstitutionService,
    private val projectPathResolver: ProjectPathResolver,
) {


    private val runningProcesses = mutableMapOf<String, Process>()
    fun applyChanges(
        project: GeneratedProject,
        oldSnippet: CodeSnippet,
        unescapedReplacedCode: String,
        newCode: String
    ) {
        val path = Path(projectPathResolver.getUserFolderPath(), oldSnippet.relativePath)
        val escapedReplacedCode = StringEscapeUtils.unescapeJava(unescapedReplacedCode)

        var updatedContent = oldSnippet.content.replace(escapedReplacedCode, newCode)
        //If replace fails, replace the whole file
        if (updatedContent == oldSnippet.content) {
            updatedContent = newCode
        }

        val updated = codeSnippetRepository.save(oldSnippet.apply {
            this.content = updatedContent
            this.embedding = openAiService.generateEmbedding(oldSnippet.filename, updatedContent)
        })
        //TODO Check why modification fails
        //println("ESCAPED: ${updated.relativePath}\n${escapedReplacedCode}\n\n")
        //println("NEW: ${updated.relativePath}\n${newCode}\n\n")
        //println("UPDATED: ${updated.relativePath}\n${updated.content}\n\n")
        Files.writeString(path, updatedContent)
    }

    fun generateProjectFiles(projectName: String, creationResponse: ProjectCreationResponse) {

        var project = GeneratedProject(
            name = projectName,
            codeToGenerateFiles = creationResponse.codeToGenerateFiles,
            codeToInstallPackages = creationResponse.codeToInstallPackages,
            codeToRun = creationResponse.codeToRun
        )
        println(
            "${project.name}\n---\n" +
                    "codeToGenerateFiles\n${project.codeToGenerateFiles}\n---\n" +
                    "codeToInstallPackages\n${project.codeToInstallPackages}\n---\n" +
                    "codeToRun\n${project.codeToRun}"
        )
        project = generatedProjectRepository.save(project)


        println("Generating files")
        println(creationResponse.codeToGenerateFiles)
        runGenerationCommand(
            projectPathResolver.getUserFolderPath(),
            creationResponse.codeToGenerateFiles,
            true
        )

        println("Installing packages")
        println(creationResponse.codeToInstallPackages)
        runGenerationCommand(
            projectPathResolver.getProjectPath(projectName),
            creationResponse.codeToInstallPackages,
            false
        )

        println("Completed")

        generateFiles(project, creationResponse.newFiles)
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
        println("files:\n$files\n\n")
        generateFiles(project, files.newFiles)
        //TODO Log Created files

        if (files.existingFiles.isNotEmpty()) {
            val oldFiles = project.id?.let { id ->
                codeSnippetRepository.findByProjectIdAndRelativePathIn(
                    id,
                    files.existingFiles.map { file -> file.path })
            } ?: mutableListOf()
            val conflictRequest = FileConflictResolverRequest(
                oldFiles.map { FileContent(it.relativePath, it.content) }.toMutableList(),
                files.existingFiles
            )
            println("conflictRequest:\n$conflictRequest\n\n")
            val conflictResolveResponse =
                openAiService.resolveFileConflict(modificationResponse.textResponse, conflictRequest)
            println("conflictResolveResponse:\n$conflictResolveResponse\n\n")
            //TODO Check the file.path, it doesn't contains the projectName
            conflictResolveResponse.modifiedFiles.forEach { file ->
                val snippet = oldFiles.singleOrNull { it.relativePath == file.path }
                if (snippet != null) {
                    applyChanges(project, snippet, file.oldContent, file.newContent)
                } else {
                    println("No existing Snippet for ${file.path}")
                }
            }
        }
        try {
            runGenerationCommand(
                projectPathResolver.getProjectPath(projectName),
                modificationResponse.codeToGenerate,
                false
            )
        } catch (e: Exception) {
            println("FAILED  to run GENERATION Command:\n${modificationResponse.codeToGenerate}\n${e.message}")
        }
    }

    private fun runGenerationCommand(projectPath: String, codeToGenerate: String, requiresCmd: Boolean) {
        var commands = if (requiresCmd)
            listOf("cmd", "/C", *codeToGenerate.split(" ").toTypedArray())
        else
            codeToGenerate.split(" ")

        commands = commandSubstitutionService.substituteCommands(commands)
        println(commands)
        val codeGeneration = ProcessBuilder()
            .command(commands)
            .directory(File(projectPath)).inheritIO()
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

    private fun generateFiles(project: GeneratedProject, newFiles: List<FileContent>): List<CodeSnippet> {
        val userFolderPath = projectPathResolver.getUserFolderPath()
        var writer: PrintWriter
        val createdSnippets = mutableListOf<CodeSnippet>()
        for (newFile in newFiles) {

            val file = File(Path(userFolderPath, newFile.path).toString())
            val parent = File(file.parent)
            val codeSnippet = CodeSnippet(
                file.name,
                newFile.path,
                newFile.content,
                openAiService.generateEmbedding(file.name, newFile.content),
            ).also {
                it.project = project
            }
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
