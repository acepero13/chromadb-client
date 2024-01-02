package com.acepero13.chromadb.client.model;

import com.acepero13.chromadb.client.model.QueryEmbedding;

import java.util.List;
import java.util.stream.Collectors;

public enum Includes {
    EMBEDDINGS("embeddings"), DOCUMENTS("documents"), METADATAS("metadatas"), DISTANCES("distances");

    private final String name;

    Includes(String name) {
        this.name = name;
    }

    public static List<Includes> defaultInclude() {
        return List.of(DOCUMENTS, METADATAS, DISTANCES);
    }

    public static List<String> defaultIncludeAsString() {
        return defaultInclude().stream().map(Includes::getName).collect(Collectors.toList());
    }

    private String getName() {
        return name;
    }

    public QueryEmbedding.IncludeEnum toRequestInclude() {
        switch (this) {
            case METADATAS:
                return QueryEmbedding.IncludeEnum.METADATAS;
            case DISTANCES:
                return QueryEmbedding.IncludeEnum.DISTANCES;
            case DOCUMENTS:
                return QueryEmbedding.IncludeEnum.DOCUMENTS;
            case EMBEDDINGS:
                return QueryEmbedding.IncludeEnum.EMBEDDINGS;
        }
        return QueryEmbedding.IncludeEnum.DOCUMENTS;
    }
}
