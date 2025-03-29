package app.web_gen.project

import app.web_gen.code_generation.enumeration.ProjectStatus
import app.web_gen.code_snippet.CodeSnippet
import app.web_gen.user.User
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "projects")
class GeneratedProject(
    var name: String,

    @Column(columnDefinition = "TEXT")
    var codeToGenerateFiles: String,
    @Column(columnDefinition = "TEXT")
    var codeToInstallPackages: String,
    @Column(columnDefinition = "TEXT")
    var codeToRun: String,

    var projectType: String,

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    val id: Long? = null

    val creation: OffsetDateTime = OffsetDateTime.now()

    @OneToMany(mappedBy = "project")
    val snippets: MutableList<CodeSnippet> = mutableListOf()

    var lastModification: OffsetDateTime = creation

    var techStack: String = ""

    @Column(columnDefinition = "TEXT")
    var dependencies: String = ""

    @Enumerated(EnumType.STRING)
    var status: ProjectStatus = ProjectStatus.Generating
}