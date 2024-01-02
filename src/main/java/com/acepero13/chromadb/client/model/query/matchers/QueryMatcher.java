package com.acepero13.chromadb.client.model.query.matchers;

import com.acepero13.chromadb.client.model.query.Comparison;

import java.util.Map;

public interface QueryMatcher {
    Comparison build();

    default Map<String, Object> buildMap() {
        Comparison comparison = build();
        return comparison.build();
    }
}
