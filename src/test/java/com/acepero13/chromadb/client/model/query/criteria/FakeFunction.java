package com.acepero13.chromadb.client.model.query.criteria;

import com.acepero13.chromadb.client.model.Embedding;
import com.acepero13.chromadb.client.model.EmbeddingFunction;

import java.util.ArrayList;
import java.util.List;

public class FakeFunction implements EmbeddingFunction {
    @Override
    public List<Embedding> createEmbeddings(List<String> documents) {
        return new ArrayList<>();
    }

    @Override
    public List<Embedding> createEmbeddings(List<String> documents, String model) {
        return new ArrayList<>();
    }
}
