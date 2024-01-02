package com.acepero13.chromadb.client.embeddings.minilm;

import com.acepero13.chromadb.client.model.Embedding;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TextEmbeddingTest {
    @Test
    void embedSimpleText() {
        TextEmbedding embedding = new TextEmbedding(Model.BGE_SMALL_EN_v1_5);
        List<Embedding> result = embedding.createEmbeddings("Hello world");
        assertEquals(1, result.size());
        assertEquals(384, result.get(0).raw().size());
    }

    @Test
    void modelCannotBeFound() {
        TextEmbedding embedding = new TextEmbedding("not-existing");
        Throwable error = assertThrows(IllegalArgumentException.class, () -> embedding.createEmbeddings("Hello world"));

        assertTrue(error.getMessage().contains("Invalid djl URL:"));
    }
}