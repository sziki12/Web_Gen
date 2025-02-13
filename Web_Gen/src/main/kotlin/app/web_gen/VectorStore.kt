package app.web_gen

import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.ai.vectorstore.pgvector.PgVectorStore
import org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.JdbcTemplate

@Bean
fun vectorStore(jdbcTemplate: JdbcTemplate?, embeddingModel: EmbeddingModel): VectorStore {
    return PgVectorStore.builder(jdbcTemplate!!, embeddingModel)
        .dimensions(1536) // Optional: defaults to model dimensions or 1536
        .distanceType(PgDistanceType.COSINE_DISTANCE) // Optional: defaults to COSINE_DISTANCE
        .indexType(PgVectorStore.PgIndexType.HNSW) // Optional: defaults to HNSW
        .initializeSchema(true) // Optional: defaults to false
        .schemaName("public") // Optional: defaults to "public"
        .vectorTableName("vector_store") // Optional: defaults to "vector_store"
        .maxDocumentBatchSize(10000) // Optional: defaults to 10000
        .build()
}