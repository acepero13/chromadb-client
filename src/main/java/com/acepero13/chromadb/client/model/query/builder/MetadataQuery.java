package com.acepero13.chromadb.client.model.query.builder;

import com.acepero13.chromadb.client.model.query.condition.metadata.MetadataCondition;
import com.acepero13.chromadb.client.model.query.matchers.MetadataMatcher;

import java.util.HashMap;
import java.util.Map;

public class MetadataQuery {
    public Builder where(String field, MetadataMatcher matcher) {
        return new Builder(Map.of(field, matcher.buildMap()));
    }

    public Builder where(MetadataCondition condition) {
        return new Builder(condition.build());
    }


    public static class Builder {

        private final Map<String, Object> query;

        public Builder(Map<String, Object> query) {
            this.query = query;
        }

        public Builder() {
            this.query = new HashMap<>();
        }

        public Map<String, Object> build() {
            return query;
        }
    }
}
