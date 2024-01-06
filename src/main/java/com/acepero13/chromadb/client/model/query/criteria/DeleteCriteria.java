package com.acepero13.chromadb.client.model.query.criteria;

import com.acepero13.chromadb.client.exceptions.RequestValidationException;
import com.acepero13.chromadb.client.model.EmbeddingFunction;
import com.acepero13.chromadb.client.model.Requestable;
import com.acepero13.chromadb.client.model.query.builder.DocumentQuery;
import com.acepero13.chromadb.client.model.query.builder.MetadataQuery;
import com.acepero13.chromadb.client.model.query.condition.document.DocumentCondition;
import com.acepero13.chromadb.client.model.query.condition.metadata.MetadataCondition;
import com.acepero13.chromadb.client.model.query.matchers.DocumentMatcher;
import com.acepero13.chromadb.client.model.query.matchers.MetadataMatcher;
import com.acepero13.chromadb.client.utils.ListUtils;
import com.acepero13.chromadb.client.utils.ObjectUtils;
import com.acepero13.chromadb.client.model.DeleteEmbedding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteCriteria implements Requestable<DeleteEmbedding> {
    private final Map<String, Object> whereMetadata;
    private final Map<String, Object> whereDocument;


    public DeleteCriteria(Builder builder) {
        this.whereDocument = builder.whereDocumentBuilder.build();
        this.whereMetadata = builder.whereMetadataBuilder.build();
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

    @Override
    public DeleteEmbedding toRequest(List<String> ids, EmbeddingFunction embeddingFunction) throws RequestValidationException {
        DeleteEmbedding req = new DeleteEmbedding();
        if (ListUtils.isNotEmpty(ids)) {
            req.ids(ids);
        }
        req.where(whereMetadata());
        req.whereDocument(whereDocument());
        Validator.validate(req, embeddingFunction);
        return req;
    }

    /**
     * If ids is empty and whereMetadata is empty and whereDocument is empty
     */
    private static class Validator implements CriteriaValidator<DeleteEmbedding> {
        private static final Validator validator = new Validator();

        private static void validate(DeleteEmbedding element, EmbeddingFunction embeddingsFunction) throws RequestValidationException {
            ValidationResult validation = validator.isValid(element, embeddingsFunction);
            if (!validation.isValid()) {
                throw new RequestValidationException(validation.errorMsg());
            }
        }
        @Override
        public ValidationResult isValid(DeleteEmbedding element, EmbeddingFunction embeddingsFunction) {
            List<String> ids = element.getIds();
            Object whereMetadata = element.getWhere();
            Object whereDocument = element.getWhereDocument();
            if(ListUtils.isEmpty(ids) && ObjectUtils.mapsAreEmpty(whereMetadata, whereDocument)) {
                return CriteriaValidator.invalid("you need to specify either the list of ids to delete or a where clause for documents or metadata");
            }
            return CriteriaValidator.valid();
        }
    }


    public static class Builder {
        private MetadataQuery.Builder whereMetadataBuilder = new MetadataQuery.Builder(new HashMap<>());
        private DocumentQuery.Builder whereDocumentBuilder = new DocumentQuery.Builder(new HashMap<>());


        public Builder whereMetadata(String field, MetadataMatcher matcher) {
            whereMetadataBuilder = new MetadataQuery().where(field, matcher);
            return this;
        }

        public Builder whereMetadata(MetadataCondition condition) {
            whereMetadataBuilder = new MetadataQuery().where(condition);
            return this;
        }

        public Builder whereDocument(DocumentCondition condition) {
            this.whereDocumentBuilder = new DocumentQuery().whereDocument(condition);
            return this;
        }

        public Builder whereDocument(DocumentMatcher matcher) {
            this.whereDocumentBuilder = new DocumentQuery().whereDocument(matcher);
            return this;
        }


        public DeleteCriteria build() {
            return new DeleteCriteria(this);
        }

    }
}
