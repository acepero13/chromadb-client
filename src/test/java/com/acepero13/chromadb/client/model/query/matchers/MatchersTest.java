package com.acepero13.chromadb.client.model.query.matchers;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MatchersTest {

    @Test void testInString(){
        Map<String, Object> matcher = Matchers.in("text1", "test2").buildMap();

        assertEquals(Map.of("$in", List.of("text1", "test2")), matcher);
    }

    @Test void testInInteger(){
        Map<String, Object> matcher = Matchers.in(1, 2).buildMap();

        assertEquals(Map.of("$in", List.of(1, 2)), matcher);
    }

    @Test void testInFloat(){
        Map<String, Object> matcher = Matchers.in(1.0f, 2.0f).buildMap();

        assertEquals(Map.of("$in", List.of(1.0f, 2.0f)), matcher);
    }

    @Test void testNotInString(){
        Map<String, Object> matcher = Matchers.notIn("text1", "test2").buildMap();

        assertEquals(Map.of("$nin", List.of("text1", "test2")), matcher);
    }

    @Test void testNotInInteger(){
        Map<String, Object> matcher = Matchers.notIn(1, 2).buildMap();

        assertEquals(Map.of("$nin", List.of(1, 2)), matcher);
    }

    @Test void testNotInFloat(){
        Map<String, Object> matcher = Matchers.notIn(2.0f, 1.0f).buildMap();

        assertEquals(Map.of("$nin", List.of(2.0f, 1.0f)), matcher);
    }
    @Test void testIsNotEqualToString(){
        Map<String, Object> matcher = Matchers.isNotEqualTo("text").buildMap();

        assertEquals(Map.of("$ne", "text"), matcher);
    }

    @Test void testIsNotEqualToInteger(){
        Map<String, Object> matcher = Matchers.isNotEqualTo(2).buildMap();

        assertEquals(Map.of("$ne", 2), matcher);
    }

    @Test void testIsNotEqualToFloat(){
        Map<String, Object> matcher = Matchers.isNotEqualTo(2.0f).buildMap();

        assertEquals(Map.of("$ne", 2.0f), matcher);
    }



    @Test void testIsEqualToFloat(){
        Map<String, Object> matcher = Matchers.isEqualTo(2.0f).buildMap();

        assertEquals(Map.of("$eq", 2.0f), matcher);
    }


    @Test void testGreaterThanFloat(){
        Map<String, Object> matcher = Matchers.greaterThan(2.0f).buildMap();

        assertEquals(Map.of("$gt", 2.0f), matcher);
    }

    @Test void testGreaterThanOrEqualToFloat(){
        Map<String, Object> matcher = Matchers.greaterThanOrEqualTo(2.0f).buildMap();

        assertEquals(Map.of("$gte", 2.0f), matcher);
    }

    @Test void testLessThanFloat(){
        Map<String, Object> matcher = Matchers.lessThan(2.0f).buildMap();

        assertEquals(Map.of("$lt", 2.0f), matcher);
    }

    @Test void testLessThanOrEqualToFloat(){
        Map<String, Object> matcher = Matchers.lessThanOrEqualTo(2.0f).buildMap();

        assertEquals(Map.of("$lte", 2.0f), matcher);
    }

}