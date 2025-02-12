package app.web_gen

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebGenApplication

fun main(args: Array<String>) {
	runApplication<WebGenApplication>(*args)
}
