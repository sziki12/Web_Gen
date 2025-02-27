package app.web_gen.project

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface GeneratedProjectRepository: JpaRepository<GeneratedProject, Long> {

    fun findByName(name: String):Optional<GeneratedProject>
}