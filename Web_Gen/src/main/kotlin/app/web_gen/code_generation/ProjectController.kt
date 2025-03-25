package app.web_gen.code_generation

import app.web_gen.code_generation.enumeration.ProjectStatus
import app.web_gen.project.GeneratedProjectRepository
import app.web_gen.project.dashboard.Dashboard
import app.web_gen.project.overview.Overview
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.time.OffsetDateTime

@Repository
@RequestMapping("/api/project")
class ProjectController(
    private val generatedProjectRepository: GeneratedProjectRepository
) {

    @GetMapping("/dashboard")
    fun getDashboard():Dashboard{
        return Dashboard(mutableListOf())
    }

    @GetMapping("/overview/{id}")
    fun getProjectOverview(@PathVariable id: Long):Overview{
        val project = generatedProjectRepository.findById(id).get()
        return Overview(
            id = project.id,
            projectName = project.name,
            projectStatus = ProjectStatus.Generating,//TODO Status
            projectType = "project.type",
            creation = OffsetDateTime.now(),
            lastModification = OffsetDateTime.now(),
            techStack = "techStack"

        )
    }

    @PostMapping("/edit/{id}")
    fun editProject(@PathVariable id: String){

    }


}