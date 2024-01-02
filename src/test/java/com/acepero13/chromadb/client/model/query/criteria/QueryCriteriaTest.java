package com.acepero13.chromadb.client.model.query.criteria;

import com.acepero13.chromadb.client.exceptions.RequestValidationException;
import com.acepero13.chromadb.client.model.QueryEmbedding;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryCriteriaTest {
    @Test void validateQuery(){
        QueryEmbedding embedding = new QueryEmbedding();
        Throwable error = assertThrows(RequestValidationException.class, () -> QueryCriteria.Validator.validate(embedding));
        assertTrue(error.getMessage().contains("You need to either provide the embeddings or the text you want to query"));
    }

}