package com.acepero13.chromadb.client.embeddings.openai;

import com.acepero13.chromadb.client.model.Embedding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreateEmbeddingResponseTest {
    private static final String JSON_RESPONSE = "{\"data\":[{\"embedding\":[1.0,2.0,3.0],\"index\":0,\"object\":\"obj1\"},{\"embedding\":[4.0,5.0,6.0],\"index\":1,\"object\":\"obj2\"}],\"model\":\"model1\",\"object\":\"object1\",\"usage\":{\"prompt_tokens\":10,\"total_tokens\":20}}";

    private CreateEmbeddingResponse response;

    @BeforeEach
    void setUp() {
        response = CreateEmbeddingResponse.of(JSON_RESPONSE);
    }

    @Test
    void testData() {
        List<CreateEmbeddingResponse.DataItem> data = response.getData();
        assertNotNull(data);
        assertEquals(2, data.size());

        CreateEmbeddingResponse.DataItem item1 = data.get(0);
        assertEquals(Arrays.asList(1.0, 2.0, 3.0), item1.getEmbeddings());
        assertEquals(0, item1.getIndex());
        assertEquals("obj1", item1.getObject());

        CreateEmbeddingResponse.DataItem item2 = data.get(1);
        assertEquals(Arrays.asList(4.0, 5.0, 6.0), item2.getEmbeddings());
        assertEquals(1, item2.getIndex());
        assertEquals("obj2", item2.getObject());
    }

    @Test
    void testModel() {
        assertEquals("model1", response.getModel());
    }

    @Test
    void testObject() {
        assertEquals("object1", response.getObject());
    }

    @Test
    void testUsage() {
        CreateEmbeddingResponse.Usage usage = response.getUsage();
        assertNotNull(usage);
        assertEquals(10, usage.getPromptTokens());
        assertEquals(20, usage.getTotalTokens());
    }

    @Test
    void testGetEmbeddings() {
        List<Embedding> embeddings = response.getEmbeddings();
        assertNotNull(embeddings);
        assertEquals(2, embeddings.size());

        Embedding embedding1 = embeddings.get(0);
        assertEquals(Arrays.asList(1.0f, 2.0f, 3.0f), embedding1.raw());

        Embedding embedding2 = embeddings.get(1);
        assertEquals(Arrays.asList(4.0f, 5.0f, 6.0f), embedding2.raw());
    }

    @Test
    void testEmptyResponse() {
        String emptyJsonResponse = "{\"data\":[],\"model\":\"\",\"object\":\"\",\"usage\":{\"prompt_tokens\":0,\"total_tokens\":0}}";
        CreateEmbeddingResponse emptyResponse = CreateEmbeddingResponse.of(emptyJsonResponse);

        assertNotNull(emptyResponse.getData());
        assertEquals(0, emptyResponse.getData().size());
        assertEquals("", emptyResponse.getModel());
        assertEquals("", emptyResponse.getObject());
        assertNotNull(emptyResponse.getUsage());
        assertEquals(0, emptyResponse.getUsage().getPromptTokens());
        assertEquals(0, emptyResponse.getUsage().getTotalTokens());
    }

    @Test
    void testInvalidJsonResponse() {
        String invalidJsonResponse = "{\"data\":[],\"model\":\"\",\"object\":\"\",\"usage\":{\"prompt_tokens\":0,\"total_tokens\":0},\"extraField\":\"extraValue\"}";

        // Ensure that parsing invalid JSON does not throw an exception
        CreateEmbeddingResponse invalidResponse = CreateEmbeddingResponse.of(invalidJsonResponse);

        assertNotNull(invalidResponse.getData());
        assertEquals(0, invalidResponse.getData().size());
        assertEquals("", invalidResponse.getModel());
        assertEquals("", invalidResponse.getObject());
        assertNotNull(invalidResponse.getUsage());
        assertEquals(0, invalidResponse.getUsage().getPromptTokens());
        assertEquals(0, invalidResponse.getUsage().getTotalTokens());
    }

}