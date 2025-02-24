package app.web_gen.code_generation.response

data class ModelResponse(
    val projectName: String,
    val textResponse: String,
    val codeToGenerate: String,
    val codeToRun: String,
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
    "projectName": {
      "type": "string",
      "description":"The name of the project in kebab case."
    },
    "codeToGenerate": {
      "type": "string",
      "description":"Code to generate the missing project files and make the project runnable without starting it. Only contains runnable code."
    },
    "codeToRun": {
      "type": "string",
      "description":"Code to run the application without the directory change. Only contains runnable code."
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
  "required": ["textResponse", "projectName", "codeToRun", "codeToGenerate", "newFiles", "modifiedFiles"],
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