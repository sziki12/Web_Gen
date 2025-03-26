package app.web_gen.project

import app.web_gen.code_generation.enumeration.ProjectStatus
import app.web_gen.project.GeneratedProjectRepository
import app.web_gen.project.dashboard.Dashboard
import app.web_gen.project.overview.Overview
import app.web_gen.security.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.time.OffsetDateTime

@Controller
@RequestMapping("/api/project")
class ProjectController(
    private val generatedProjectRepository: GeneratedProjectRepository,
    private val authService: AuthService,
) {

    @GetMapping("/dashboard")
    fun getDashboard(): ResponseEntity<Dashboard> {
        return ResponseEntity.ok(Dashboard(mutableListOf()))
    }

    @GetMapping("/overview/{id}")
    fun getProjectOverview(@PathVariable id: Long): ResponseEntity<Overview> {
        val project = generatedProjectRepository.findById(id).get()
        val projectId = project.id
        return if (projectId != null) {
            val overview = Overview(
                id = projectId,
                projectName = project.name,
                projectStatus = ProjectStatus.Generating,//TODO Status
                projectType = "project.type",
                creation = OffsetDateTime.now(),
                lastModification = OffsetDateTime.now(),
                techStack = "techStack"
            )
            ResponseEntity.ok(overview)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/edit/{id}")
    fun editProject(@PathVariable id: String) {

    }


}