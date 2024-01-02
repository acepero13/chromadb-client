package com.acepero13.chromadb.client.model.query.criteria;

import com.acepero13.chromadb.client.exceptions.RequestValidationException;
import com.acepero13.chromadb.client.model.query.condition.Conditions;
import com.acepero13.chromadb.client.model.query.matchers.Matchers;
import com.acepero13.chromadb.client.handler.ApiException;
import com.acepero13.chromadb.client.model.DeleteEmbedding;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.acepero13.chromadb.client.model.query.matchers.Matchers.contains;
import static com.acepero13.chromadb.client.model.query.matchers.Matchers.isEqualTo;
import static org.junit.jupiter.api.Assertions.*;

class DeleteCriteriaTest {
    @Test void testDeleteCriteriaWhereMetadata() throws RequestValidationException {
        DeleteCriteria actual = DeleteCriteria.builder()
                .whereMetadata("field", isEqualTo("value"))
                .build();

        DeleteEmbedding expected = new DeleteEmbedding();
        expected.where(Map.of("field", Map.of("$eq", "value")))
                .whereDocument(new HashMap<>());

        assertEquals(expected, actual.toRequest(null, new FakeFunction()));

    }

    @Test void testDeleteCriteriaWhereConditionMetadata() throws RequestValidationException {
        DeleteCriteria actual = DeleteCriteria.builder()
                .whereMetadata(Conditions.or(
                        Conditions.cond("field", isEqualTo("value")),
                        Conditions.cond("anotherField", isEqualTo("value"))
                ))
                .build();

        DeleteEmbedding expected = new DeleteEmbedding();
        expected.where(Map.of("$or", List.of(Map.of("field", Map.of("$eq", "value")),
                        Map.of("anotherField", Map.of("$eq", "value"))) ))
                .whereDocument(new HashMap<>());

        assertEquals(expected, actual.toRequest(null, new FakeFunction()));

    }


    @Test void testWhereDocuments() throws RequestValidationException {
        DeleteCriteria actual = DeleteCriteria.builder()
                .whereDocument(contains("value"))
                .build();

        DeleteEmbedding expected = new DeleteEmbedding();
        expected.whereDocument(Map.of("$contains", "value"))
                .where(new HashMap<>());

        assertEquals(expected, actual.toRequest(null, new FakeFunction()));

    }

    @Test void testWhereDocumentsConditions() throws RequestValidationException {
        DeleteCriteria actual = DeleteCriteria.builder()
                .whereDocument(Conditions.or(contains("value"), contains("anotherValue")))
                .build();

        DeleteEmbedding expected = new DeleteEmbedding();
        expected.whereDocument(Map.of("$or", List.of(Map.of("$contains", "value"), Map.of("$contains", "anotherValue"))))
                .where(new HashMap<>());

        assertEquals(expected, actual.toRequest(null, new FakeFunction()));

    }

    @Test void testFailsBecauseNothingProvided(){
        DeleteCriteria actual = DeleteCriteria.builder().build();
        Throwable error = assertThrows(ApiException.class, () -> actual.toRequest(null, new FakeFunction()));

        assertTrue(error.getMessage().contains("you need to specify either the list of ids to delete or a where clause for documents or metadata"));
    }

}