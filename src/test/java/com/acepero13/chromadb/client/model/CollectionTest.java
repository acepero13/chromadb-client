package com.acepero13.chromadb.client.model;

import com.acepero13.chromadb.client.model.query.criteria.*;
import com.acepero13.chromadb.client.model.query.matchers.Matchers;
import com.acepero13.chromadb.client.result.GetResult;
import com.acepero13.chromadb.client.result.QueryResponse;
import com.acepero13.chromadb.client.utils.IdGenerator;
import com.acepero13.chromadb.client.handler.ApiException;
import com.acepero13.chromadb.client.handler.DefaultApi;
import com.acepero13.chromadb.client.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.acepero13.chromadb.client.model.query.matchers.Matchers.isEqualTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CollectionTest {
    public static final String COLLECTION_ID = "id";
    public static final float[] FLOATS = {1.0f, 2.0f, 3.0f};
    private final DefaultApi mockApi = Mockito.mock(DefaultApi.class);
    private final EmbeddingFunction mockEmbeddings = Mockito.mock(EmbeddingFunction.class, RETURNS_DEEP_STUBS);
    private final Collection collection = new Collection(mockApi, COLLECTION_ID, new Metadata(), CollectionName.of("name"), mockEmbeddings);

    @BeforeEach
    void setUp() {
        when(mockEmbeddings.createEmbeddings((Documents) any())).thenReturn(List.of(new Embedding(FLOATS)));
        when(mockEmbeddings.createEmbeddingsAsObject((Documents) any())).thenReturn(List.of(1.0f, 2.0f, 3.0f));
    }

    @Test void testCollectionAttributes(){
        assertEquals("name",collection.getNameAsString());
        assertEquals(CollectionName.of("name"), collection.getName());
        assertEquals("id", collection.getId());
    }

    @Test
    void successAddsEmbeddingToCollection() throws ApiException {

        List<String> ids = List.of("1", "2");

        when(mockApi.add(any(), any())).thenReturn(true);
        Map<String, Object> map = Map.of("key", "value");

        AddCriteria criteria = AddCriteria.builder()
                .withDocuments(Documents.of("Hello", "World"))
                .withMetadata(Metadata.single("key", "value"))
                .withIncrementalIndex()
                .withEmbeddings(List.of(new Embedding(1.0f, 2.0f, 3.0f), new Embedding(3.0f, 5.0f, 3.0f)))
                .build();

        AddEmbedding expectedRequest = new AddEmbedding();
        expectedRequest.incrementIndex(true);
        expectedRequest.setIds(ids);
        expectedRequest.setMetadatas(List.of(map));
        expectedRequest.setDocuments(List.of("Hello", "World"));
        expectedRequest.setEmbeddings(List.of(1.0f, 2.0f, 3.0f));

        when(mockApi.add(eq(expectedRequest), eq(COLLECTION_ID))).thenReturn(true);
        QueryResponse<Boolean> response = collection.add(ids, criteria);

        assertTrue(response.isSuccess());
        assertEquals(true, response.payload().orElse(false));
    }


    @Test
    void testAddWithoutSpecifyingIds() throws ApiException {


        when(mockApi.add(any(), any())).thenReturn(true);
        Map<String, Object> map = Map.of("key", "value");

        AddCriteria criteria = AddCriteria.builder()
                .withDocuments(Documents.of("Hello", "World"))
                .withMetadata(Metadata.single("key", "value"))
                .withIncrementalIndex()
                .withEmbeddings(List.of(new Embedding(1.0f, 2.0f, 3.0f), new Embedding(3.0f, 5.0f, 3.0f)))
                .build();

        AddEmbedding expectedRequest = new AddEmbedding();
        expectedRequest.incrementIndex(true);
        expectedRequest.setIds(List.of("d4ab5ad55f1dc0ffe69d38b31485c73e876a35d927e8659d70", "6623e8e79ab4f7bd2ac6679a25749cd7d532b162c765de0e29"));
        expectedRequest.setMetadatas(List.of(map));
        expectedRequest.setDocuments(List.of("Hello", "World"));
        expectedRequest.setEmbeddings(List.of(List.of(1.0f, 2.0f, 3.0f), List.of(3.0f, 5.0f, 3.0f)));

        when(mockApi.add(eq(expectedRequest), eq(COLLECTION_ID))).thenReturn(true);

        QueryResponse<List<String>> result = collection.add(criteria);
        assertTrue(result.isSuccess());
        assertEquals(2, result.payload().orElseThrow().size());
        verify(mockApi).add(expectedRequest, COLLECTION_ID);
    }

    @Test
    void testFailedAddWithoutSpecifyingIds() throws ApiException {


        HTTPValidationError error = new HTTPValidationError().addDetailItem(new ValidationError().msg("Error"));
        when(mockApi.add(any(), eq(COLLECTION_ID))).thenReturn(error);

        QueryResponse<List<String>> response = collection.add(
                AddCriteria.builder()
                        .withDocuments(Documents.single("text"))
                        .withEmbeddings(1.0f, 2.0f)
                        .build());

        assertFalse(response.isSuccess());
        assertTrue(response.error().orElseThrow().toString().contains("Failing adding new elements to collection"));
    }

    @Test
    void testWithCustomIdGenerator() throws ApiException {


        when(mockApi.add(any(), any())).thenReturn(true);
        Map<String, Object> map = Map.of("key", "value");

        AddCriteria criteria = AddCriteria.builder()
                .withDocuments(Documents.of("Hello", "World"))
                .withMetadata(Metadata.single("key", "value"))
                .withIncrementalIndex()
                .withEmbeddings(List.of(new Embedding(1.0f, 2.0f, 3.0f), new Embedding(3.0f, 5.0f, 3.0f)))
                .withIdGenerator(embeddings -> List.of("id1", "id2"))
                .build();

        AddEmbedding expectedRequest = new AddEmbedding();
        expectedRequest.incrementIndex(true);
        expectedRequest.setIds(List.of("id1", "id2"));
        expectedRequest.setMetadatas(List.of(map));
        expectedRequest.setDocuments(List.of("Hello", "World"));
        expectedRequest.setEmbeddings(List.of(List.of(1.0f, 2.0f, 3.0f), List.of(3.0f, 5.0f, 3.0f)));


        collection.add(criteria);
        verify(mockApi).add(expectedRequest, COLLECTION_ID);
    }

    @Test void testGetMetadata(){
        Collection col = new Collection(mockApi, COLLECTION_ID, Metadata.of("id", "value"), CollectionName.of("name"), mockEmbeddings);
        assertEquals(Metadata.of("id", "value"), col.getMetadata());
    }

    @Test
    void testCount() throws ApiException {
        when(mockApi.count(COLLECTION_ID)).thenReturn(10);
        QueryResponse<Integer> response = collection.count();
        assertTrue(response.isSuccess());
        assertEquals(10, response.payload().orElse(-1));

    }

    @Test
    void testAddingToCollectionFailure() throws ApiException {

        HTTPValidationError error = new HTTPValidationError().addDetailItem(new ValidationError().msg("Error"));
        when(mockApi.add(any(), eq(COLLECTION_ID))).thenReturn(error);

        QueryResponse<Boolean> response = collection.add(
                List.of("1"),
                AddCriteria.builder()
                        .withDocuments(Documents.single("text"))
                        .withEmbeddings(1.0f, 2.0f)
                        .build());

        assertFalse(response.isSuccess());
        assertEquals(error, response.error().orElse(null));
    }

    @Test
    @DisplayName("Cannot add elements to collection if ids is empty")
    void cannotAdd() {
        Documents documents = new Documents()
                .add("Hello")
                .add("World");

        List<Metadata> metadatas = Metadata.single("key", "value");

        Throwable exception = assertThrows(ApiException.class, () -> collection.add(new ArrayList<>(), AddCriteria.builder().build()));
        assertEquals("Ids cannot be empty", exception.getMessage());
    }


    @Test
    @DisplayName("returns the number of items in the collection")
    void countDocumentsInCollection() throws ApiException {
        when(mockApi.count(COLLECTION_ID)).thenReturn(10);
        QueryResponse<Integer> response = collection.count();
        assertTrue(response.isSuccess());
        assertEquals(10, response.payload().orElse(-1));
    }

    @Test
    @DisplayName("Delete all elements from collection")
    void deleteAll() throws ApiException {
        String jsonResponse = "{'ids' : ['id1', 'id2']}";
        when(mockApi.get(eq(new GetEmbedding()), eq(COLLECTION_ID))).thenReturn(jsonResponse);
        collection.delete();
        DeleteEmbedding expected = new DeleteEmbedding();
        expected.ids(List.of("id1", "id2"));
        expected.whereDocument(new HashMap<>());
        expected.where(new HashMap<>());
        Mockito.verify(mockApi).delete(expected, COLLECTION_ID);
    }

    @Test
    @DisplayName("Cannot delete if cannot get all elements")
    void emptyCollection() {
        ApiException thrown = assertThrows(ApiException.class, collection::delete);
        assertTrue(thrown.getMessage().contains("Error could not get all elements."));
    }

    @Test
    @DisplayName("Cannot delete if collection is empty")
    void noIdsCollection() throws ApiException {
        String jsonResponse = "{'ids' : []}";
        when(mockApi.get(eq(new GetEmbedding()), eq(COLLECTION_ID))).thenReturn(jsonResponse);
        ApiException thrown = assertThrows(ApiException.class, collection::delete);
        assertTrue(thrown.getMessage().contains("Collection is empty. Nothing to delete"));
    }

    @Test
    @DisplayName("Delete with ids")
    void deleteWithIds() throws ApiException {
        collection.delete(List.of("id1", "id2"));
        DeleteEmbedding expected = new DeleteEmbedding();
        expected.ids(List.of("id1", "id2"));
        expected.whereDocument(new HashMap<>());
        expected.where(new HashMap<>());
        Mockito.verify(mockApi).delete(expected, COLLECTION_ID);
    }

    @Test
    @DisplayName("Delete using query")
    void deleteWithQuery() throws ApiException {
        DeleteCriteria params = DeleteCriteria.builder()
                .whereDocument(Matchers.contains("text"))
                .whereMetadata("metadata", isEqualTo("meta"))
                .build();
        collection.delete(params);
        DeleteEmbedding expected = new DeleteEmbedding();
        expected.whereDocument(Map.of("$contains", "text"));
        expected.where(Map.of("metadata", Map.of("$eq", "meta")));
        Mockito.verify(mockApi).delete(expected, COLLECTION_ID);
    }

    @Test
    @DisplayName("Query elements")
    void query() throws ApiException {
        QueryCriteria parameters = QueryCriteria.builder()
                .whereMetadata("key", isEqualTo("value"))
                .whereDocument(Matchers.contains("text"))
                .include(List.of(Includes.DISTANCES, Includes.METADATAS))
                .withNumberOfResults(5)
                .build();
        collection.query("Hello world", parameters);

        QueryEmbedding expected = new QueryEmbedding();
        expected.nResults(5)
                .whereDocument(Map.of("$contains", "text"))
                .where(Map.of("key", Map.of("$eq", "value")))
                .include(List.of(QueryEmbedding.IncludeEnum.DISTANCES, QueryEmbedding.IncludeEnum.METADATAS));

        verify(mockApi).getNearestNeighbors(eq(expected), eq(COLLECTION_ID));
    }

    @Test
    @DisplayName("Query elements")
    void queryDocuments() throws ApiException {

        var res = collection.query(Documents.of("Hello world"));
        assertTrue(res.isError());

    }

    @Test
    @DisplayName("Query elements")
    void testQueryWithDefaultParameters() throws ApiException {

        collection.query("Hello world");

        QueryEmbedding expected = new QueryEmbedding();
        expected.nResults(10)
                .where(new HashMap<>())
                .whereDocument(new HashMap<>())
                .include(List.of(QueryEmbedding.IncludeEnum.DOCUMENTS, QueryEmbedding.IncludeEnum.METADATAS, QueryEmbedding.IncludeEnum.DISTANCES));

        verify(mockApi).getNearestNeighbors(eq(expected), eq(COLLECTION_ID));
    }

    @Test
    @DisplayName("Query elements")
    void queryWithEmbeddings() throws ApiException {
        QueryCriteria parameters = QueryCriteria.builder()
                .whereMetadata("key", isEqualTo("value"))
                .whereDocument(Matchers.contains("text"))
                .withEmbeddings(Embedding.single(6.0f, 7.0f, 8.0f))
                .include(Includes.DISTANCES, Includes.METADATAS)
                .withNumberOfResults(5)
                .build();
        collection.query(Documents.of("Hello world"), parameters);

        QueryEmbedding expected = new QueryEmbedding();
        expected.nResults(5)
                .whereDocument(Map.of("$contains", "text"))
                .where(Map.of("key", Map.of("$eq", "value")))
                .queryEmbeddings(List.of(List.of(6.0f, 7.0f, 8.0f)))
                .include(List.of(QueryEmbedding.IncludeEnum.DISTANCES, QueryEmbedding.IncludeEnum.METADATAS));

        verify(mockApi).getNearestNeighbors(eq(expected), eq(COLLECTION_ID));
    }

    @Test
    void get() throws ApiException {
        GetCriteria criteria = GetCriteria.builder()
                .withLimit(10)
                .withOffset(2)
                .whereMetadata("metadata", isEqualTo("value"))
                .whereDocument(Matchers.contains("text"))
                .include(Includes.DISTANCES, Includes.METADATAS)
                .build();

        QueryResponse<GetResult> result = collection.get(List.of("id1"), criteria);

        GetEmbedding expected = new GetEmbedding();
        expected.limit(10)
                .offset(2)
                .ids(List.of("id1"))
                .where(Map.of("metadata", Map.of("$eq", "value")))
                .whereDocument(Map.of("$contains", "text"))
                .include(List.of(GetEmbedding.IncludeEnum.DISTANCES, GetEmbedding.IncludeEnum.METADATAS))
        ;


        verify(mockApi).get(expected, COLLECTION_ID);
    }

    @Test
    void update() throws ApiException {
        UpdateCriteria updateCriteria = UpdateCriteria.builder()
                .withDocuments(Documents.single("text"))
                .withMetadata(Metadata.single("meta", "data"))
                .build();

        when(mockApi.update(any(), any())).thenReturn(true);

        collection.update(List.of("id1"), updateCriteria);

        UpdateEmbedding expected = new UpdateEmbedding()
                .ids(List.of("id1"))
                .documents(List.of("text"))
                .metadatas(List.of(Map.of("meta", "data")))
                .embeddings(List.of(1.0f, 2.0f, 3.0f));
        verify(mockApi).update(eq(expected), eq(COLLECTION_ID));
    }

    @Test
    void updateWithCustomEmbeddings() throws ApiException {
        UpdateCriteria updateCriteria = UpdateCriteria.builder()
                .withDocuments(Documents.single("text"))
                .withMetadata(Metadata.single("meta", "data"))
                .withEmbeddings(10.0f, 11.0f, 12.0f)
                .build();

        when(mockApi.update(any(), any())).thenReturn(true);

        collection.update(List.of("id1"), updateCriteria);

        UpdateEmbedding expected = new UpdateEmbedding()
                .ids(List.of("id1"))
                .documents(List.of("text"))
                .metadatas(List.of(Map.of("meta", "data")))
                .embeddings(List.of(List.of(10.0f, 11.0f, 12.0f)));
        verify(mockApi).update(eq(expected), eq(COLLECTION_ID));
    }

    @Test
    void updateCannotHaveEmptyIds() {
        Throwable error = assertThrows(ApiException.class, () -> collection.update(new ArrayList<>(), UpdateCriteria.builder().build()));

        assertTrue(error.getMessage().contains("Ids cannot be null for update"));
    }


    @Test
    void upsert() throws ApiException {
        when(mockApi.upsert(any(), any())).thenReturn(true);
        AddCriteria criteria = AddCriteria.builder()
                .withDocuments(Documents.single("text"))
                .withMetadata(Metadata.single("meta", "data"))
                .withNoIncrementalIndex()
                .withEmbeddings(2.0f, 3.0f, 5.0f)
                .build();
        collection.upsert(List.of("id1"), criteria);

        AddEmbedding expected = new AddEmbedding();
        expected.incrementIndex(false)
                .ids(List.of("id1"))
                .metadatas(List.of(Map.of("meta", "data")))
                .documents(List.of("text"))
                .incrementIndex(false)
                .embeddings(List.of(List.of(2.0f, 3.0f, 5.0f)))
        ;
        verify(mockApi).upsert(expected, COLLECTION_ID);
    }

    @Test
    void upsertCannotHaveEmptyIds() {
        Throwable error = assertThrows(ApiException.class, () -> collection.upsert(new ArrayList<>(), AddCriteria.builder().build()));
        assertTrue(error.getMessage().contains("Ids cannot be empty"));
    }

    @Test
    void getAllWithFilter() throws ApiException {
        String json = "{\"documents\":[\"doc1\",\"doc2\"],\"embeddings\":[],\"ids\":[\"id1\", \"id2\"],\"metadatas\":[]}";
        when(mockApi.get(any(), any())).thenReturn(json);

        Predicate<GetResult> filter = result -> result.getIds().contains("id1");
        QueryResponse<GetResult> actual = collection.getAll(filter);


        GetEmbedding expected = new GetEmbedding()
                .ids(null)
                .whereDocument(null);
        verify(mockApi).get(expected, COLLECTION_ID);

        assertEquals(2, actual.payload().orElseThrow().getDocuments().total());
    }


    @Test
    void getAllWithFiltersOut() throws ApiException {
        String json = "{\"documents\":[\"doc1\",\"doc2\"],\"embeddings\":[],\"ids\":[\"id1\", \"id2\"],\"metadatas\":[]}";
        when(mockApi.get(any(), any())).thenReturn(json);

        Predicate<GetResult> filter = result -> result.getIds().contains("non-existing");
        QueryResponse<GetResult> actual = collection.getAll(filter);


        GetEmbedding expected = new GetEmbedding()
                .ids(null)
                .whereDocument(null);
        verify(mockApi).get(expected, COLLECTION_ID);

        assertTrue(actual.payload().isEmpty());
    }

    @Test void testCreate(){
        CreateCollection request = Collection.CreateParams.create("name").request();

        assertEquals(createDefaultCollection("name"), request);
    }

    @Test void testCreateWithName(){
        CreateCollection request = Collection.CreateParams.create(CollectionName.of("anotherName")).request();

        assertEquals(createDefaultCollection("anotherName"), request);
    }

    @Test void testCreateParams(){
        FakeFunction embeddingFunction = new FakeFunction();
        Collection.CreateParams params = Collection.CreateParams.create("name").withEmbeddingFunction(embeddingFunction);

        assertEquals(embeddingFunction, params.embeddingsFunction());
    }

    private CreateCollection createDefaultCollection(String name) {
        return new CreateCollection()
                .getOrCreate(true)
                .name(name)
                .metadata(Map.of("client", "java-client"))
                ;
    }

}