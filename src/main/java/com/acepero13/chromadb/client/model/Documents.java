package com.acepero13.chromadb.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Documents {
    private final List<String> documents = new ArrayList<>();

    public Documents() {
    }

    private Documents(String... documents) {
        this.documents.addAll(List.of(documents));
    }

    private Documents(List<String> documents) {
        this.documents.addAll(documents);
    }

    /**
     * Creates and returns an empty {@link Documents} object.
     *
     * @return An empty {@link Documents} object.
     */
    public static Documents empty() {
        return new Documents();
    }

    /**
     * Returns a single {@link Documents} object for the given `document` string.
     *
     * @param document The input string to be processed.
     * @return A single {@link Documents} object containing the processed string.
     */
    public static Documents single(String document) {
        return new Documents(document);
    }

    /**
     * Creates a new `Documents` object from the given list of values.
     *
     * @param values The values to add to the document.
     * @return A new `Documents` object with the given values added.
     */
    public static Documents of(String... values) {
        return new Documents(values);
    }

    public static Documents of(List<String> documents) {
        return new Documents(documents);
    }

    /**
     * Adds the specified document to this instance.
     *
     * @param document The document to add.
     * @return This instance, useful for chaining multiple calls together.
     */
    public Documents add(String document) {
        this.documents.add(document);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Documents documents1 = (Documents) o;

        return documents.equals(documents1.documents);
    }

    @Override
    public int hashCode() {
        return documents.hashCode();
    }

    /**
     * Returns an immutable list of documents.
     *
     * @return an unmodifiable list of documents
     */
    public List<String> asList() {
        return Collections.unmodifiableList(documents);
    }

    /**
     * Checks if the given string is present in the list of documents.
     *
     * @param msg The string to be searched for in the list of documents.
     * @return True if the string is found, false otherwise.
     */
    public boolean contains(String msg) {
        return documents.contains(msg);
    }

    /**
     * Returns the total number of documents in this collection.
     *
     * @return the total number of documents in this collection
     */
    public int total() {
        return documents.size();
    }

    @Override
    public String toString() {
        return documents.toString();
    }
}
