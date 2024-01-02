package integration;

import com.acepero13.chromadb.client.DbClient;
import com.acepero13.chromadb.client.embeddings.openai.OpenAiEmbeddingsFunction;
import com.acepero13.chromadb.client.model.Collection;
import com.acepero13.chromadb.client.model.CollectionName;
import com.acepero13.chromadb.client.model.Documents;
import com.acepero13.chromadb.client.model.Metadata;
import com.acepero13.chromadb.client.model.query.condition.Conditions;
import com.acepero13.chromadb.client.model.query.criteria.AddCriteria;
import com.acepero13.chromadb.client.model.query.criteria.DeleteCriteria;
import com.acepero13.chromadb.client.model.query.criteria.QueryCriteria;
import com.acepero13.chromadb.client.model.query.criteria.UpdateCriteria;
import com.acepero13.chromadb.client.result.GetResult;
import com.acepero13.chromadb.client.result.QueryResponse;
import com.acepero13.chromadb.client.result.QueryResult;
import com.acepero13.chromadb.client.handler.ApiException;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.acepero13.chromadb.client.model.query.condition.Conditions.cond;
import static com.acepero13.chromadb.client.model.query.condition.Conditions.or;
import static com.acepero13.chromadb.client.model.query.matchers.Matchers.contains;
import static com.acepero13.chromadb.client.model.query.matchers.Matchers.isEqualTo;
import static com.acepero13.chromadb.client.result.QueryResult.DistanceRange.distanceIsLessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class CollectionIT {
    public static final int PORT = 8000;
    public static final String ID = "randomId";
    public static final float[] DEFAULT_EMBEDDINGS = {-1.0f, -2.5f, 2.0f};
    public static final String ID_WITH_EMBEDDINGS = "id22";
    @Container
    public static GenericContainer<?> chromaDBContainer = new GenericContainer<>("chromadb/chroma:latest")
            .withExposedPorts(PORT);
    private Collection collection;
    private DbClient client;

    @BeforeAll
    static void beforeAll() {
        chromaDBContainer.start();
    }

    @AfterAll
    static void afterAll() {
        chromaDBContainer.stop();
    }

    @BeforeEach
    public void setUp() throws MalformedURLException, ApiException {
        //.waitingFor(Wait.forListeningPort());
        String address = chromaDBContainer.getHost();
        Integer port = chromaDBContainer.getMappedPort(PORT);
        client = DbClient.create(new URL("http://" + address + ":" + port));
        this.collection = client.createCollection(Collection.CreateParams.create(CollectionName.of("myJavaTestCollection")));
    }

    @AfterEach
    public void tearDown() throws ApiException {
        client.deleteCollection(collection);
    }

    @Test
    public void testAddToCollection() throws ApiException {
        collection.add(List.of("id1"), AddCriteria.builder()
                .withDocuments("This is a text")
                .withMetadata(Metadata.single("key", "value"))
                .withEmbeddings(1.0f, 2.0f, 3.0f)
                .build());

        assertEquals(1, collection.count().payload().orElse(-1));
    }


    @Test
    void testDeleteAllEmbeddingsFromCollection() throws ApiException {
        addOneItemToCollection();
        collection.delete();
        assertEquals(0, collection.count().payload().orElse(-1));
    }


    @Test
    void testDeleteEmbeddingById() throws ApiException {
        addOneItemToCollection();
        var response = collection.delete(List.of(ID));
        assertEquals(0, collection.count().payload().orElse(-1));
        assertTrue(response.isSuccess());
    }

    @Test
    void testDeleteEmbeddingWithCriteria() throws ApiException {
        addOneItemToCollection();
        QueryResponse<List<String>> response = collection.delete(DeleteCriteria.builder()
                .whereMetadata("key", isEqualTo("random"))
                .build());

        assertEquals(0, collection.count().payload().orElse(-1));
        assertTrue(response.isSuccess());
    }

    @Test
    void testUpdate() throws ApiException {
        addOneItemToCollection();
        collection.update(List.of(ID), UpdateCriteria.builder()
                .withDocuments(Documents.single("This is an updated document"))
                .withEmbeddings(DEFAULT_EMBEDDINGS)
                .build());

        QueryResponse<GetResult> result = collection.getAll(res -> res.getDocuments().contains("This is an updated document"));

        assertTrue(result.isSuccess());
    }

    @Test
    void testUpsertCreatesNewItem() throws ApiException {
        addOneItemToCollection();
        collection.upsert(List.of("newId"), AddCriteria.builder()
                .withDocuments(Documents.single("This is a new document"))
                .withEmbeddings(1.0f, 2.0f, 3.0f)
                .build());

        QueryResponse<GetResult> result = collection.getAll(res -> res.getDocuments().contains("This is a new document"));

        assertTrue(result.isSuccess());
        assertEquals(2, collection.count().payload().orElse(-1));
    }

    @Test
    void testUpsertUpdatesOldItem() throws ApiException {
        addOneItemToCollection();
        collection.upsert(List.of(ID), AddCriteria.builder()
                .withDocuments(Documents.single("This is an updated document"))
                .withEmbeddings(1.0f, 2.0f, 3.0f)
                .build());

        QueryResponse<GetResult> result = collection.getAll(res -> res.getDocuments().contains("This is an updated document"));

        assertTrue(result.isSuccess());
        assertEquals(1, collection.count().payload().orElse(-1));
    }

    @Test
    void simpleQuery() throws ApiException {
        addOneItemToCollectionUsingDefaultEmbeddings();
        QueryResponse<QueryResult> result = collection.query("This is a random text", QueryCriteria.builder()
                .whereMetadata("meta", isEqualTo("data"))
                .build());

        List<List<String>> ids = result.payload().map(res -> res.getIds(distanceIsLessThan(0.5f)))
                .orElseThrow();

        assertEquals(List.of(List.of(ID_WITH_EMBEDDINGS)), ids);
    }

    @Test
    @Disabled("I need a new openAI KEy")
    void openAiEmbeddings() throws ApiException {
        Collection openAiCollection = client.createCollection(Collection.CreateParams
                .create(CollectionName.of("openAiCollection"))
                .withEmbeddingFunction(OpenAiEmbeddingsFunction.createUsingApiKeyFromEnv())
        );

        openAiCollection.add(List.of("openAi1"), AddCriteria.builder()
                .withDocuments(Documents.of("Hello world"))
                .build());

        assertEquals(1, openAiCollection.count().payload().orElse(-1));
    }

    @Test
    void complexQuery() throws ApiException {
        // TODO: Make this query more complex
// whereDocument (version = v1 || version = v2) && (documents contains Doc or documents contains Doc1)

        collection.add(List.of("id1", "id2", "id3"), AddCriteria.builder()
                .withDocuments("Hello world", "Hello python", "Hello java")
                .withMetadata(Metadata.ofEmpty(), Metadata.of("version", "v1"), Metadata.of("version", "v2"))
                .withIncrementalIndex()
                .build());

        QueryCriteria criteria = QueryCriteria.builder()
                .whereMetadata(
                        or(
                                cond("version", isEqualTo("v1")), cond("version", isEqualTo("v2"))
                        )
                )
                .whereDocument(or(contains("python"), (contains("java"))))
                .withNumberOfResults(5)
                .build();
        QueryResponse<QueryResult> result = collection.query("Hello", criteria);

        assertTrue(result.isSuccess());
        List<List<String>> ids = result.payload().orElseThrow().getIds();
        assertEquals(List.of(List.of("id3", "id2")), ids);
    }

    private void addOneItemToCollectionUsingDefaultEmbeddings() throws ApiException {
        collection.add(List.of(ID_WITH_EMBEDDINGS), AddCriteria.builder()
                .withDocuments(Documents.of("This is a random text from anotherId"))
                .withMetadata(Metadata.single("meta", "data"))
                .build());

        //final String logs = chromaDBContainer.getLogs();

    }

    private void addOneItemToCollection() throws ApiException {
        collection.add(List.of(ID), AddCriteria.builder()
                .withDocuments(Documents.of("This is a random text"))
                .withMetadata(Metadata.single("key", "random"))
                .withEmbeddings(DEFAULT_EMBEDDINGS)
                .build());

    }


}
