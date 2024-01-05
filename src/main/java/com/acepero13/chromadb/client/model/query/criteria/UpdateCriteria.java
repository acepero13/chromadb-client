package com.acepero13.chromadb.client.model.query.criteria;

import com.acepero13.chromadb.client.model.*;
import com.acepero13.chromadb.client.handler.ApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateCriteria implements Requestable<UpdateEmbedding> {
    private final Documents documents;
    private final List<Embedding> embeddings;
    private final List<Metadata> metadata;

    public UpdateCriteria(Builder builder) {
        this.documents = builder.documents;
        this.embeddings = builder.embeddings;
        this.metadata = builder.metadata;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Documents documents() {
        return documents;
    }

    public List<Object> metadata() {
        return metadata.stream().map(Metadata::toMap).collect(Collectors.toList());
    }

    public List<Object> embeddings() {
        return embeddings.stream().map(Embedding::rawObject).collect(Collectors.toList());
    }

    public boolean hasEmbeddings() {
        return embeddings != null && !embeddings.isEmpty();
    }

    @Override
    public UpdateEmbedding toRequest(List<String> ids, EmbeddingFunction embeddingFunction) throws ApiException {
        UpdateEmbedding req = new UpdateEmbedding();
        if (ids.isEmpty()) {
            throw new ApiException("Ids cannot be null for update");
        }
        List<Object> embeddings = hasEmbeddings()
                ? embeddings()
                : embeddingFunction.createEmbeddingsAsObject(documents());

        req.documents(documents().asList())
                .ids(ids)
                .metadatas(metadata())
                .embeddings(embeddings);
        return req;
    }

    public static class Builder {
        private List<Metadata> metadata = new ArrayList<>();
        private Documents documents = new Documents();
        private List<Embedding> embeddings = new ArrayList<>();

        public Builder withMetadata(List<Metadata> metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder withDocuments(Documents documents) {
            this.documents = documents;
            return this;
        }

        public Builder withEmbeddings(List<Embedding> embeddings) {
            this.embeddings = embeddings;
            return this;
        }

        public UpdateCriteria build() {
            return new UpdateCriteria(this);
        }

        public Builder withEmbeddings(float ...floats) {
            this.embeddings = Collections.singletonList(new Embedding(floats));
            return this;
        }
    }
}
