package app.web_gen

import jakarta.persistence.*
import org.hibernate.annotations.Type

@Entity
@Table(name = "code_snippets")
data class CodeSnippet(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val filename: String,

    @Column(columnDefinition = "TEXT")
    val content: String,

    @Column(columnDefinition = "vector(1536)") // Assuming 1536-dimensional OpenAI embeddings
    val embedding: String
)
