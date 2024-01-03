package integration;

import com.acepero13.chromadb.client.DbClient;
import com.acepero13.chromadb.client.handler.ApiException;
import com.acepero13.chromadb.client.model.Collection;
import com.acepero13.chromadb.client.model.CollectionName;
import com.acepero13.chromadb.client.model.Metadata;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
@Testcontainers
class DbClientIT {
    public static final int PORT = 8000;

    @Container
    public static GenericContainer<?> chromaDBContainer = new GenericContainer<>("chromadb/chroma:latest")
            .withExposedPorts(PORT);
    private DbClient client;

    DbClientIT() throws MalformedURLException {
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
        //.waitingFor(Wait.forListeningPort());
        String address = chromaDBContainer.getHost();
        Integer port = chromaDBContainer.getMappedPort(PORT);
        this.client = DbClient.create(new URL("http://" + address + ":" + port));
    }

    @AfterEach
    public void tearDown() throws ApiException {
        client.deleteAllCollections();
    }

    @Test
    void testListCollections() throws ApiException {
        this.client.createCollection(Collection.CreateParams.create("anotherCollection"));
        List<Collection> collections = client.listCollections();
        assertEquals(1, collections.size());
    }

    @Test
    void testVersion() throws ApiException {
        String version = this.client.version();
        assertTrue(version.contains("."));
    }

    @Test
    void testHeartBeat() throws ApiException {
        Map<String, BigDecimal> heartbeat = client.heartbeat();
        assertTrue(heartbeat.containsKey("nanosecond heartbeat"));
    }



    @Test
    void createCollection() throws ApiException {
        Collection collection = client.createCollection(Collection.CreateParams.create("test"));
        assertEquals("test", collection.getNameAsString());
    }

    @Test
    void updateCollection() throws ApiException {
        Collection collection = client.createCollection(Collection.CreateParams.create("test"));
        Collection got = client.getCollection(CollectionName.of("test"), null);
        assertNotNull(got);
        client.update(collection, CollectionName.of("newcollection"), new Metadata().addMetadata("meta", "data"), null);
        Collection got2 = client.getCollection(CollectionName.of("newcollection"), null);

        assertEquals("newcollection", got2.getNameAsString());


    }

    @Test
    void deleteCollection() throws ApiException {
        Collection collection = client.createCollection(Collection.CreateParams.create("test"));
        int total = client.listCollections().size();
        assertEquals(1, total);
        client.deleteCollection(collection);
        int totalAfterDeletion = client.listCollections().size();
        assertEquals(0, totalAfterDeletion);

    }

}