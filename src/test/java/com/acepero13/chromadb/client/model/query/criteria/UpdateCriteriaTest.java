package com.acepero13.chromadb.client.model.query.criteria;

import com.acepero13.chromadb.client.model.Documents;
import com.acepero13.chromadb.client.model.Embedding;
import com.acepero13.chromadb.client.model.Metadata;
import com.acepero13.chromadb.client.handler.ApiException;
import com.acepero13.chromadb.client.model.UpdateEmbedding;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateCriteriaTest {
    @Test
    void testUpdate() throws ApiException {
        UpdateCriteria criteria = UpdateCriteria.builder()
                .withEmbeddings(List.of(new Embedding(1.0f, 2.0f), new Embedding(3.0f, 4.0f)))
                .withDocuments(Documents.of("text"))
                .withMetadata(Metadata.single("key", "value"))
                .build();

        UpdateEmbedding expected = new UpdateEmbedding();
        List<String> ids = List.of("id1", "id2");
        expected.ids(ids)
                .metadatas(List.of(Map.of("key", "value")))
                .documents(List.of("text"))
                .embeddings(List.of(List.of(1.0f, 2.0f), List.of(3.0f, 4.0f)));

        assertEquals(expected, criteria.toRequest(ids, null));
    }

}