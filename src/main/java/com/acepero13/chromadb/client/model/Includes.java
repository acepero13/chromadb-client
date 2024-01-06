package com.acepero13.chromadb.client.model;

import java.util.List;
import java.util.stream.Collectors;

public enum Includes {
    EMBEDDINGS("embeddings"), DOCUMENTS("documents"), METADATAS("metadatas"), DISTANCES("distances");

    private final String name;

    Includes(String name) {
        this.name = name;
    }

    /**
     * Returns a default set of includes
     *
     * @return a list of {@link Includes} representing the default set of includes
     */
    public static List<Includes> defaultInclude() {
        return List.of(DOCUMENTS, METADATAS, DISTANCES);
    }

    /**
     * Returns a list of default included fields as strings.
     *
     * @return A list of default included fields as strings.
     */
    public static List<String> defaultIncludeAsString() {
        return defaultInclude().stream().map(Includes::getName).collect(Collectors.toList());
    }

    private String getName() {
        return name;
    }

    /**
     * Converts this {@link Includes} to its equivalent {@link QueryEmbedding.IncludeEnum} representation.
     *
     * @return the equivalent {@link QueryEmbedding.IncludeEnum} representation of this object.
     */
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
