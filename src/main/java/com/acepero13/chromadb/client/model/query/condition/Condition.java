package com.acepero13.chromadb.client.model.query.condition;

import java.util.Map;

public interface Condition {
    Map<String, Object> build();
}
