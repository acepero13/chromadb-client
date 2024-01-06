package com.acepero13.chromadb.client.model.query.builder;

import com.acepero13.chromadb.client.model.Metadata;
import com.acepero13.chromadb.client.model.query.matchers.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.acepero13.chromadb.client.model.query.condition.Conditions.*;
import static com.acepero13.chromadb.client.model.query.matchers.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class MetadataQueryTest {
    @Test
    void testCreateSimpleMetadataQuery() {
        var builder = new MetadataQuery();
        Map<String, Object> result = builder.where("age", isEqualTo(20))
                .build();

        assertEquals(expectedCondition("age", "$eq", 20), result);
    }

    @Test
    void testGreaterThan() {
        var builder = new MetadataQuery().where("age", greaterThan(20));

        assertEquals(expectedCondition("age", "$gt", 20), builder.build());
    }

    @Test
    void testGreaterOrEqualThan() {
        var builder = new MetadataQuery().where("age", greaterThanOrEqualTo(20));

        assertEquals(expectedCondition("age", "$gte", 20), builder.build());
    }

    @Test
    void testLessThan() {
        var builder = new MetadataQuery().where("age", lessThan(20));

        assertEquals(expectedCondition("age", "$lt", 20), builder.build());
    }

    @Test
    void testIn() {
        var builder = new MetadataQuery().where("name", Matchers.in("John", "Sarah", "Monica"));

        Map<String, Map<String, List<String>>> expected = Map.of("name", Map.of("$in", List.of("John", "Sarah", "Monica")));
        assertEquals(expected, builder.build());
    }

    @Test
    void testNotIn() {
        var builder = new MetadataQuery().where("name", Matchers.notIn("John", "Sarah", "Monica"));

        Map<String, Map<String, List<String>>> expected = Map.of("name", Map.of("$nin", List.of("John", "Sarah", "Monica")));
        assertEquals(expected, builder.build());
    }

    @Test
    void testLessOrEqualThan() {
        var builder = new MetadataQuery().where("temperature", lessThanOrEqualTo(37));

        assertEquals(expectedCondition("temperature", "$lte", 37), builder.build());
    }

    private Map<String, Object> expectedCondition(String field, String op, Object value) {
        return Map.of(field, Map.of(op, value));
    }


    @Test
    void testOrCondition() {
        var builder = new MetadataQuery();
        Map<String, Object> actual = builder.where(or(
                cond("a", greaterThan(10)),
                cond("a", lessThan(20))
        )).build();


        Map<String, List<Map<String, Map<String, Object>>>> expected = (Map.of("$or", List.of(Map.of("a", Map.of("$gt", 10)),
                Map.of("a", Map.of("$lt", 20)))));
        assertEquals(expected, actual);
    }

    @Test
    void testAndCondition() {
        var builder = new MetadataQuery();
        Map<String, Object> actual = builder.where(and(
                cond("a", greaterThan(10)),
                cond("a", lessThan(20))
        )).build();


        Map<String, Object> expected = Map.of("$and", List.of(Map.of("a", Map.of("$gt", 10)),
                Map.of("a", Map.of("$lt", 20))));
        assertEquals(expected, actual);
    }



    @Test
    @DisplayName("(((A=='X') and (B > 'U')) or ((A='Y') and (B='Z')))")
    void testComplexQuery() {
        var builder = new MetadataQuery();
        Map<String, Object> actual = builder.where(or(
                and(cond("a", isEqualTo("X")), cond("b", greaterThan(10))),
                and(cond("a", isEqualTo("Y")), cond("b", isEqualTo(0)))
        )).build();


        Map<String, Object> firstAnd = Map.of("$and",
                List.of(Map.of("a", Map.of("$eq", "X")),
                        Map.of("b", Map.of("$gt", 10))
                ));

        Map<String, Object> secondAnd = Map.of("$and",
                List.of(Map.of("a", Map.of("$eq", "Y")),
                        Map.of("b", Map.of("$eq", 0))
                ));
        Map<String, List<Map<String, Object>>> expected = Map.of("$or", List.of(firstAnd, secondAnd));

        assertEquals(expected, actual);
    }

    @Test void testToString(){
        assertEquals("[Metadata{metadata={key=value}}]", Metadata.single("key", "value").toString());
    }

    @Test void testContainsKey(){
        assertTrue(Metadata.of("key", "value").hasKey("key"));
    }

    @Test void testDoesNotContainKey(){
        assertFalse(Metadata.of("key", "value").hasKey("notAKey"));
    }

    @Test void testEmpty(){
        assertTrue(Metadata.empty().isEmpty());
    }

    @Test void testHashCode(){
        assertEquals(Metadata.empty().hashCode(), Metadata.empty().hashCode());
    }

    @Test void testHashCodeSingle(){
        assertEquals(Metadata.ofEmpty().hashCode(), Metadata.ofEmpty().hashCode());
    }

    @Test void testEmptyIsImmutable(){
        assertThrows(UnsupportedOperationException.class, () -> Metadata.ofEmpty().addMetadata("key", "value"));
    }

    @Test void testEmptyListIsImmutable(){
        assertThrows(UnsupportedOperationException.class, () -> Metadata.empty().add(new Metadata()));
    }

    @Test void testGetExistingValue(){
        var metadata = new Metadata()
                .addMetadata("firstKey", "firstValue")
                .addMetadata("secondKey", 2);

        assertEquals("firstValue", metadata.get("firstKey", String.class).orElseThrow());
    }

    @Test void testGetExistingValueCasting(){
        var metadata = new Metadata()
                .addMetadata("firstKey", "firstValue")
                .addMetadata("secondKey", 2);

        assertEquals(2, metadata.get("secondKey", Integer.class).orElseThrow());
    }

    @Test void testValueDoesNotExist(){
        var metadata = new Metadata()
                .addMetadata("firstKey", "firstValue")
                .addMetadata("secondKey", 2);

        assertTrue(metadata.get("non-existing", String.class).isEmpty());
    }

    @Test void testValueExistsButWrongType(){
        var metadata = new Metadata()
                .addMetadata("string", "value")
                .addMetadata("secondKey", 2);

        assertTrue(metadata.get("string", Integer.class).isEmpty());
    }


}