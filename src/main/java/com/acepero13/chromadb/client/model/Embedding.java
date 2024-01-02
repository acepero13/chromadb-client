package com.acepero13.chromadb.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Embedding {
    private final List<Float> embedding;


    public Embedding(List<Float> embedding) {
        this.embedding = embedding;
    }

    public Embedding(float ...floatArray) {
        List<Float> floatList = new ArrayList<>();

        for (float value : floatArray) {
            floatList.add(value);
        }
        this.embedding = floatList;
    }

    public static List<Embedding> single(float ...vector) {
        return Collections.singletonList(new Embedding(vector));
    }

    public static List<Object> rawList(List<Embedding> embeddings) {
        return embeddings.stream().map(Embedding::rawObject).collect(Collectors.toList());
    }


    public List<Float> raw() {
        return embedding;
    }

    public Object rawObject() {
        return embedding;
    }
}
