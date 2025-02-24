package app.web_gen.project

import app.web_gen.code_snippet.CodeSnippet
import jakarta.persistence.*

@Entity
@Table(name="projects")
class GeneratedProject(
    var name: String,
    var codeToGenerate:String,
    var codeToRun:String,
    ) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    val id: Long? = null

    @OneToMany(mappedBy = "project")
    val snippets:MutableList<CodeSnippet> = mutableListOf()

}