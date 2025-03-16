package app.web_gen.project

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import kotlin.io.path.Path
import kotlin.io.path.pathString

@Component
class ProjectPathResolver {
    @Value(value = "\${generated.project.path}")
    private lateinit var baseFilePath: String

    fun getProjectPath(projectName:String): String = Path(getUserFolderPath(),projectName).pathString
    fun getUserFolderPath(): String {
        //TODO Get current User
        val tempUser = "USER_ACCOUNT"
        return Path(baseFilePath,tempUser).pathString
    }
}