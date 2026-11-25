package app.web_gen.project.dashboard

import app.web_gen.code_generation.enumeration.ProjectStatus
import app.web_gen.project.GeneratedProject

data class Dashboard(
    var projects: MutableList<DashboardItem>
)

data class DashboardItem(
    var id: Long,
    var projectName: String,
    var status: ProjectStatus
) {
    constructor(id: Long, projectName: String) : this(id, projectName, ProjectStatus.Generating)
}

fun Collection<GeneratedProject>.getDashboard(): Dashboard {
    return Dashboard(this.map {
        DashboardItem(it.id!!, it.name, it.status)
    }.toMutableList())
}