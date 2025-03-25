package app.web_gen.project.dashboard

import app.web_gen.code_generation.enumeration.ProjectStatus

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
