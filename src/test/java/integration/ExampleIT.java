package integration;

import com.acepero13.chromadb.client.DbClient;
import com.acepero13.chromadb.client.embeddings.minilm.TextEmbedding;
import com.acepero13.chromadb.client.handler.ApiException;
import com.acepero13.chromadb.client.model.Collection;
import com.acepero13.chromadb.client.model.CollectionName;
import com.acepero13.chromadb.client.model.Metadata;
import com.acepero13.chromadb.client.model.query.criteria.AddCriteria;
import com.acepero13.chromadb.client.model.query.criteria.QueryCriteria;
import com.acepero13.chromadb.client.result.QueryResponse;
import com.acepero13.chromadb.client.result.QueryResult;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class ExampleIT {
    public static final int PORT = 8000;
    @Container
    public static GenericContainer<?> chromaDBContainer = new GenericContainer<>("chromadb/chroma:latest")
            .withExposedPorts(PORT);
    private URL url;

    ExampleIT() throws MalformedURLException {

    }
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
        String address = chromaDBContainer.getHost();
        Integer port = chromaDBContainer.getMappedPort(PORT);
        this.url = new URL("http://" + address + ":" + port);
    }

    @Test void testMain() throws ApiException {
        DbClient client = DbClient.create(url);
        Collection collection = client.createCollection("test-collection");

        collection.add(List.of("1", "2"), AddCriteria.builder()
                        .withDocuments("Hello, my name is John. I am a Data Scientist.", "Hello, my name is Bond. I am a Spy.")
                        .withMetadata(Metadata.of("type", "scientist"), Metadata.of("type", "spy"))
                .build());

        QueryResponse<QueryResult> response = collection.query("Who is the spy");

        assertTrue(response.isSuccess());
        assertEquals("2", response.payload().orElseThrow().getIds().get(0).get(0));
    }



    @Test void testPotentialExample() throws ApiException {
        DbClient client = DbClient.create(url);
        Collection collection = client.createCollection(CollectionName.of("qa-collection"));

        collection.add(List.of("1", "2"), AddCriteria.builder()
                .withDocuments("The user's current location is Paris", "The user's name is Alberto Durero")
                .withMetadata(Metadata.of("type", "location"), Metadata.of("type", "name"))
                .build());

        QueryResponse<QueryResult> response = collection.query("What can I Do here?");

        assertTrue(response.isSuccess());
        assertEquals("1", response.payload().orElseThrow().getIds().get(0).get(0));
    }
}
