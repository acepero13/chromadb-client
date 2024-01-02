package com.acepero13.chromadb.client.model;

import com.acepero13.chromadb.client.handler.ApiException;

import java.util.List;

public interface Requestable<T> {
    T toRequest(List<String> ids, EmbeddingFunction embeddingFunction) throws ApiException;
}
