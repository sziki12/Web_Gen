package app.web_gen.code_snippet

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface CodeSnippetRepository : JpaRepository<CodeSnippet, Long> {
    @Query("SELECT * FROM code_snippets WHERE project_id = (SELECT project_id FROM projects WHERE name = :projectName) ORDER BY embedding <-> cast(:queryVector as vector)  LIMIT :limit", nativeQuery = true)
    fun findRelevantSnippets(@Param("projectName") projectName:String, @Param("queryVector") queryVector: FloatArray, @Param("limit") limit: Int): List<CodeSnippet>

    fun findByProjectIdAndFilename(projectId: Long, filename: String): Optional<CodeSnippet>
    fun existsByProjectIdAndFilename(projectId: Long, filename: String): Boolean
}
