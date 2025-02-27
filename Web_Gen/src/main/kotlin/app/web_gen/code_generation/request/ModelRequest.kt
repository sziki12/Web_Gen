package app.web_gen.code_generation.request

import app.web_gen.code_generation.response.FileContent

data class FileConflictResolverRequest(
    var alreadyExistingFiles: MutableList<FileContent>,
    val oldCodeToGenerate: String,
    val oldCodeToRun: String,
)

data class ProjectGenerationRequest(
    var projectName: String,
)