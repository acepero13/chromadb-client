package com.acepero13.chromadb.client.model.query.criteria;

import com.acepero13.chromadb.client.model.EmbeddingFunction;
import com.acepero13.chromadb.client.model.Includes;
import com.acepero13.chromadb.client.model.Requestable;
import com.acepero13.chromadb.client.model.query.builder.DocumentQuery;
import com.acepero13.chromadb.client.model.query.builder.MetadataQuery;
import com.acepero13.chromadb.client.model.query.condition.document.DocumentCondition;
import com.acepero13.chromadb.client.model.query.condition.metadata.MetadataCondition;
import com.acepero13.chromadb.client.model.query.matchers.DocumentMatcher;
import com.acepero13.chromadb.client.model.query.matchers.MetadataMatcher;
import com.acepero13.chromadb.client.model.GetEmbedding;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetCriteria implements Requestable<GetEmbedding> {
    private final Map<String, Object> whereMetadata;
    private final Map<String, Object> whereDocument;
    private final List<Includes> includes;
    private final Integer limit;
    private final Integer offset;

    public GetCriteria(Builder builder) {
        this.whereDocument = builder.whereDocument.build();
        this.whereMetadata = builder.whereMetadata.build();
        this.includes = builder.includes;
        this.limit = builder.limit;
        this.offset = builder.offset;
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

    public List<GetEmbedding.IncludeEnum> includes() {
        return includes.stream().map(i -> {
            switch (i) {
                case METADATAS:
                    return GetEmbedding.IncludeEnum.METADATAS;
                case DISTANCES:
                    return GetEmbedding.IncludeEnum.DISTANCES;
                case DOCUMENTS:
                    return GetEmbedding.IncludeEnum.DOCUMENTS;
                case EMBEDDINGS:
                    return GetEmbedding.IncludeEnum.EMBEDDINGS;
            }
            return GetEmbedding.IncludeEnum.DOCUMENTS;
        }).collect(Collectors.toList());
    }

    public Integer limit() {
        return this.limit;
    }

    public Integer offset() {
        return this.offset;
    }

    @Override
    public GetEmbedding toRequest(List<String> ids, EmbeddingFunction embeddingFunction) {
        GetEmbedding req = new GetEmbedding();
        if (!ids.isEmpty()) {
            req.ids(ids);
        }
        req.where(whereMetadata());
        req.whereDocument(whereDocument());
        req.include(includes());
        req.limit(limit());
        //req.sort(); // TODO: Sort seems not to be supported
        req.offset(offset());
        return req;
    }

    public static class Builder {
        public Integer limit = 10;
        public Integer offset = 0;
        private MetadataQuery.Builder whereMetadata = new MetadataQuery.Builder();
        private DocumentQuery.Builder whereDocument = new DocumentQuery.Builder();
        private List<Includes> includes = Includes.defaultInclude();

        public Builder whereMetadata(MetadataCondition condition) {
            this.whereMetadata = new MetadataQuery().where(condition);
            return this;
        }

        public Builder whereMetadata(String field, MetadataMatcher matcher) {
            whereMetadata = new MetadataQuery().where(field, matcher);
            return this;
        }

        public Builder whereDocument(DocumentCondition condition) {
            this.whereDocument = new DocumentQuery().whereDocument(condition);
            return this;
        }

        public Builder whereDocument(DocumentMatcher matcher) {
            this.whereDocument = new DocumentQuery().whereDocument(matcher);
            return this;
        }


        public Builder withLimit(int limit) {
            this.limit = limit;
            return this;
        }

        public Builder withOffset(int offset) {
            this.offset = offset;
            return this;
        }

        public Builder include(List<Includes> includes) {
            this.includes = includes;
            return this;
        }

        public Builder include(Includes... includes) {
            return include(List.of(includes));
        }

        public GetCriteria build() {
            return new GetCriteria(this);
        }

    }
}
