package app.web_gen.project

import app.web_gen.code_generation.enumeration.ProjectStatus
import app.web_gen.project.GeneratedProjectRepository
import app.web_gen.project.dashboard.Dashboard
import app.web_gen.project.dashboard.getDashboard
import app.web_gen.project.overview.Overview
import app.web_gen.project.overview.getOverview
import app.web_gen.security.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.time.OffsetDateTime

@Controller
@RequestMapping("/api/project")
@CrossOrigin
class ProjectController(
    private val projectRepository: GeneratedProjectRepository,
    private val authService: AuthService,
) {

    @GetMapping("/dashboard")
    fun getDashboard(): ResponseEntity<Dashboard> {
        val currentUser = authService.getCurrentUser()
        val dashboard = currentUser.projects.getDashboard()
        return ResponseEntity.ok(dashboard)
    }

    @GetMapping("/overview/{projectId}")
    fun getProjectOverview(@PathVariable projectId: Long): ResponseEntity<Overview> {

        return try {
            val currentUserId = authService.getCurrentUser().id ?: -1
            println("currentUserId: $currentUserId")
            val project = projectRepository.findByIdAndUserId(projectId, currentUserId).get()
            val overview = project.getOverview()

            ResponseEntity.ok(overview)
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/edit/{id}")
    fun editProject(@PathVariable id: String) {

    }


}