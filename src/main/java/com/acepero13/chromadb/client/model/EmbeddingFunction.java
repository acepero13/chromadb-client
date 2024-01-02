package com.acepero13.chromadb.client.model;

import com.acepero13.chromadb.client.embeddings.minilm.TextEmbedding;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface EmbeddingFunction {

    static final TextEmbedding TEXT_EMBEDDING = new TextEmbedding();

    static EmbeddingFunction defaultFunction() {
        return TEXT_EMBEDDING;
    }

    List<Embedding> createEmbeddings(List<String> documents);

    List<Embedding> createEmbeddings(List<String> documents, String model);

    default List<Embedding> createEmbeddings(Documents documents) {
        return createEmbeddings(documents.asList());
    }

    default List<Object> createEmbeddingsAsObject(List<String> texts) {
        return createEmbeddings(texts).stream()
                .map(Embedding::rawObject)
                .collect(Collectors.toList());
    }

    default List<Object> createEmbeddingsAsObject(Documents documents) {
        return createEmbeddings(documents).stream()
                .map(Embedding::rawObject)
                .collect(Collectors.toList());
    }

    default List<Embedding> createEmbeddings(String text) {
        return createEmbeddings(Collections.singletonList(text));
    }
}
