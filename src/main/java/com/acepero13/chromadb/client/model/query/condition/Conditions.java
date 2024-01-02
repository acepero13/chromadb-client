package com.acepero13.chromadb.client.model.query.condition;

import com.acepero13.chromadb.client.model.query.Comparison;
import com.acepero13.chromadb.client.model.query.condition.document.DocAndCondition;
import com.acepero13.chromadb.client.model.query.condition.document.DocOrCondition;
import com.acepero13.chromadb.client.model.query.condition.document.DocumentCondition;
import com.acepero13.chromadb.client.model.query.condition.metadata.AndCondition;
import com.acepero13.chromadb.client.model.query.condition.metadata.MetadataCondition;
import com.acepero13.chromadb.client.model.query.condition.metadata.OrCondition;
import com.acepero13.chromadb.client.model.query.matchers.DocumentMatcher;
import com.acepero13.chromadb.client.model.query.matchers.MetadataMatcher;
import com.acepero13.chromadb.client.model.query.matchers.QueryMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Conditions {
    private Conditions() {
    }

    public static AndCondition and(String field, MetadataMatcher metadataMatcher) {
        return new AndConditionImpl(field, metadataMatcher);
    }

    public static AndCondition and(MetadataCondition... conditions) {
        List<Map<String, Object>> all = Stream.of(conditions).map(Condition::build).collect(Collectors.toList());
        return new AndConditionImpl(all);
    }

    public static OrCondition or(String field, MetadataMatcher matcher) {
        return new OrConditionImpl(field, matcher);
    }

    public static OrCondition or(MetadataCondition... conditions) {
        List<Map<String, Object>> all = Stream.of(conditions).map(Condition::build).collect(Collectors.toList());
        return new OrConditionImpl(all);
    }


    public static DocAndCondition and(DocumentMatcher... matchers) {
        List<Map<String, Object>> conditions = Stream.of(matchers)
                .map(QueryMatcher::buildMap)
                .collect(Collectors.toList());
        return new DocumentAndConditionImpl(conditions);
    }

    public static DocAndCondition and(DocumentCondition... conditions) {
        List<Map<String, Object>> all = Stream.of(conditions).map(Condition::build).collect(Collectors.toList());
        return new DocumentAndConditionImpl(all);
    }

    public static DocOrCondition or(DocumentMatcher matcher) {
        return new DocumentOrConditionImpl(matcher);
    }

    public static DocOrCondition or(DocumentCondition... conditions) {
        List<Map<String, Object>> all = Stream.of(conditions).map(Condition::build).collect(Collectors.toList());
        return new DocumentOrConditionImpl(all);
    }


    private static Map<String, Object> buildOrWith(List<Map<String, Object>> conditions) {
        return Map.of(Comparison.Operator.OR.operator(), conditions);
    }

    private static Map<String, Object> buildAndWith(List<Map<String, Object>> conditions) {
        return Map.of(Comparison.Operator.AND.operator(), conditions);
    }

    public static DocOrCondition or(DocumentMatcher... matchers) {
        List<Map<String, Object>> conditions = Stream.of(matchers)
                .map(QueryMatcher::buildMap)
                .collect(Collectors.toList());
        return new DocumentOrConditionImpl(conditions);
    }

    public static MetadataCondition cond(String field, MetadataMatcher matcher) {
        return () -> Map.of(field, matcher.buildMap());
    }

    private static class DocumentOrConditionImpl implements DocOrCondition {
        private final List<Map<String, Object>> conditions;

        private DocumentOrConditionImpl(List<Map<String, Object>> conditions) {
            this.conditions = conditions;
        }

        public DocumentOrConditionImpl(DocumentMatcher matcher) {
            this.conditions = new ArrayList<>();
            this.conditions.add(matcher.buildMap());
        }

        @Override
        public Map<String, Object> build() {
            return buildOrWith(conditions);

        }

        @Override
        public void or(DocumentMatcher matcher) {
            conditions.add(matcher.buildMap());
        }
    }

    private static class DocumentAndConditionImpl implements DocAndCondition {
        private final List<Map<String, Object>> conditions;

        public DocumentAndConditionImpl(List<Map<String, Object>> conditions) {
            this.conditions = new ArrayList<>(conditions);
        }
        @Override
        public Map<String, Object> build() {
            return buildAndWith(conditions);
        }
        @Override
        public void and(DocumentMatcher matcher) {
            conditions.add(matcher.buildMap());
        }
    }

    private static class AndConditionImpl implements AndCondition {
        private final List<Map<String, Object>> conditions;

        public AndConditionImpl(List<Map<String, Object>> conditions) {
            this.conditions = new ArrayList<>(conditions);
        }

        public AndConditionImpl(String field, MetadataMatcher metadataMatcher) {
            this(List.of(Map.of(field, metadataMatcher.buildMap())));
        }

        @Override
        public void and(String field, MetadataMatcher matcher) {
            conditions.add(Map.of(field, matcher.buildMap()));
        }

        @Override
        public Map<String, Object> build() {
            return buildAndWith(conditions);
        }
    }

    private static class OrConditionImpl implements OrCondition {
        private final List<Map<String, Object>> conditions;

        public OrConditionImpl(List<Map<String, Object>> conditions) {
            this.conditions = new ArrayList<>(conditions);
        }

        public OrConditionImpl(String field, MetadataMatcher metadataMatcher) {
            this(List.of(Map.of(field, metadataMatcher.buildMap())));
        }

        @Override
        public void or(String field, MetadataMatcher matcher) {
            conditions.add(Map.of(field, matcher.buildMap()));
        }

        @Override
        public Map<String, Object> build() {
            return buildOrWith(conditions);
        }
    }
}


