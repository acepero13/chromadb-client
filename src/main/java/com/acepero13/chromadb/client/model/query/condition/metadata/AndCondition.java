package com.acepero13.chromadb.client.model.query.condition.metadata;

import com.acepero13.chromadb.client.model.query.matchers.MetadataMatcher;

public interface AndCondition extends MetadataCondition {
    void and(String field, MetadataMatcher matcher);
}
