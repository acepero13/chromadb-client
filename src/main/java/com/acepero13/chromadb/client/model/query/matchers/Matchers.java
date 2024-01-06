package com.acepero13.chromadb.client.model.query.matchers;

import com.acepero13.chromadb.client.model.query.Comparison;
import com.acepero13.chromadb.client.model.query.Comparison.Operator;

import java.util.List;

public class Matchers {
    private Matchers() {
    }

    public static MetadataMatcher isEqualTo(String value) {
        return createMatcher(Operator.EQUAL, value);
    }

    public static MetadataMatcher isEqualTo(Integer value) {
        return createMatcher(Operator.EQUAL, value);
    }

    public static MetadataMatcher isEqualTo(Float value) {
        return createMatcher(Operator.EQUAL, value);
    }

    public static MetadataMatcher isNotEqualTo(String value) {
        return createMatcher(Operator.NOT_EQ, value);
    }

    public static MetadataMatcher isNotEqualTo(Integer value) {
        return createMatcher(Operator.NOT_EQ, value);
    }

    public static MetadataMatcher isNotEqualTo(Float value) {
        return createMatcher(Operator.NOT_EQ, value);
    }

    private static MetadataMatcher createMatcher(Operator op, Object value) {
        return () -> new Comparison(op, value);
    }

    public static MetadataMatcher in(String... values) {
        return createMatcher(Operator.IN, List.of(values));
    }

    public static MetadataMatcher in(Integer... values) {
        return createMatcher(Operator.IN, List.of(values));
    }

    public static MetadataMatcher in(Float... values) {
        return createMatcher(Operator.IN, List.of(values));
    }

    public static MetadataMatcher notIn(String... values) {
        return createMatcher(Operator.NOT_IN, List.of(values));
    }

    public static MetadataMatcher notIn(Integer... values) {
        return createMatcher(Operator.NOT_IN, List.of(values));
    }

    public static MetadataMatcher notIn(Float... values) {
        return createMatcher(Operator.NOT_IN, List.of(values));
    }

    public static MetadataMatcher greaterThan(Integer value) {
        return createMatcher(Operator.GT, value);
    }

    public static MetadataMatcher greaterThan(Float value) {
        return createMatcher(Operator.GT, value);
    }


    public static MetadataMatcher greaterThanOrEqualTo(Integer value) {
        return createMatcher(Operator.GTE, value);
    }

    public static MetadataMatcher greaterThanOrEqualTo(Float value) {
        return createMatcher(Operator.GTE, value);
    }

    public static MetadataMatcher lessThan(Integer value) {
        return createMatcher(Operator.LT, value);
    }

    public static MetadataMatcher lessThan(Float value) {
        return createMatcher(Operator.LT, value);
    }

    public static MetadataMatcher lessThanOrEqualTo(Integer value) {
        return createMatcher(Operator.LTE, value);
    }

    public static MetadataMatcher lessThanOrEqualTo(Float value) {
        return createMatcher(Operator.LTE, value);
    }

    public static DocumentMatcher contains(String value) {
        return () -> new Comparison(Operator.CONTAINS, value);
    }

    public static DocumentMatcher notContains(String value) {
        return () -> new Comparison(Operator.NOT_CONTAINS, value);
    }
}
