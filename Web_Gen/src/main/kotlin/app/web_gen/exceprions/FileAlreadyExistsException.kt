package app.web_gen.exceprions

data class FileAlreadyExistsException(var filename:String): Exception("File $filename already Exists")