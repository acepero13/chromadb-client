package com.acepero13.chromadb.client.model.query.criteria;

import com.acepero13.chromadb.client.exceptions.RequestValidationException;
import com.acepero13.chromadb.client.model.QueryEmbedding;
import com.acepero13.chromadb.client.model.query.matchers.Matchers;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.acepero13.chromadb.client.model.query.condition.Conditions.or;
import static org.junit.jupiter.api.Assertions.*;

class QueryCriteriaTest {
    @Test void validateQuery(){
        QueryEmbedding embedding = new QueryEmbedding();
        Throwable error = assertThrows(RequestValidationException.class, () -> QueryCriteria.Validator.validate(embedding));
        assertTrue(error.getMessage().contains("You need to either provide the embeddings or the text you want to query"));
    }

    @Test void testWhereMetadataBuilder(){
        QueryCriteria criteria = QueryCriteria.builder().whereMetadata(or("field", Matchers.isEqualTo(2))).build();
        Map<String, Object> expected = Map.of("$or", List.of(Map.of("field", Map.of("$eq", 2))));
        assertEquals(expected, criteria.whereMetadata());
    }

    @Test void testWhereDocumentBuilder(){
        QueryCriteria criteria = QueryCriteria.builder().whereDocument(or(Matchers.contains("text"))).build();
        Map<String, Object> expected = Map.of("$or", List.of(Map.of("$contains", "text")));
        assertEquals(expected, criteria.whereDocument());
    }

}