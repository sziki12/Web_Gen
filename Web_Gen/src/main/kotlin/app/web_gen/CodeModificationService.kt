package app.web_gen

import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Paths

@Service
class CodeModificationService {
    fun applyChanges(filePath: String, oldCode: String, newCode: String) {
        val path = Paths.get(filePath)
        val content = Files.readString(path)
        val updatedContent = content.replace(oldCode, newCode)
        Files.writeString(path, updatedContent)
    }
}