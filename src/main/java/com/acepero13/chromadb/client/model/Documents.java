package com.acepero13.chromadb.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Documents {
    private final List<String> documents = new ArrayList<>();

    public Documents() {
    }

    public Documents(List<String> documents) {
        this.documents.addAll(documents);
    }

    public Documents(String... documents) {
        this.documents.addAll(List.of(documents));
    }

    public static Documents empty() {
        return new Documents();
    }

    public static Documents single(String document) {
        return new Documents(document);
    }

    public static Documents of(String ...values) {
        return new Documents(values);
    }

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

    public List<String> asList() {
        return Collections.unmodifiableList(documents);
    }

    public boolean contains(String msg) {
        return documents.contains(msg);
    }

    public int total() {
        return documents.size();
    }

    @Override
    public String toString() {
        return documents.toString();
    }
}
