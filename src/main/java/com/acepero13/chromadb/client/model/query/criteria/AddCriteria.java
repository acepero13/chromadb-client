package com.acepero13.chromadb.client.model.query.criteria;

import com.acepero13.chromadb.client.exceptions.RequestValidationException;
import com.acepero13.chromadb.client.model.*;
import com.acepero13.chromadb.client.utils.IdGenerator;
import com.acepero13.chromadb.client.utils.ListUtils;
import com.acepero13.chromadb.client.handler.ApiException;
import com.acepero13.chromadb.client.model.AddEmbedding;

import java.util.*;
import java.util.stream.Collectors;

public class AddCriteria implements Requestable<AddEmbedding> {
    private final Documents documents;
    private final List<Embedding> embeddings;
    private final List<Metadata> metadata;
    private final boolean shouldIncrementIndex;
    private final IdGenerator idGenerator;

    public AddCriteria(Builder builder) {
        this.documents = builder.documents;
        this.embeddings = builder.embeddings;
        this.metadata = builder.metadata;
        this.shouldIncrementIndex = builder.shouldIncrementIndex;
        this.idGenerator = builder.idGenerator;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(AddCriteria params) {
        return new Builder(params);
    }

    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    public boolean hasEmbeddings() {
        return embeddings != null && !embeddings.isEmpty();
    }


    public Documents documents() {
        return documents;
    }

    public List<Map<String, Object>> metadata() {
        return metadata.stream().map(Metadata::toMap).collect(Collectors.toList());

    }

    public boolean shouldIncrementIndex() {
        return shouldIncrementIndex;
    }

    public AddEmbedding toRequest(final List<String> ids, EmbeddingFunction embeddingsFunction) throws ApiException {
        if (ids.isEmpty() && idGenerator == null) {
            throw new ApiException("Ids cannot be empty");
        }
        AddEmbedding request = new AddEmbedding();
        request.documents(documents.asList());

        request.metadatas(Metadata.from(metadata));
        request.incrementIndex(shouldIncrementIndex);
        List<Embedding> embeddingList = hasEmbeddings()
                ? embeddings
                : Objects.requireNonNull(embeddingsFunction, "Embeddings were not specify, therefore, you need to specify an embedding function")
                .createEmbeddings(documents());

        request.embeddings(Embedding.rawList(embeddingList));
        if (ids.isEmpty()) {
            request.setIds(idGenerator.generate(embeddingList));
        } else {
            request.ids(ids);
        }


        Validator.validate(request, embeddingsFunction);

        return request;

    }

    public boolean doesNotHaveIdGenerator() {
        return !hasIdGenerator();
    }

    private boolean hasIdGenerator() {
        return idGenerator != null;
    }

    /**
     * :
     * Error - If you don't provide either embeddings or documents
     * Error - If the length of ids, embeddings, metadatas, or documents don't match
     * Error - If you don't provide an embedding function and don't provide embeddings
     */
    private static class Validator implements CriteriaValidator<AddEmbedding> {

        private static final Validator validator = new Validator();

        private static void validate(AddEmbedding element, EmbeddingFunction embeddingsFunction) throws RequestValidationException {
            ValidationResult validation = validator.isValid(element, embeddingsFunction);
            if (!validation.isValid()) {
                throw new RequestValidationException(validation.errorMsg());
            }
        }

        @Override
        public ValidationResult isValid(AddEmbedding element, EmbeddingFunction embeddingsFunction) {
            List<String> ids = element.getIds();
            List<Object> embeddings = element.getEmbeddings();
            List<String> documents = element.getDocuments();
            if (ListUtils.isEmpty(embeddings) && ListUtils.isEmpty(documents)) { // If you don't provide either embeddings or documents
                return CriteriaValidator.invalid("You must provide either embeddings or a list of documents");
            }
            if (ListUtils.notAllHaveSameLength(ids, embeddings, documents)) { // If the length of ids, embeddings, metadatas, or documents don't match
                return CriteriaValidator.invalid("The length of ids, embeddings, and documents must match");
            }
            return CriteriaValidator.valid();
        }
    }

    public static class Builder {
        private boolean shouldIncrementIndex = true;
        private List<Metadata> metadata = new ArrayList<>();
        private Documents documents = new Documents();
        private List<Embedding> embeddings = new ArrayList<>();
        private IdGenerator idGenerator = null;

        public Builder(AddCriteria params) {
            this.shouldIncrementIndex = params.shouldIncrementIndex;
            this.metadata = params.metadata;
            this.documents = params.documents;
            this.embeddings = params.embeddings;
        }

        public Builder() {

        }

        public Builder withMetadata(List<Metadata> metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder withDocuments(Documents documents) {
            this.documents = documents;
            return this;
        }

        public Builder withDocuments(String... documents) {
            this.documents = new Documents(documents);
            return this;
        }

        public Builder withEmbeddings(List<Embedding> embeddings) {
            this.embeddings = embeddings;
            return this;
        }

        public Builder withIncrementalIndex() {
            this.shouldIncrementIndex = true;
            return this;
        }

        public Builder withNoIncrementalIndex() {
            this.shouldIncrementIndex = false;
            return this;
        }

        public AddCriteria build() {
            return new AddCriteria(this);
        }

        public Builder withEmbeddings(float... floats) {
            this.embeddings = Collections.singletonList(new Embedding(floats));
            return this;
        }

        public Builder withMetadata(Metadata... metadatas) {
            withMetadata(List.of(metadatas));
            return this;
        }

        public Builder withIdGenerator(IdGenerator idGenerator) {
            this.idGenerator = idGenerator;
            return this;
        }
    }
}
