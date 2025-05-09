package app.web_gen.code_generation.response

import app.web_gen.code_snippet.CodeSnippet

data class ProjectModificationResponse(
    val textResponse: String,
    val codeToGenerate: String,
    var newFiles: MutableList<FileContent>,
    var modifiedFiles: MutableList<ModifiedFileContent>,
) {
    companion object {
        val responseFormat =
            """
{
  "type": "object",
  "properties": {
    "textResponse": {
      "type": "string"
    },
    "codeToGenerate": {
      "type": "string",
      "description":"Code to make the modifications usable. Especially npm package installation. Only contains runnable code."
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
  "required": ["textResponse", "codeToGenerate", "newFiles", "modifiedFiles"],
  "additionalProperties": false
}
        """
    }
}

data class ProjectCreationResponse(
    val textResponse: String,
    val codeToGenerateFiles: String,
    val codeToInstallPackages: String,
    val codeToRun: String,
    var newFiles: MutableList<FileContent>

) {
    companion object {
        val responseFormat =
            """
{
  "type": "object",
  "properties": {
    "textResponse": {
      "type": "string",
      "description":"Brief response to the users request"
    },
    "codeToGenerateFiles": {
      "type": "string",
      "description":"Code to generate the missing project files. Consider the Root folder the current directory and existing. Only contains runnable code for cmd."
    },
    "codeToInstallPackages": {
      "type": "string",
      "description":"Code to install the missing npm packages. It is launched from the project folder. Only contains runnable code for cmd."
    },
    "codeToRun": {
      "type": "string",
      "description":"Code to run the application. The current directory is the project's directory. Only contains runnable code for cmd."
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
    }
  },
  "required": ["textResponse", "codeToRun", "codeToGenerateFiles","codeToInstallPackages", "newFiles"],
  "additionalProperties": false
}
        """
    }
}

data class FileConflictResolverResponse(
    val textResponse: String,
    val codeToGenerate: String,
    var modifiedFiles: MutableList<ModifiedFileContent>,
) {
    companion object {
        val responseFormat =
            """
{
  "type": "object",
  "properties": {
    "textResponse": {
      "type": "string"
    },
    "codeToGenerate": {
      "type": "string",
      "description":"Code to make the modifications usable. Especially npm package installation. Only contains runnable code."
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
  "required": ["textResponse", "codeToGenerate", "modifiedFiles"],
  "additionalProperties": false
}
        """
    }
}

data class FileContent(
    var path: String,
    val content: String,
)

data class ModifiedFileContent(
    val path: String,
    val oldContent: String,
    val newContent: String,
)

data class NewAndExistingFiles(
    val newFiles: MutableList<FileContent> = mutableListOf(),
    val existingFiles: MutableList<FileContent> = mutableListOf()
)