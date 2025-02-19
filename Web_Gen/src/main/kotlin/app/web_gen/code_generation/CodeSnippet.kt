package app.web_gen.code_generation

import com.fasterxml.jackson.annotation.JsonInclude
import dev.ai4j.openai4j.Json
import jakarta.persistence.*
import org.hibernate.annotations.Type
import io.hypersistence.utils.hibernate.type.json.JsonType;

@Entity
@Table(name = "code_snippets")
data class CodeSnippet(

    val filename: String,

    @Column(columnDefinition = "TEXT")
    val content: String,

    @Basic
    @Type(JsonType::class)
    @Column(columnDefinition = "vector(1536)") // Assuming 1536-dimensional OpenAI embeddings
    val embedding: FloatArray,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CodeSnippet) return false

        if (filename != other.filename) return false
        if (content != other.content) return false
        if (!embedding.contentEquals(other.embedding)) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = filename.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + embedding.contentHashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        return result
    }
}
