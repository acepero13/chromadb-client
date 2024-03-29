package com.acepero13.chromadb.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Embedding {
    private final List<Float> embedding;


    public Embedding(List<Float> embedding) {
        this.embedding = embedding;
    }

    public Embedding(float... floatArray) {
        List<Float> floatList = new ArrayList<>();

        for (float value : floatArray) {
            floatList.add(value);
        }
        this.embedding = floatList;
    }

    /**
     * Returns a list containing a single {@link Embedding} object with the given vector.
     *
     * @param vector the vector for the embedding
     * @return a list containing a single {@link Embedding} object with the given vector
     */
    public static List<Embedding> single(float... vector) {
        return Collections.singletonList(new Embedding(vector));
    }

    /**
     * Returns a list of raw objects from a list of embeddings.
     *
     * @param embeddings the list of {@link Embedding}s
     * @return a list of {@link Object}s
     */
    public static List<Object> rawList(List<Embedding> embeddings) {
        return embeddings.stream().map(Embedding::rawObject).collect(Collectors.toList());
    }

    public static Embedding of(float ...vector) {
        return new Embedding(vector);
    }


    /**
     * Returns the raw embeddings of the words in the vocabulary.
     *
     * @return A list of float values representing the raw embeddings for each word in the vocabulary.
     */
    public List<Float> raw() {
        return embedding;
    }

    /**
     * Returns the raw object that this {@code Embedding} is wrapping.
     *
     * @return the raw object that this {@code Embedding} is wrapping.
     */
    public Object rawObject() {
        return embedding;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Embedding embedding1 = (Embedding) o;

        return embedding.equals(embedding1.embedding);
    }

    @Override
    public int hashCode() {
        return embedding.hashCode();
    }

    @Override
    public String toString() {
        return "Embedding{" +
                "embedding=" + embedding +
                '}';
    }
}
