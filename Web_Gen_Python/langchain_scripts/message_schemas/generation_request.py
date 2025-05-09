from pydantic import BaseModel, Field
layout_generation_request_schema = {
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
      "description":"Runnable terminal code to generate required project files. Prefer using scaffolding tools (e.g., npx). Assumes the current directory is the user folder. Don't change the working directory."
    },
    "codeToInstallPackages": {
      "type": "string",
      "description":"Runnable terminal code to install required dependencies (e.g., npm/yarn/pnpm). Assumes execution from the project root. Don't change the working directory."
    },
    "codeToRun": {
      "type": "string",
      "description":"Runnable terminal code to start the application (e.g., dev server, build step). Executed from the project root. Don't change the working directory."
    },
    "newFiles": {
      "type": "array",
      "description":"List of new files to create in the project for it to be functional.",
      "items": {
        "type": "object",
        "properties": {
          "path": { 
              "type": "string",
              "description": "Relative path to the new file from the root directory." },
          "description": { 
              "type": "string",
              "description": "Detailed description of the file containing it's purpose."  }
        },
        "required": ["path", "content"],
        "additionalProperties": False
      }
    }
  },
  "required": ["textResponse", "codeToRun", "codeToGenerateFiles","codeToInstallPackages", "newFiles"],
  "additionalProperties": False
}

file_generation_request_schema = {
  "title": "file_generation_schema",
  "description": "The template for the content of the generated files.",
  "type": "object",
  "properties": {
    "role": {
      "type": "string",
      "description":"Should be developer."
    },
    "content": {
      "type": "string",
       "description":"Brief response to the users request"
    },
    "newFiles": {
      "type": "array",
      "description":"List of new files to create in the project for it to be functional.",
      "items": {
        "type": "object",
        "properties": {
          "path": { 
              "type": "string",
              "description": "Relative path to the new file from the root directory." },
          "content": { 
              "type": "string",
              "description": "Full content of the file."  }
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