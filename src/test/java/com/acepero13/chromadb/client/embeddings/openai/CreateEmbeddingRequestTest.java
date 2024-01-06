package com.acepero13.chromadb.client.embeddings.openai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateEmbeddingRequestTest {
    @Test
    void testBuilderWithDefaults() {
        String input = "test input";
        CreateEmbeddingRequest.Builder builder = CreateEmbeddingRequest.builder(input);
        CreateEmbeddingRequest request = builder.build();

        assertEquals(input, request.getInput());
        assertEquals("text-embedding-ada-002", request.getModel());
        assertEquals("java-client=0.0.1", request.getUser());
    }

    @Test
    void testBuilderWithCustomValues() {
        String input = "test input";
        String customModel = "custom-model";
        String customUser = "custom-user";

        CreateEmbeddingRequest.Builder builder = CreateEmbeddingRequest.builder(input)
                .withModel(customModel)
                .withUser(customUser);

        CreateEmbeddingRequest request = builder.build();

        assertEquals(input, request.getInput());
        assertEquals(customModel, request.getModel());
        assertEquals(customUser, request.getUser());
    }

    @Test
    void testJsonSerialization() {
        String input = "test input";
        String customModel = "custom-model";
        String customUser = "custom-user";

        CreateEmbeddingRequest.Builder builder = CreateEmbeddingRequest.builder(input)
                .withModel(customModel)
                .withUser(customUser);

        CreateEmbeddingRequest request = builder.build();
        String json = request.json();

        String expectedJson = "{\"input\":\"test input\",\"model\":\"custom-model\",\"user\":\"custom-user\"}";

        assertEquals(expectedJson, json);
    }

}