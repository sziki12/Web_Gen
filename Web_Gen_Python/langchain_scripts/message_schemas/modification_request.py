modification_request_schema = {
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
        "additionalProperties": False
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
        "additionalProperties": False
      }
    }
  },
  "required": ["textResponse", "codeToGenerate", "newFiles", "modifiedFiles"],
  "additionalProperties": False
}