package com.acepero13.chromadb.client.utils;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class ObjectUtils {
    private ObjectUtils() {
    }

    public static boolean mapsAreEmpty(Object... maps) {
        return Stream.of(maps)
                .filter(Objects::nonNull)
                .filter(obj -> obj instanceof Map)
                .map(Map.class::cast)
                .allMatch(Map::isEmpty);
    }
}
