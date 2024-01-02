package com.acepero13.chromadb.client.model.query.builder;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.acepero13.chromadb.client.model.query.condition.Conditions.and;
import static com.acepero13.chromadb.client.model.query.condition.Conditions.or;
import static com.acepero13.chromadb.client.model.query.matchers.Matchers.contains;
import static com.acepero13.chromadb.client.model.query.matchers.Matchers.notContains;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DocumentQueryTest {
    @Test
    void testContains() {
        var builder = new DocumentQuery();
        Map<String, Object> actual = builder.whereDocument(contains("text"))
                .build();

        assertEquals(expectContains("$contains", "text"), actual);
    }

    @Test
    void testDoesNotContain() {
        var builder = new DocumentQuery();
        Map<String, Object> actual = builder.whereDocument(notContains("text"))
                .build();

        assertEquals(expectContains("$not_contains", "text"), actual);
    }

    @Test
    void testOr() {
        var builder = new DocumentQuery();
        Map<String, Object> actual = builder.whereDocument(or(contains("text1"), contains("text2"))).build();

        Map<String, Object> expected = Map.of("$or", List.of(Map.of("$contains", "text1"), Map.of("$contains", "text2")));
        assertEquals(expected, actual);
    }

    @Test
    void testAnd() {
        var builder = new DocumentQuery();
        Map<String, Object> actual = builder.whereDocument(and(contains("text1"), contains("text2"))).build();

        Map<String, Object> expected = Map.of("$and", List.of(Map.of("$contains", "text1"), Map.of("$contains", "text2")));
        assertEquals(expected, actual);
    }

    @Test
    void testComplexQueryGluedByAnd() {
        var builder = new DocumentQuery();

        Map<String, Object> actual = builder.whereDocument(and(
                        or(contains("hello"), contains("world")),
                        or(contains("python"), contains("java"))
                ))
                .build();

        Map<String, Object> expected = Map.of("$and",
                List.of(Map.of("$or", List.of(Map.of("$contains", "hello"), Map.of("$contains", "world"))),
                        Map.of("$or", List.of(Map.of("$contains", "python"), Map.of("$contains", "java")))
                ));
        assertEquals(expected, actual);
    }

    @Test
    void testComplexQueryGluedByOr() {
        var builder = new DocumentQuery();

        Map<String, Object> actual = builder.whereDocument(or(
                        and(contains("hello"), contains("world")),
                        and(contains("python"), contains("java"))
                ))
                .build();

        Map<String, Object> expected = Map.of("$or",
                List.of(Map.of("$and", List.of(Map.of("$contains", "hello"), Map.of("$contains", "world"))),
                        Map.of("$and", List.of(Map.of("$contains", "python"), Map.of("$contains", "java")))
                ));
        assertEquals(expected, actual);
    }


    private Map<String, Object> expectContains(String operator, String text) {
        return Map.of(operator, text);
    }

}