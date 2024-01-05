package com.acepero13.chromadb.client.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmbeddingFunctionTest {
    @Test
    void testCreateEmbeddings() {
        var func = new MockedEmbeddings();
        assertEquals(List.of(Embedding.of(4.0f, 5.0f, 6.0f)), func.createEmbeddings("hello"));
    }

    @Test
    void testCreateEmbeddingsWithModel() {
        var func = new MockedEmbeddings();
        assertEquals(List.of(Embedding.of(4.0f, 5.0f, 6.0f)), func.createEmbeddings(List.of("hello"), "model"));
    }

    @Test
    void testCreateEmbeddingsAsObject() {
        var func = new MockedEmbeddings();
        assertEquals(List.of(List.of(4.0f, 5.0f, 6.0f), List.of(7.0f, 8.0f, 9.0f)), func.createEmbeddingsAsObject(List.of("hello", "world")));
    }

    @Test
    void testCreateEmbeddingsAsObjectWithDocuments() {
        var func = new MockedEmbeddings();
        assertEquals(List.of(List.of(4.0f, 5.0f, 6.0f), List.of(7.0f, 8.0f, 9.0f)), func.createEmbeddingsAsObject(Documents.of("hello", "world")));
    }

    @Test
    void testCreateEmbeddingsWithDocuments() {
        var func = new MockedEmbeddings();
        assertEquals(List.of(Embedding.of(4.0f, 5.0f, 6.0f), Embedding.of(7.0f, 8.0f, 9.0f), Embedding.of(1.0f, 2.0f, 3.0f)), func.createEmbeddings(Documents.of("hello", "world", "text")));
    }

    @Test
    void testEmbeddingsEquals() {
        assertEquals(Embedding.of(1f, 2f, 3f), Embedding.of(1f, 2f, 3f));
    }

    @Test
    void testEmbeddingsHashCode() {
        assertEquals(Embedding.of(1f, 2f, 3f).hashCode(), Embedding.of(1f, 2f, 3f).hashCode());
    }

    @Test void testToString(){
        assertTrue(Embedding.of(1f, 2f, 3f).toString().contains("[1.0, 2.0, 3.0]"));
    }

    private static class MockedEmbeddings implements EmbeddingFunction {

        public static final Map<String, Embedding> DEFAULT_EMBEDDINGS = Map.of(
                "text", Embedding.of(1.0f, 2.0f, 3.0f),
                "hello", Embedding.of(4.0f, 5.0f, 6.0f),
                "world", Embedding.of(7.0f, 8.0f, 9.0f)
        );
        private final Map<String, Embedding> embeddings;

        private MockedEmbeddings(Map<String, Embedding> embeddings) {
            this.embeddings = embeddings;
        }

        private MockedEmbeddings() {
            this.embeddings = DEFAULT_EMBEDDINGS;
        }

        @Override
        public List<Embedding> createEmbeddings(List<String> documents) {
            return documents.stream()
                    .map(embeddings::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        @Override
        public List<Embedding> createEmbeddings(List<String> documents, String model) {
            return createEmbeddings(documents);
        }
    }

}