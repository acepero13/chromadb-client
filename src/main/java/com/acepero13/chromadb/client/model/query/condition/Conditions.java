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

    /**
     * Creates an AndCondition object for the given field and metadataMatcher.
     *
     * @param field           The name of the field to apply the condition on.
     * @param metadataMatcher The {@link MetadataMatcher} to be used in the condition.
     * @return An instance of {@link AndConditionImpl} with the given parameters.
     */
    public static AndCondition and(String field, MetadataMatcher metadataMatcher) {
        return new AndConditionImpl(field, metadataMatcher);
    }


    /**
     * Creates a new AndCondition that combines multiple MetadataConditions using logical AND operation.
     *
     * @param conditions the list of MetadataConditions to combine
     * @return the resulting AndCondition
     */
    public static MetadataCondition and(MetadataCondition... conditions) {
        List<Map<String, Object>> all = Stream.of(conditions).map(Condition::build).collect(Collectors.toList());
        return new AndConditionImpl(all);
    }

    /**
     * Creates a new `MetadataCondition` that represents an "or" operation between two conditions.
     *
     * @param field   The name of the metadata field to compare.
     * @param matcher The matcher to use for comparing the field values.
     * @return A new `MetadataCondition` object that represents an "or" operation between the given field and matcher.
     */
    public static MetadataCondition or(String field, MetadataMatcher matcher) {
        return new OrConditionImpl(field, matcher);
    }

    /**
     * Combines multiple MetadataConditions using logical OR operator.
     *
     * @param conditions array of {@link MetadataCondition}s to be combined with logical OR operator.
     * @return a new {@link OrConditionImpl} instance representing the combination of provided conditions.
     */
    public static MetadataCondition or(MetadataCondition... conditions) {
        List<Map<String, Object>> all = Stream.of(conditions).map(Condition::build).collect(Collectors.toList());
        return new OrConditionImpl(all);
    }


    /**
     * Creates a new {@link DocumentCondition} that represents the logical AND of multiple {@link DocumentMatcher}s.
     *
     * @param matchers The {@link DocumentMatcher}s to combine with an AND operation
     * @return A {@link DocumentCondition} that matches if all given {@link DocumentMatcher}s match
     */
    public static DocumentCondition and(DocumentMatcher... matchers) {
        List<Map<String, Object>> conditions = Stream.of(matchers)
                .map(QueryMatcher::buildMap)
                .collect(Collectors.toList());
        return new DocumentAndConditionImpl(conditions);
    }

    /**
     * Creates a new `DocumentAndCondition` from the given `conditions`.
     * <p>
     * The `conditions` are combined using logical AND operation, meaning that the result of this method will only be true if all of the given conditions are met.
     * </p>
     *
     * @param conditions the conditions to combine
     * @return a new `DocumentAndCondition` representing the combination of the given conditions
     */
    public static DocumentCondition and(DocumentCondition... conditions) {
        List<Map<String, Object>> all = Stream.of(conditions).map(Condition::build).collect(Collectors.toList());
        return new DocumentAndConditionImpl(all);
    }

    /**
     * Creates a new document condition that matches documents that match either of two conditions.
     *
     * @param matcher the first matcher to use
     * @return a new document condition that matches documents that match either of two conditions
     */
    public static DocumentCondition or(DocumentMatcher matcher) {
        return new DocumentOrConditionImpl(matcher);
    }


    /**
     * Creates a new `DocumentOrConditionImpl` instance with the given `conditions`.
     *
     * @param conditions the conditions to be OR'ed together
     * @return a `DocumentOrConditionImpl` instance
     */
    public static DocumentCondition or(DocumentCondition... conditions) {
        List<Map<String, Object>> all = Stream.of(conditions).map(Condition::build).collect(Collectors.toList());
        return new DocumentOrConditionImpl(all);
    }


    private static Map<String, Object> buildOrWith(List<Map<String, Object>> conditions) {
        return Map.of(Comparison.Operator.OR.operator(), conditions);
    }

    private static Map<String, Object> buildAndWith(List<Map<String, Object>> conditions) {
        return Map.of(Comparison.Operator.AND.operator(), conditions);
    }

    /**
     * Returns a new {@code DocumentCondition} that matches if any of the given matchers match.
     *
     * @param matchers The list of matchers to combine using OR logic
     * @return A new {@code DocumentOrConditionImpl} instance
     */
    public static DocumentCondition or(DocumentMatcher... matchers) {
        List<Map<String, Object>> conditions = Stream.of(matchers)
                .map(QueryMatcher::buildMap)
                .collect(Collectors.toList());
        return new DocumentOrConditionImpl(conditions);
    }

    /**
     * Creates a new {@link MetadataCondition} that matches on the given `field` using the specified `matcher`.
     *
     * @param field   The name of the field to match on.
     * @param matcher The {@link MetadataMatcher} to use when matching.
     * @return A new {@link MetadataCondition} that can be used in a {@link MetadataMatcher}.
     */
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
        public Map<String, Object> build() {
            return buildOrWith(conditions);
        }
    }
}


