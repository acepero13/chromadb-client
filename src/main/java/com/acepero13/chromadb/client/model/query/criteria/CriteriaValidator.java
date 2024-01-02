package com.acepero13.chromadb.client.model.query.criteria;

import com.acepero13.chromadb.client.model.EmbeddingFunction;
import com.acepero13.chromadb.client.model.AddEmbedding;

public interface CriteriaValidator<T> {
    ValidationResult isValid(T element, EmbeddingFunction embeddingsFunction);

    static ValidationResult valid(){
        return new ValidationResult() {
            @Override
            public boolean isValid() {
                return true;
            }

            @Override
            public String errorMsg() {
                return "";
            }
        };
    }

    static ValidationResult invalid(String msg) {
        return new ValidationResult() {
            @Override
            public boolean isValid() {
                return false;
            }

            @Override
            public String errorMsg() {
                return msg;
            }
        };
    }

    interface ValidationResult {
        boolean isValid();
        String errorMsg();


    }
}
