from pydantic import BaseModel, Field
generation_request_schema = {
  "title": "project_generation_template",
  "description": "The template for the user how to generate the project, install it's dependencies and run it.",
  "type": "object",
  "properties": {
    "role": {
      "type": "string",
    },
    "content": {
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
        "additionalProperties": False
      }
    }
  },
  "required": ["textResponse", "codeToRun", "codeToGenerateFiles","codeToInstallPackages", "newFiles"],
  "additionalProperties": False
}



class FileItem(BaseModel):
    """A file item representing a created or modified file."""
    path: str = Field(description="The path of the file relative to the root folder.")
    content: str = Field(description="The content of the file.")

class GenerationContent(BaseModel):
    """The content of the message."""
    textResponse: str = Field(description="Brief response to the users request.")
    codeToGenerateFiles: str = Field(description="Code to generate the missing project files. Consider the Root folder the current directory and existing. Only contains runnable code for cmd.")
    codeToInstallPackages: str = Field(description="Code to install the missing npm packages. It is launched from the project folder. Only contains runnable code for cmd.")
    codeToRun: str = Field(description="Code to run the application. The current directory is the project's directory. Only contains runnable code for cmd.")
    newFiles: list["FileItem"] = Field(description="List of new files to be created.")


class GenerationRequest(BaseModel):
    """The template for the user how to generate the project, install it's dependencies and run it."""
    role: str = Field(description="The role of the assistant.")
    textResponse: str = Field(description="Brief response to the users request.")
    codeToGenerateFiles: str = Field(description="Code to generate the missing project files. Consider the Root folder the current directory and existing. Only contains runnable code for cmd.")
    codeToInstallPackages: str = Field(description="Code to install the missing npm packages. It is launched from the project folder. Only contains runnable code for cmd.")
    codeToRun: str = Field(description="Code to run the application. The current directory is the project's directory. Only contains runnable code for cmd.")
    #newFiles: list["FileItem"] = Field(description="List of new files to be created.")