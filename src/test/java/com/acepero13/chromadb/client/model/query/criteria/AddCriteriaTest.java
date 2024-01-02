package com.acepero13.chromadb.client.model.query.criteria;

import com.acepero13.chromadb.client.exceptions.RequestValidationException;
import com.acepero13.chromadb.client.model.Documents;
import com.acepero13.chromadb.client.model.Embedding;
import com.acepero13.chromadb.client.model.Metadata;
import com.acepero13.chromadb.client.handler.ApiException;
import com.acepero13.chromadb.client.model.AddEmbedding;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AddCriteriaTest {

    public static final List<String> IDS = List.of("1", "2");
    public static final FakeFunction FAKE_FUNCTION = new FakeFunction();

    @Test
    void testCreateAddAction() throws ApiException {
        AddEmbedding actualRequest = AddCriteria.builder()
                .withIncrementalIndex()
                .withDocuments(Documents.of("text", "another text"))
                .withEmbeddings(List.of(new Embedding(1.0f, 2.0f), new Embedding(3.0f, 4.0f)))
                .withMetadata(Metadata.single("key", "value"))
                .build()
                .toRequest(List.of("id1", "id2"), FAKE_FUNCTION);

        AddEmbedding expected = new AddEmbedding()
                .incrementIndex(true)
                .documents(List.of("text", "another text"))
                .embeddings(List.of(List.of(1.0f, 2.0f), List.of(3.0f, 4.0f)))
                .metadatas(List.of(Map.of("key", "value")))
                .ids(List.of("id1", "id2"));
        assertEquals(expected, actualRequest);
    }

    @Test
    void testCreateAddActionWithoutIncrementalIndex() {
        AddCriteria actualRequest = AddCriteria.builder()
                .withNoIncrementalIndex()
                .build();

        assertFalse(actualRequest.shouldIncrementIndex());


    }

    @Test
    void testMetadata() {
        AddCriteria actualRequest = AddCriteria.builder()
                .withMetadata(Metadata.single("key", "value"))
                .build();

        assertEquals(List.of(Map.of("key", "value")), actualRequest.metadata());


    }

    @Test
    void testWithoutEmbeddings() {
        AddCriteria actualRequest = AddCriteria.builder().build();

        assertFalse(actualRequest.hasEmbeddings());

    }

    @Test
    void testWitEmbeddings() {
        AddCriteria actualRequest = AddCriteria.builder()
                .withEmbeddings(Embedding.single(1.0f, 20.0f, 3.0f))
                .build();

        assertTrue(actualRequest.hasEmbeddings());

    }

    @Test
    void testCreateAddActionWitIncrementalIndex() {
        AddCriteria actualRequest = AddCriteria.builder()
                .withIncrementalIndex()
                .build();

        assertTrue(actualRequest.shouldIncrementIndex());

    }

    @Test
    void testInvalidRequestMissingDocumentsAndEmbeddings() {
        AddCriteria actualRequest = AddCriteria.builder()
                .withIncrementalIndex()
                .build();

        Throwable error = assertThrows(RequestValidationException.class, () -> actualRequest.toRequest(IDS, FAKE_FUNCTION));
        assertTrue(error.getMessage().contains("You must provide either embeddings or a list of documents"));
    }

    @Test
    void testInvalidRequestIdsDifferentLengthAsDocsAndEmbeedings() {
        AddCriteria actualRequest = AddCriteria.builder()
                .withEmbeddings(1.0f, 2.0f, 3.0f)
                .withDocuments(Documents.single("Text"))
                .build();

        Throwable error = assertThrows(RequestValidationException.class, () -> actualRequest.toRequest(IDS, FAKE_FUNCTION));
        assertTrue(error.getMessage().contains("The length of ids, embeddings, and documents must match"));
    }

    @Test
    void testInvalidRequestNoEmbeddingsOrEmbeddingFunctionProvided() {
        AddCriteria actualRequest = AddCriteria.builder()
                .withDocuments(Documents.single("Text"))
                .build();

        Throwable error = assertThrows(NullPointerException.class, () -> actualRequest.toRequest(List.of("1"), null));
        assertTrue(error.getMessage().contains("Embeddings were not specify, therefore, you need to specify an embedding function"));
    }


}