package app.web_gen.user

import app.web_gen.project.GeneratedProject
import jakarta.persistence.*

@Entity
@Table(name = "app_user")
data class User(
    var name: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val id: Long? = null

    @OneToMany
    var projects: MutableList<GeneratedProject> = mutableListOf()
}