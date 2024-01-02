package com.acepero13.chromadb.client.result;

import com.acepero13.chromadb.client.model.Documents;
import com.acepero13.chromadb.client.model.Embedding;
import com.acepero13.chromadb.client.model.Metadata;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.acepero13.chromadb.client.result.QueryResult.DistanceRange.*;
import static org.junit.jupiter.api.Assertions.*;

class QueryResultTest {

    private static final String JSON = "{\"ids\": [[\"id1\", \"id2\"], [\"d1\", \"d2\"]], \"distances\": [[0.0, 1.0], [-1.0, 5.0]]}";
    protected static final String FULL_JSON = "{\"ids\": [[\"id1\", \"id2\"], [\"d1\", \"d2\"]], \"distances\": [[0.0, 1.0], [-1.0, 5.0]], \"documents\": [[\"first text\", \"second text\"], [\"one\", \"two\"]], \"metadatas\": [[{\"meta\": \"data\"}], [{\"key\": \"value\"}]]}";
    @Test void parsesResult(){

        var result = QueryResult.of(JSON);

        assertEquals(List.of(List.of("id1", "id2"), List.of("d1", "d2")), result.getIds());
    }

    @Test void filterOnlyResultsEqualTo0(){
        var result = QueryResult.of(JSON);

        assertEquals(List.of(List.of("id1")), result.getIds(distanceIsEqualTo(0.0f, 0.001)));
    }

    @Test void testFilterResultsLowerThanZeroFive(){
        var result = QueryResult.of(JSON);
        assertEquals(List.of(List.of("id1"), List.of("d1")), result.getIds(distanceIsLessThan(0.5f)));
    }

    @Test void testFilterResultsBiggerThanZeroFive(){
        var result = QueryResult.of(JSON);
        assertEquals(List.of(List.of("id2"), List.of("d2")), result.getIds(distanceIsBiggerThan(0.5f)));
    }


    @Test void testGetDistances(){
        var result = QueryResult.of(JSON);
        assertEquals(List.of(List.of(0.0f, 1.0f), List.of(-1.0f, 5.0f)), result.getDistances());
    }

    @Test void testToJson(){
        var result = QueryResult.of(JSON);
        assertEquals("{\"embeddings\":[],\"ids\":[[\"id1\",\"id2\"],[\"d1\",\"d2\"]],\"distances\":[[0.0,1.0],[-1.0,5.0]]}", result.toString());
    }

    @Test void testWithDefaultRange(){
        var result = QueryResult.of(JSON);
        assertEquals(List.of(List.of("id1")), result.getIds(QueryResult.DEFAULT_RANGE));
    }

    @Test void testDocuments(){
        var result = QueryResult.of(FULL_JSON);
        assertEquals(List.of(Documents.of("first text", "second text"), Documents.of("one", "two")), result.getDocuments());
    }

    @Test void testDocumentsWithDistanceIs0(){
        var result = QueryResult.of(FULL_JSON);
        assertEquals(List.of(Documents.of("first text")), result.getDocuments(distanceIsEqualTo(0.0f, 0.001)));
    }

    @Test void testMetadata(){
        var result = QueryResult.of(FULL_JSON);
        assertEquals(List.of(Metadata.single("meta", "data"), Metadata.single("key", "value")), result.getMetadatas());
    }

    @Test void getEmbeddings(){
        var result = QueryResult.of(FULL_JSON);
        assertEquals(new ArrayList<>(), result.getEmbeddings());
    }

    @Test void getEmbeddingsDistance(){
        var result = QueryResult.of(FULL_JSON);
        assertEquals(new ArrayList<>(), result.getEmbeddings(QueryResult.DEFAULT_RANGE));
    }

    @Test void testMetadataDistanceIsEqual0(){
        var result = QueryResult.of(FULL_JSON);
        assertTrue(result.getMetadatas(QueryResult.DistanceRange.distanceIsEqualTo(1.0f, 0.0)).isEmpty());
    }

    @Test void testRangeToString(){
        assertEquals("DistanceRange{lowerBound=-0.5, upperBound=0.5}", QueryResult.DEFAULT_RANGE.toString());
    }
}