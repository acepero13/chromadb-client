package com.acepero13.chromadb.client.result;

import com.acepero13.chromadb.client.model.HTTPValidationError;
import com.acepero13.chromadb.client.model.ValidationError;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class QueryResponseTest {

    @Test
    void testOfStringSuccess() {
        QueryResponse<String> response = QueryResponse.ofString("Hello, World!");

        assertTrue(response.isSuccess());
        assertEquals("Hello, World!", response.payload().orElse(null));
        assertTrue(response.error().isEmpty());
    }

    @Test
    void testOfStringFailure() {
        HTTPValidationError error = new HTTPValidationError().addDetailItem(new ValidationError().msg("Error"));
        QueryResponse<String> response = QueryResponse.ofString(error);

        assertFalse(response.isSuccess());
        assertTrue(response.payload().isEmpty());
        assertTrue(response.error().isPresent());
        assertEquals(error, response.error().orElse(null));
    }

    @Test
    void testOfIntegerSuccess() {
        QueryResponse<Integer> response = QueryResponse.ofInteger(42);

        assertTrue(response.isSuccess());
        assertEquals(42, response.payload().orElse(null));
        assertTrue(response.error().isEmpty());
    }

    @Test
    void testOfBooleanSuccess() {
        QueryResponse<Boolean> response = QueryResponse.ofBoolean(true);

        assertTrue(response.isSuccess());
        assertTrue(response.payload().orElse(null));
        assertTrue(response.error().isEmpty());
    }

    @Test
    void testOfObjectSuccess() {
        String value = "Hello, World!";
        QueryResponse<String> response = QueryResponse.of(value, String.class);

        assertTrue(response.isSuccess());
        assertEquals(value, response.payload().orElse(null));
        assertTrue(response.error().isEmpty());
    }

    @Test
    void testFailureToString() {
        HTTPValidationError error = new HTTPValidationError().addDetailItem(new ValidationError().msg("MyError"));
        QueryResponse<QueryResult> response = QueryResponse.ofResult(error);

        assertTrue(response.toString().contains("MyError"));
    }

    @Test
    void testToStringOfObjectSuccess() {
        String value = "Hello, World!";
        QueryResponse<String> response = QueryResponse.of(value, String.class);

        assertTrue(response.isSuccess());
        assertEquals("Success: {Optional[Hello, World!] }", response.toString());
    }

    @Test
    void testOfObjectFailureWithValidationError() {
        HTTPValidationError error = new HTTPValidationError().addDetailItem(new ValidationError().msg("Error"));
        QueryResponse<String> response = QueryResponse.of(error, String.class);

        assertFalse(response.isSuccess());
        assertTrue(response.payload().isEmpty());
        assertTrue(response.error().isPresent());
        assertEquals(error, response.error().orElse(null));
    }

    @Test
    void testOfObjectFailureWithUnknown() {
        Object unknown = new Object();
        QueryResponse<String> response = QueryResponse.of(unknown, String.class);

        assertFalse(response.isSuccess());
        assertTrue(response.payload().isEmpty());
        assertTrue(response.error().isPresent());
        HTTPValidationError error = response.error().orElse(null);
        assertNotNull(error);
        assertEquals(1, error.getDetail().size());
        assertEquals("Unknown", error.getDetail().get(0).getMsg());
    }

    @Test
    void testOfResultSuccess() {
        String validJsonObject = "{" +
                "\"documents\":[[\"doc1\",\"doc2\"],[\"doc3\",\"doc4\"]]," +
                "\"embeddings\":[[1.0,2.0,3.0],[4.0,5.0,6.0]]," +
                "\"ids\":[[\"id1\",\"id2\"],[\"id3\",\"id4\"]]," +
                "\"metadatas\":[[{\"key\":\"value\"},{\"key\":\"value\"}],[{\"key\":\"value\"},{\"key\":\"value\"}]]," +
                "\"distances\":[[0.1,0.2],[0.3,0.4]]" +
                "}";
        QueryResult result = QueryResult.of(validJsonObject);
        QueryResponse<QueryResult> response = QueryResponse.ofResult(validJsonObject);

        assertTrue(response.isSuccess());
        assertTrue(response.error().isEmpty());
    }

    @Test
    void testOfResultFailure() {
        HTTPValidationError error = new HTTPValidationError().addDetailItem(new ValidationError().msg("Error"));
        QueryResponse<QueryResult> response = QueryResponse.ofResult(error);

        assertFalse(response.isSuccess());
        assertTrue(response.payload().isEmpty());
        assertTrue(response.error().isPresent());
        assertEquals(error, response.error().orElse(null));
    }





    @Test
    void testOfGetResultFailure() {
        HTTPValidationError error = new HTTPValidationError().addDetailItem(new ValidationError().msg("Error"));
        QueryResponse<GetResult> response = QueryResponse.ofGetResult(error);

        assertFalse(response.isSuccess());
        assertTrue(response.payload().isEmpty());
        assertTrue(response.error().isPresent());
        assertEquals(error, response.error().orElse(null));
    }

    @Test
    void testOfListSuccess() {
        List<String> list = Arrays.asList("item1", "item2", "item3");
        QueryResponse<List<String>> response = QueryResponse.ofList(list);

        assertTrue(response.isSuccess());
        assertEquals(list, response.payload().orElse(null));
        assertTrue(response.error().isEmpty());
    }

    @Test
    void testOfListFailure() {
        HTTPValidationError error = new HTTPValidationError().addDetailItem(new ValidationError().msg("Error"));
        QueryResponse<List<String>> response = QueryResponse.ofList(error);

        assertFalse(response.isSuccess());
        assertTrue(response.payload().isEmpty());
        assertTrue(response.error().isPresent());
        assertEquals(error, response.error().orElse(null));
    }

    @Test
    void testOfNullableSuccess() {
        QueryResponse<Boolean> response = QueryResponse.ofNullable(null);

        assertTrue(response.isSuccess());
        assertTrue(response.payload().orElse(null));
        assertTrue(response.error().isEmpty());
    }

    @Test
    void testOfNullableFailure() {
        HTTPValidationError error = new HTTPValidationError().addDetailItem(new ValidationError().msg("Error"));
        QueryResponse<Boolean> response = QueryResponse.ofNullable(error);

        assertFalse(response.isSuccess());
        assertTrue(response.payload().isEmpty());
        assertTrue(response.error().isPresent());
        assertEquals(error, response.error().orElse(null));
    }

    @Test
    void testFailed() {
        String errorMsg = "Custom error message";
        QueryResponse<String> response = QueryResponse.failed(errorMsg);

        assertFalse(response.isSuccess());
        assertTrue(response.payload().isEmpty());
        assertTrue(response.error().isPresent());

        HTTPValidationError error = response.error().orElse(null);
        assertNotNull(error);
        assertEquals(1, error.getDetail().size());
        assertEquals(errorMsg, error.getDetail().get(0).getMsg());
    }
}
