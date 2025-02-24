package app.web_gen.code_generation

import org.springframework.stereotype.Service
import java.io.File
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.pathString

@Service
class CommandSubstitutionService {
    val substitutableCommands = mutableMapOf<String, String>()

    private val filePath =
        Path(System.getProperty("user.dir"), "src/main/resources/substitutableCommands.txt").pathString

    init {
        loadCommands()
    }

    private final fun loadCommands() {
        val file = File(filePath)
        if (file.exists()) {
            val sc = Scanner(file)
            while (sc.hasNextLine()) {
                val line = sc.nextLine()

                val data = line.split(" <-> ")
                val command = data[0]
                val path = data[1]
                substitutableCommands[command] = path
            }
        } else {
            file.createNewFile()
        }
    }

    fun substituteCommands(command: String): String {
        var outCommand = command
        for (key in substitutableCommands.keys) {
            substitutableCommands[key]?.let {
                outCommand = if (outCommand == key) {
                    it
                } else {
                    outCommand.replace(key, it)
                }
            }
        }
        return outCommand
    }

    fun substituteCommands(commands: List<String>): List<String> {
        val outCommands = mutableListOf<String>()
        for (command in commands) {
            outCommands.add(this.substituteCommands(command))

        }
        return outCommands
    }
}