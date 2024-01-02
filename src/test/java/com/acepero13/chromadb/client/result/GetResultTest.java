package com.acepero13.chromadb.client.result;

import com.google.gson.Gson;
import com.acepero13.chromadb.client.model.Documents;
import com.acepero13.chromadb.client.model.Metadata;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetResultTest {

    @Test
    void testGetDocuments() {
        String json = "{\"documents\":[\"doc1\",\"doc2\"],\"embeddings\":[],\"ids\":[],\"metadatas\":[]}";
        GetResult getResult = new Gson().fromJson(json, GetResult.class);

        Documents documents = getResult.getDocuments();

        assertEquals(2, documents.total());
    }

    @Test
    void testGetEmbeddings() {
        String json = "{\"documents\":[],\"embeddings\":[1.0,2.0,3.0],\"ids\":[],\"metadatas\":[]}";
        GetResult getResult = new Gson().fromJson(json, GetResult.class);

        List<Float> embeddings = getResult.getEmbeddings();

        assertEquals(3, embeddings.size());
    }

    @Test
    void testGetIds() {
        String json = "{\"documents\":[],\"embeddings\":[],\"ids\":[\"id1\",\"id2\"],\"metadatas\":[]}";
        GetResult getResult = new Gson().fromJson(json, GetResult.class);

        List<String> ids = getResult.getIds();

        assertEquals(2, ids.size());
    }

    @Test
    void testGetMetadatas() {
        String json = "{\"documents\":[],\"embeddings\":[],\"ids\":[],\"metadatas\":[{\"key1\":\"value1\"},{\"key2\":\"value2\"}]}";
        GetResult getResult = new Gson().fromJson(json, GetResult.class);

        List<Metadata> metadatas = getResult.getMetadatas();

        assertEquals(2, metadatas.size());
    }

    @Test
    void testToString() {
        String json = "{\"documents\":[],\"embeddings\":[],\"ids\":[],\"metadatas\":[{\"key1\":\"value1\"},{\"key2\":\"value2\"}]}";
        GetResult getResult = new Gson().fromJson(json, GetResult.class);
        String expectedJson = "{\"documents\":[],\"embeddings\":[],\"ids\":[],\"metadatas\":[{\"key1\":\"value1\"},{\"key2\":\"value2\"}]}";

        assertEquals(expectedJson, getResult.toString());
    }
}