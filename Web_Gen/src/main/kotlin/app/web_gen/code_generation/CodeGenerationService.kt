package app.web_gen.code_generation

import app.web_gen.code_generation.response.ModelResponse
import app.web_gen.exceprions.FileAlreadyExistsException
import org.springframework.stereotype.Service
import java.io.File
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.Path

@Service
class CodeGenerationService {
    fun applyChanges(filePath: String, oldCode: String, newCode: String) {
        val path = Paths.get(filePath)
        val content = Files.readString(path)
        val updatedContent = content.replace(oldCode, newCode)
        Files.writeString(path, updatedContent)
    }

    fun generateFiles(modelResponse:ModelResponse){
        val baseFilePath:String = Path(System.getProperty("user.dir"),"src/main/resources/generated").toString()
        var writer:PrintWriter
        for(newFile in modelResponse.newFiles){
            val file = File(Path(baseFilePath,newFile.path).toString())
            val parent = File(file.parent)
            println(file.path)
            if(!file.exists()){
                parent.mkdirs()
                file.createNewFile()
                writer = PrintWriter(file)
                writer.println(newFile.content)
                writer.close()
            }
            else{
                throw FileAlreadyExistsException(newFile.path)
            }
        }
        //TODO modified  files
    }
}