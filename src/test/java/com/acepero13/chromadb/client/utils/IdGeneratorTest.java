package com.acepero13.chromadb.client.utils;

import com.acepero13.chromadb.client.model.Embedding;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IdGeneratorTest {
    private final IdGenerator idGenerator = IdGenerator.defaultIdGenerator();

    @Test
    public void testNormalCase() {
        List<String> ids = idGenerator.generate(Embedding.single(1.0f, 2.0f, 3.0f));
        assertEquals(1, ids.size());
        assertTrue(ids.get(0).length() >= 3 && ids.get(0).length() <= 50);
    }

    @Test
    public void testEmptyList() {
        List<String> ids = idGenerator.generate(Collections.emptyList());
        assertTrue(ids.isEmpty());
    }



    @Test
    public void testLargeEmbeddings() {
        List<Float> elements = new ArrayList<>();
        for (int i = 0; i < 2000; i++) {
            elements.add((float) i);
        }
        Embedding largeEmbedding = new Embedding(elements); // Large data
        List<String> ids = idGenerator.generate(Collections.singletonList(largeEmbedding));
        assertEquals(1, ids.size());
        assertEquals(50, ids.get(0).length());
    }



    @Test
    public void testNoSuchAlgorithmException() {
        // This test depends on how you can simulate NoSuchAlgorithmException
    }

    @Test
    public void testUniqueIdsForDifferentEmbeddings() {
        Embedding embedding1 = new Embedding(1.0f, 2.0f, 3.0f);
        Embedding embedding2 = new Embedding(1.0f, 2.0f, 3.1f); // Different from embedding1
        List<String> ids1 = idGenerator.generate(Collections.singletonList(embedding1));
        List<String> ids2 = idGenerator.generate(Collections.singletonList(embedding2));
        assertNotEquals(ids1.get(0), ids2.get(0));
    }

    @Test
    public void testConsistentIds() {
        Embedding embedding1 = new Embedding(1.0f, 2.0f, 3.0f);
        Embedding embedding2 = new Embedding(1.0f, 2.0f, 3.0f); // Different from embedding1
        List<String> ids1 = idGenerator.generate(Collections.singletonList(embedding1));
        List<String> ids2 = idGenerator.generate(Collections.singletonList(embedding2));
        assertEquals(ids1.get(0), ids2.get(0));
    }
}