package app.web_gen

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CodeSnippetRepository : JpaRepository<CodeSnippet, Long> {
    @Query("SELECT * FROM code_snippets ORDER BY embedding <-> :queryVector LIMIT :limit", nativeQuery = true)
    fun findRelevantSnippets(@Param("queryVector") queryVector: String, @Param("limit") limit: Int): List<CodeSnippet>
}
