package com.acepero13.chromadb.client.model.query;

import java.util.Map;

public class Comparison {

    private final Operator operator;
    private final Object value;

    public Comparison(Operator operator, Object value) {
        this.operator = operator;
        this.value = value;
    }

    public Map<String, Object> build() {
        return Map.of(operator.operator, value);
    }

    public enum Operator {
        EQUAL("$eq"), IN("$in"), NOT_IN("$nin"), NOT_EQ("$ne"), GT("$gt"), GTE("$gte"), LT("$lt"), LTE("$lte"),
        AND("$and"), OR("$or"),
        CONTAINS("$contains"), NOT_CONTAINS("$not_contains");

        private final String operator;

        Operator(String operator) {
            this.operator = operator;
        }

        public String operator() {
            return operator;
        }
    }
}
