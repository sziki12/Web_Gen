package app.web_gen

import app.web_gen.code_running.CodeRunnerService
import org.springframework.boot.ExitCodeEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component


@Component
class ExitHandler(
    private val codeRunnerService: CodeRunnerService,
) {
    @EventListener
    fun exitEvent(event: ExitCodeEvent) {
        println("Exit code: " + event.exitCode)
        codeRunnerService.terminateAllApplication()
    }
}
