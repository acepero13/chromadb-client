package com.acepero13.chromadb.client.result;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.acepero13.chromadb.client.model.Documents;
import com.acepero13.chromadb.client.model.Metadata;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetResult {
    @SerializedName("documents")
    private List<String> documents;
    @SerializedName("embeddings")
    private List<Float> embeddings;
    @SerializedName("ids")
    private List<String> ids;
    @SerializedName("metadatas")
    private List<Map<String, Object>> metadatas;

    public Documents getDocuments() {
        return new Documents(documents);
    }

    public List<Float> getEmbeddings() {
        return embeddings;
    }

    public List<String> getIds() {
        return ids;
    }

    public List<Metadata> getMetadatas() {
        return metadatas.stream().map(Metadata::new).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
