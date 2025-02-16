package app.web_gen.response

data class ModelResponse(
    val textResponse: String,
    var newFiles: MutableList<FileContent>,
    var modifiedFiles: MutableList<ModifiedFileContent>,
    )

data class FileContent(
    val path: String,
    val content: String,
)

data class ModifiedFileContent(
    val path: String,
    val oldContent: String,
    val newContent: String,

)