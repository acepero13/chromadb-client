package com.acepero13.chromadb.client.model.query.criteria;

import com.acepero13.chromadb.client.exceptions.RequestValidationException;
import com.acepero13.chromadb.client.model.Embedding;
import com.acepero13.chromadb.client.model.EmbeddingFunction;
import com.acepero13.chromadb.client.model.Includes;
import com.acepero13.chromadb.client.model.query.builder.DocumentQuery;
import com.acepero13.chromadb.client.model.query.builder.MetadataQuery;
import com.acepero13.chromadb.client.model.query.condition.document.DocumentCondition;
import com.acepero13.chromadb.client.model.query.condition.metadata.MetadataCondition;
import com.acepero13.chromadb.client.model.query.matchers.DocumentMatcher;
import com.acepero13.chromadb.client.model.query.matchers.MetadataMatcher;
import com.acepero13.chromadb.client.utils.ListUtils;
import com.acepero13.chromadb.client.model.QueryEmbedding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class QueryCriteria {
    private final Map<String, Object> whereMetadata;
    private final Map<String, Object> whereDocument;
    private final int nResults;
    private final List<Includes> includes;
    private final List<Embedding> embeddings;

    private QueryCriteria(Builder builder) {
        this.whereMetadata = builder.whereMetadata.build();
        this.whereDocument = builder.whereDocument.build();
        this.nResults = builder.nResults;
        this.includes = builder.includes;
        this.embeddings = builder.embeddings;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<String, Object> whereMetadata() {
        return whereMetadata;
    }

    public Map<String, Object> whereDocument() {
        return whereDocument;
    }


    public List<QueryEmbedding.IncludeEnum> include() {
        return includes.stream().map(Includes::toRequestInclude).collect(Collectors.toList());
    }

    public Integer nResults() {
        return nResults;
    }

    public List<Object> embeddings() {
        return embeddings.stream().map(Embedding::rawObject).collect(Collectors.toList());
    }

    public boolean hasEmbeddings() {
        return embeddings != null && !embeddings.isEmpty();
    }

    /**
     * ValueError - If you don't provide either query_embeddings or query_texts
     * ValueError - If you provide both query_embeddings and query_texts
     */
    public static class Validator implements CriteriaValidator<QueryEmbedding> {

        public static boolean validate(QueryEmbedding query) throws RequestValidationException {
            var validator = new Validator();
            ValidationResult valid = validator.isValid(query, null);
            if (!valid.isValid()) {
                throw new RequestValidationException(valid.errorMsg());
            }
            return valid.isValid();
        }

        @Override
        public ValidationResult isValid(QueryEmbedding element, EmbeddingFunction embeddingsFunction) {
            if (ListUtils.isEmpty(element.getQueryEmbeddings())) {
                return CriteriaValidator.invalid("You need to either provide the embeddings or the text you want to query");
            }
            return CriteriaValidator.valid();
        }
    }


    public static class Builder {
        private MetadataQuery.Builder whereMetadata = new MetadataQuery.Builder();
        private DocumentQuery.Builder whereDocument = new DocumentQuery.Builder();
        private int nResults = 10;
        private List<Includes> includes = Includes.defaultInclude();
        private List<Embedding> embeddings = new ArrayList<>();


        public Builder whereMetadata(String field, MetadataMatcher matcher) {
            this.whereMetadata = new MetadataQuery().where(field, matcher);
            return this;
        }

        public Builder whereMetadata(MetadataCondition condition) {
            this.whereMetadata = new MetadataQuery().where(condition);
            return this;
        }


        public Builder whereDocument(DocumentMatcher matcher) {
            this.whereDocument = new DocumentQuery().whereDocument(matcher);
            return this;
        }

        public Builder whereDocument(DocumentCondition condition) {
            this.whereDocument = new DocumentQuery().whereDocument(condition);
            return this;
        }


        public Builder withNumberOfResults(int nResults) {
            this.nResults = nResults;
            return this;
        }

        public Builder withEmbeddings(List<Embedding> embeddings) {
            this.embeddings = embeddings;
            return this;
        }

        public Builder include(List<Includes> includes) {
            this.includes = includes;
            return this;
        }


        public QueryCriteria build() {
            return new QueryCriteria(this);
        }


        public Builder include(Includes... includes) {
            return include(List.of(includes));
        }


    }
}
