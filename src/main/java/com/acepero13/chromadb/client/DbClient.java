package com.acepero13.chromadb.client;

import com.acepero13.chromadb.client.handler.ApiClient;
import com.acepero13.chromadb.client.handler.ApiException;
import com.acepero13.chromadb.client.handler.DefaultApi;
import com.acepero13.chromadb.client.model.Collection;
import com.acepero13.chromadb.client.model.*;
import com.google.gson.internal.LinkedTreeMap;

import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

@SuppressWarnings("unchecked")
public class DbClient {
    private final DefaultApi api;
    private final Map<String, Collection> collections = new HashMap<>();


    DbClient(DefaultApi api) {
        this.api = api;
    }

    public static DbClient create(URL basePath) {
        var api = new ApiClient();
        api.setBasePath(basePath.toString());
        return new DbClient(new DefaultApi(api));
    }

    public Collection createCollection(Collection.CreateParams createParams) throws ApiException {
        LinkedTreeMap<String, String> resp = (LinkedTreeMap<String, String>) api.createCollection(createParams.request());
        Collection collection = fetch(CollectionName.of(resp.get("name")), createParams.embeddingsFunction());

        this.collections.putIfAbsent(collection.getId(), collection);
        return collection;
    }

    private Collection fetch(CollectionName collectionName, EmbeddingFunction embeddingFunction) throws ApiException {
        LinkedTreeMap<String, Object> respCollection = (LinkedTreeMap<String, Object>) api.getCollection(collectionName.getName());
        CollectionName name = CollectionName.of(respCollection.get("name").toString());
        String collectionId = respCollection.get("id").toString();
        Map<String, Object> metadata = (LinkedTreeMap<String, Object>) respCollection.get("metadata");
        return new Collection(api, collectionId, new Metadata(metadata), name, embeddingFunction);

    }

    public Collection getCollection(CollectionName collectionName, EmbeddingFunction embeddingFunction) throws ApiException {
        return fetch(collectionName, embeddingFunction);
    }

    public Optional<Collection> getCollection(CollectionName collectionName) throws ApiException {
        Collection collection = collections.getOrDefault(collectionName.getName(), getCollection(collectionName, EmbeddingFunction.defaultFunction()));
        return Optional.ofNullable(collection);

    }


    public void deleteCollection(Collection collection) throws ApiException {
        api.deleteCollection(collection.getNameAsString());
        this.collections.remove(collection.getId());
    }

    public Collection update(Collection collection, CollectionName newName, Metadata newMetadata, EmbeddingFunction embeddingFunction) throws ApiException {
        UpdateCollection req = new UpdateCollection();
        String oldName = collection.getName().getName();
        req.setNewName(newName.getName());
        req.setNewMetadata(newMetadata.toMap());
        api.updateCollection(req, collection.getId());
        Collection updatedCollection = fetch(newName, embeddingFunction);
        collections.put(oldName, updatedCollection);
        return updatedCollection;
    }

    public List<Collection> listCollections() throws ApiException {

        List<LinkedTreeMap<String, Object>> apiResponse = (List<LinkedTreeMap<String, Object>>) api.listCollections();
        List<Collection> collectionList = new ArrayList<>();
        for (LinkedTreeMap<String, Object> response : apiResponse) {
            collectionList.add(getCollection(CollectionName.of(response.get("name")), null));
        }
        return collectionList;
    }

    public Map<String, BigDecimal> heartbeat() throws ApiException {
        return api.heartbeat();
    }

    public String version() throws ApiException {
        return api.version();
    }

    public Boolean reset() throws ApiException {
        return api.reset();
    }

    public void deleteAllCollections() throws ApiException {
        listCollections().forEach(col -> {
            try {
                deleteCollection(col);
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
