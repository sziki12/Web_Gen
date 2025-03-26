package app.web_gen.project.overview

import app.web_gen.code_generation.enumeration.ProjectStatus
import java.time.OffsetDateTime

data class Overview(
    var id: Long,
    var projectName: String,
    var projectStatus: ProjectStatus,
    var creation: OffsetDateTime,
    var lastModification: OffsetDateTime,
    var techStack: String,
    var projectType: String,
    //TODO Logs if the application is started, Websocket?
)