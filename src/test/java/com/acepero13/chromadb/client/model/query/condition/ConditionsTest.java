package com.acepero13.chromadb.client.model.query.condition;

import com.acepero13.chromadb.client.model.query.matchers.Matchers;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConditionsTest {
    @Test void testAndWithMatcher(){
        Map<String, Object> condition = Conditions.and("age", Matchers.isEqualTo(2)).build();
        assertEquals(Map.of("$and", List.of(Map.of("age", Map.of("$eq", 2)))), condition);
    }

    @Test void testOrWithMatcher(){
        Map<String, Object> condition = Conditions.or("age", Matchers.isEqualTo(2)).build();
        assertEquals(Map.of("$or", List.of(Map.of("age", Map.of("$eq", 2)))), condition);
    }

    @Test void orDocumentMatcher(){
        Map<String, Object> condition = Conditions.or(Matchers.contains("text")).build();
        assertEquals(Map.of("$or", List.of(Map.of("$contains", "text"))), condition);
    }



}