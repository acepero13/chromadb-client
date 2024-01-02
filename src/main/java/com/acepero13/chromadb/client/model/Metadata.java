package com.acepero13.chromadb.client.model;

import java.util.*;
import java.util.stream.Collectors;

public class Metadata {
    public static final Metadata SINGLE_EMPTY = new Metadata(Collections.emptyMap());
    private final Map<String, Object> metadata;
    private static final List<Metadata> EMPTY = Collections.emptyList();

    public Metadata() {
        this.metadata = new HashMap<>();
    }

    public Metadata(Map<String, Object> metadata) {

        this.metadata = metadata;
    }

    public static List<Map<String, Object>> from(List<Metadata> metadatas) {
        return metadatas.stream().map(Metadata::toMap).collect(Collectors.toList());
    }

    public static List<Metadata> single(Metadata metadata) {
        return Collections.singletonList(metadata);
    }

    public static List<Metadata> single(Map<String, Object> metadata) {
        return single(new Metadata(metadata));
    }

    public static List<Metadata> empty() {
        return EMPTY;
    }

    public static List<Metadata> single(String key, Object value) {
        return single(Map.of(key, value));
    }

    public static List<Metadata> of(List<Map<String, Object>> metas) {
        return metas.stream().map(Metadata::new).collect(Collectors.toList());
    }

    public static Metadata of(String key, String value) {
        return new Metadata(Map.of(key, value));
    }

    public static Metadata ofEmpty() {
        return SINGLE_EMPTY;
    }

    public Metadata addMetadata(String key, Object value) {
        this.metadata.put(key, value);
        return this;
    }

    public Map<String, Object> toMap() {
        return this.metadata;
    }

    public <T> Optional<T> get(String key, Class<T> type) {
        if (!metadata.containsKey(key)) {
            return Optional.empty();
        }
        return Optional.of(metadata.get(key))
                .filter(value -> type.isAssignableFrom(value.getClass()))
                .map(type::cast);


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Metadata metadata1 = (Metadata) o;

        return metadata.equals(metadata1.metadata);
    }

    @Override
    public int hashCode() {
        return metadata.hashCode();
    }

    public boolean hasKey(String key) {
        return metadata.containsKey(key);
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "metadata=" + metadata +
                '}';
    }
}
