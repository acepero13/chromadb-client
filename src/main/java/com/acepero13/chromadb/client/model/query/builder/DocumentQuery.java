package com.acepero13.chromadb.client.model.query.builder;

import com.acepero13.chromadb.client.model.query.condition.document.DocumentCondition;
import com.acepero13.chromadb.client.model.query.matchers.DocumentMatcher;

import java.util.HashMap;
import java.util.Map;

public class DocumentQuery {
    public Builder whereDocument(DocumentMatcher matcher) {
        return new Builder(matcher.buildMap());
    }

    public Builder whereDocument(DocumentCondition condition) {
        return new Builder(condition.build());
    }


    public static class Builder {
        private final Map<String, Object> query;

        public Builder(Map<String, Object> query) {
            this.query = query;
        }

        public Builder() {
            this(new HashMap<>());
        }

        public Map<String, Object> build() {
            return query;
        }
    }
}
