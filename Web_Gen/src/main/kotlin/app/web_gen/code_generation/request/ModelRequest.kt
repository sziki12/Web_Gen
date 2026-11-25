package app.web_gen.code_generation.request

import app.web_gen.code_generation.response.FileContent

data class FileConflictResolverRequest(
    var alreadyExistingFiles: MutableList<FileContent>,
    var newlyGeneratedFiles: MutableList<FileContent>,
    //val oldCodeToGenerate: String,
    //val oldCodeToRun: String,
)

data class ProjectGenerationRequest(
    var projectName: String,
    var threadId: String,
    var prompt: String
)
{
    fun toMap():Map<String,String> = mapOf("project_name" to projectName, "thread_id" to threadId, "prompt" to prompt )
}

//Command error resolver
//Given command
//Error message

//Response
//Correct command

//Code error resolver
//Code error message
//Relevant content

//Response
//Command to run
//Modified files