package app.web_gen.code_snippet

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface CodeSnippetRepository : JpaRepository<CodeSnippet, Long> {
    @Query("SELECT * FROM code_snippets WHERE project_id = :projectId ORDER BY embedding <-> cast(:queryVector as vector)  LIMIT :limit", nativeQuery = true)
    fun findRelevantSnippets(@Param("projectId") projectId:Long, @Param("queryVector") queryVector: FloatArray, @Param("limit") limit: Int): List<CodeSnippet>

    fun findByProjectIdAndFilename(projectId: Long, filename: String): Optional<CodeSnippet>
    fun existsByProjectIdAndFilename(projectId: Long, filename: String): Boolean

    fun findByProjectIdAndRelativePathIn(projectId: Long, relativePath: Collection<String>):MutableList<CodeSnippet>
}
