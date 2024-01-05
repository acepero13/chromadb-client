package com.acepero13.chromadb.client;

import com.acepero13.chromadb.client.handler.ApiException;
import com.acepero13.chromadb.client.handler.DefaultApi;
import com.acepero13.chromadb.client.model.*;
import com.google.gson.internal.LinkedTreeMap;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DbClientTest {
    private final DefaultApi api = Mockito.mock(DefaultApi.class);
    private final DbClient client = new DbClient(api);

    @Test
    void createCollection() throws ApiException {
        CreateCollection expected = mockCreateCollection("test", "collectionId");
        Collection collection = client.createCollection("test");

        verify(api).createCollection(eq(expected));

        assertEquals("test", collection.getName().getName());
        assertEquals("collectionId", collection.getId());
    }

    @Test
    void createCollectionFromName() throws ApiException {
        CreateCollection expected = mockCreateCollection("name", "fromName");
        Collection collection = client.createCollection(CollectionName.of("name"));

        verify(api).createCollection(eq(expected));

        assertEquals("name", collection.getName().getName());
        assertEquals("fromName", collection.getId());
    }

    @Test
    void getCollection() throws ApiException {
        mockCreateCollection("name", "fromName");
        Collection collection = client.getCollection(CollectionName.of("name"), null);

        verify(api).getCollection(eq("name"));

        assertEquals("name", collection.getName().getName());
        assertEquals("fromName", collection.getId());
    }

    @Test
    void deleteCollection() throws ApiException {
        Collection collection = createCollectionHelper("id", "name");
        client.deleteCollection(collection);
        verify(api).deleteCollection("name");
    }

    @NotNull
    private Collection createCollectionHelper(String id, String name) {
        return new Collection(api, id, Metadata.SINGLE_EMPTY, CollectionName.of(name), null);
    }

    @Test void heartBeat() throws ApiException {
        client.heartbeat();
        verify(api).heartbeat();
    }

    @Test void version() throws ApiException {
        client.version();
        verify(api).version();
    }

    @Test void reset() throws ApiException {
        client.reset();
        verify(api).reset();
    }

    @Test void listCollections() throws ApiException {
        CreateCollection expected = mockCreateCollection("name", "id");
        LinkedTreeMap<String, Object> result = new LinkedTreeMap<>();
        result.put("name", "name");
        when(api.listCollections()).thenReturn(List.of(result));
        List<Collection> collections = client.listCollections();
        assertEquals(1, collections.size());
    }

    @Test void deleteAllCollections() throws ApiException {
        CreateCollection expected = mockCreateCollection("name", "id");
        LinkedTreeMap<String, Object> result = new LinkedTreeMap<>();
        result.put("name", "name");
        when(api.listCollections()).thenReturn(List.of(result));
        client.deleteAllCollections();
        verify(api).deleteCollection("name");
    }

    @Test void deleteAllCollectionsRaisesException() throws ApiException, MalformedURLException {

        CreateCollection expected = mockCreateCollection("name", "id");
        LinkedTreeMap<String, Object> result = new LinkedTreeMap<>();
        result.put("name", "name");
        when(api.listCollections()).thenReturn(List.of(result));
        Mockito.doThrow(new ApiException()).when(api).deleteCollection(eq("name"));
        assertThrows(RuntimeException.class, client::deleteAllCollections);

    }

    @Test void createClient() throws MalformedURLException {
        assertNotNull(DbClient.create(new URL("http://localhost:8000")));
    }

    @Test void wrongUrlCannotCreateClient() {
        assertThrows(MalformedURLException.class, () -> {
            DbClient.create(new URL("invalidURL"));
        });
    }

    @Test void update() throws ApiException {
        mockCreateCollection("oldName", "id");
        mockCreateCollection("newName", "id");
        Collection old = createCollectionHelper("id", "oldName");
        client.update(old, CollectionName.of("newName"), Metadata.SINGLE_EMPTY, null);
        UpdateCollection expected = new UpdateCollection();
        expected.setNewName("newName");
        expected.setNewMetadata(Metadata.SINGLE_EMPTY);
        api.updateCollection(eq(expected), eq("id"));

        assertNotNull(client.getCollection(CollectionName.of("newName")));
    }

    private CreateCollection mockCreateCollection(String collectionName, String collectionId) throws ApiException {
        LinkedTreeMap<String, String> resp = new LinkedTreeMap<>();
        Map<String, String> metadata = Map.of("client", "java-client");
        CreateCollection expected = new CreateCollection()
                .name(collectionName)
                .getOrCreate(true)
                .metadata(metadata);
        resp.put("name", collectionName);
        Mockito.when(api.createCollection(any())).thenReturn(resp);
        LinkedTreeMap<String, Object> respCollection = new LinkedTreeMap<>();
        respCollection.put("name", collectionName);
        respCollection.put("id", collectionId);
        respCollection.put("metadata", new LinkedTreeMap<>());

        Mockito.when(api.getCollection(eq(collectionName))).thenReturn(respCollection);
        return expected;
    }

}