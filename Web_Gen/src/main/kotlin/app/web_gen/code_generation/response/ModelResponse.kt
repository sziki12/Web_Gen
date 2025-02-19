package app.web_gen.code_generation.response

data class ModelResponse(
    val textResponse: String,
    var newFiles: MutableList<FileContent>,
    var modifiedFiles: MutableList<ModifiedFileContent>,
    )
{
    companion object{
        val responseFormat =
            """
{
  "type": "object",
  "properties": {
    "textResponse": {
      "type": "string"
    },
    "newFiles": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "path": { "type": "string" },
          "content": { "type": "string" }
        },
        "required": ["path", "content"],
        "additionalProperties": false
      }
    },
    "modifiedFiles": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "path": { "type": "string" },
          "oldContent": { "type": "string" },
          "newContent": { "type": "string" }
        },
        "required": ["path", "oldContent", "newContent"],
        "additionalProperties": false
      }
    }
  },
  "required": ["textResponse", "newFiles", "modifiedFiles"],
  "additionalProperties": false
}
        """
    }
}

data class FileContent(
    val path: String,
    val content: String,
)

data class ModifiedFileContent(
    val path: String,
    val oldContent: String,
    val newContent: String,

)