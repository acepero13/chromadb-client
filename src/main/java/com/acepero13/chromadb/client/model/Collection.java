package com.acepero13.chromadb.client.model;

import com.acepero13.chromadb.client.embeddings.minilm.TextEmbedding;
import com.acepero13.chromadb.client.model.query.criteria.*;
import com.acepero13.chromadb.client.result.GetResult;
import com.acepero13.chromadb.client.result.QueryResponse;
import com.acepero13.chromadb.client.result.QueryResult;
import com.acepero13.chromadb.client.utils.IdGenerator;
import com.acepero13.chromadb.client.handler.ApiException;
import com.acepero13.chromadb.client.handler.DefaultApi;
import com.acepero13.chromadb.client.model.AddEmbedding;
import com.acepero13.chromadb.client.model.CreateCollection;
import com.acepero13.chromadb.client.model.GetEmbedding;
import com.acepero13.chromadb.client.model.QueryEmbedding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class Collection {
    private final DefaultApi api;
    private final String collectionId;
    private final Metadata metadata;
    private final EmbeddingFunction embeddingsFunction;
    private final CollectionName name;


    public Collection(DefaultApi api, String collectionId, Metadata metadata, CollectionName name, EmbeddingFunction embeddingFunction) {
        this.api = api;
        this.collectionId = collectionId;
        this.metadata = metadata;
        this.name = name;
        this.embeddingsFunction = embeddingFunction;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    /**
     * Adds the specified documents to the collection and returns the query response.
     *
     * @param ids    The list of document IDs to add.
     * @param params The {@link AddCriteria} parameters for the add operation.
     * @return The query response containing a boolean indicating whether the add operation was successful.
     * @throws ApiException if an error occurs during the API call.
     */
    public QueryResponse<Boolean> add(List<String> ids, AddCriteria params) throws ApiException {
        return QueryResponse.ofBoolean(this.api.add(params.toRequest(ids, embeddingsFunction), this.collectionId));
    }

    /**
     * Adds the specified documents to the collection and returns the query response.
     * When using this method, the ids will be generated automatically using sha-256 algorithm
     * or using the {@link IdGenerator} function specified in {@link AddCriteria.Builder#withIdGenerator(IdGenerator)}
     *
     * @param params The {@link AddCriteria} parameters for the add operation.
     * @return The query response containing a boolean indicating whether the add operation was successful.
     * @throws ApiException if an error occurs during the API call.
     */
    public QueryResponse<List<String>> add(AddCriteria params) throws ApiException {
        AddCriteria newParams;
        if (params.doesNotHaveIdGenerator()) {
            newParams = AddCriteria.builder(params)
                    .withIdGenerator(IdGenerator.defaultIdGenerator())
                    .build();
        } else {
            newParams = params;
        }

        AddEmbedding request = newParams.toRequest(new ArrayList<>(), embeddingsFunction);
        QueryResponse<Boolean> result = QueryResponse.ofBoolean(this.api.add(request, this.collectionId));
        if(result.isError()) {
            return QueryResponse.failed("Failing adding new elements to collection." + result.error().toString());
        }
        return QueryResponse.ofList(request.getIds());
    }

    /**
     * Retrieves all embeddings from the collection using the GET endpoint
     *
     * @return A QueryResponse object containing a GetResult object with the retrieved embeddings
     * @throws ApiException if there is an error making the API call or parsing the response
     */
    public QueryResponse<GetResult> getAll() throws ApiException {
        return QueryResponse.ofGetResult(this.api.get(new GetEmbedding(), collectionId));
    }


    /**
     * Gets all items that match the given filter.
     *
     * @param filter The filter to apply.
     * @return A QueryResponse containing all items that match the given filter, or a failed query if no items were found.
     * @throws ApiException If there was an error performing the API call.
     */
    public QueryResponse<GetResult> getAll(Predicate<GetResult> filter) throws ApiException {
        return QueryResponse.ofGetResult(this.api.get(new GetEmbedding(), collectionId))
                .payload()
                .filter(filter)
                .map(res -> QueryResponse.of(res, GetResult.class))
                .orElse(QueryResponse.failed("Nothing was found"));
    }

    /**
     * Retrieves an array of objects from the database, each identified by an id in the input list. The method also
     * takes a {@link GetCriteria} object that specifies additional parameters for the request.
     *
     * @param ids        A list of identifiers for the objects to be retrieved
     * @param parameters Additional parameters for the request, such as projection and filtering criteria
     * @return A {@link QueryResponse} containing an array of {@link GetResult} objects
     * @throws ApiException if there is an error in the underlying API call.
     */
    public QueryResponse<GetResult> get(List<String> ids, GetCriteria parameters) throws ApiException {

        return QueryResponse.ofGetResult(this.api.get(parameters.toRequest(ids, embeddingsFunction), collectionId));
    }

    /**
     * Updates a set of documents in the collection using the provided update criteria and parameters.
     *
     * @param ids    the IDs of the documents to be updated
     * @param params the update criteria and parameters
     * @return a QueryResponse object containing the result of the operation, which includes the number of updated documents
     * @throws ApiException if the API call fails
     */
    public QueryResponse<Boolean> update(List<String> ids, UpdateCriteria params) throws ApiException {
        return QueryResponse.ofNullable(this.api.update(params.toRequest(ids, embeddingsFunction), collectionId));
    }

    public QueryResponse<Boolean> upsert(List<String> ids, AddCriteria params) throws ApiException {
        return QueryResponse.ofNullable(this.api.upsert(params.toRequest(ids, embeddingsFunction), collectionId));

    }

    /**
     * Queries the index for nearest neighbors using the given texts and parameters.
     *
     * @param texts  The list of text to search with.
     * @param params The query criteria to use when searching.
     * @return A {@link QueryResponse} containing a list of {@link QueryResult} objects.
     * @throws ApiException if there was an error during the API request.
     */
    public QueryResponse<QueryResult> query(List<String> texts, QueryCriteria params) throws ApiException {
        QueryEmbedding req = new QueryEmbedding();
        req.where(params.whereMetadata());
        req.whereDocument(params.whereDocument());
        req.include(params.include());
        req.nResults(params.nResults());
        List<Object> embeddings = params.hasEmbeddings()
                ? params.embeddings()
                : embeddingsFunction.createEmbeddingsAsObject(texts);

        req.queryEmbeddings(embeddings);
        QueryCriteria.Validator.validate(req);

        return QueryResponse.ofResult(this.api.getNearestNeighbors(req, collectionId));
    }

    /**
     * Deletes the specified records from the database.
     *
     * @param ids        a list of record IDs to delete
     * @param parameters the deletion criteria
     * @return a QueryResponse containing the deleted record IDs and any errors that occurred during the operation
     * @throws ApiException if there was an error deleting the records
     */
    public QueryResponse<List<String>> delete(List<String> ids, DeleteCriteria parameters) throws ApiException {

        return QueryResponse.ofList(this.api.delete(parameters.toRequest(ids, embeddingsFunction), collectionId));
    }

    /**
     * Deletes the specified documents from the search index.
     *
     * @param ids the IDs of the documents to delete
     * @return the response object containing the deleted document IDs and their corresponding versions
     * @throws ApiException if there is an error during the deletion process
     */
    public QueryResponse<List<String>> delete(List<String> ids) throws ApiException {
        return delete(ids, new DeleteCriteria.Builder().build());
    }

    /**
     * Deletes one or more records from the database based on the specified criteria.
     *
     * @param parameters the delete criteria
     * @return a {@link QueryResponse} object that contains information about the deleted records
     * @throws ApiException if there is an error while deleting the records
     */
    public QueryResponse<List<String>> delete(DeleteCriteria parameters) throws ApiException {
        return delete(new ArrayList<>(), parameters);
    }

    /**
     * Deletes all elements from the collection and returns a list of their IDs.
     *
     * @throws ApiException if there was an error getting the elements or deleting them.
     */
    public QueryResponse<List<String>> delete() throws ApiException {
        QueryResponse<GetResult> response = getAll();
        if (response.isError()) {
            throw new ApiException("Error could not get all elements." + response.error().orElseThrow());
        }
        List<String> ids = response.payload().map(GetResult::getIds).orElse(new ArrayList<>());
        if (ids.isEmpty()) {
            throw new ApiException("Collection is empty. Nothing to delete");
        }
        return delete(ids, new DeleteCriteria.Builder().build());
    }

    /**
     * Queries the search index using the given query criteria and returns a list of results.
     *
     * @param texts  the texts to be queried
     * @param params the query criteria
     * @return a {@link QueryResponse} containing the query results
     * @throws ApiException if there was an error making the API call
     */
    public QueryResponse<QueryResult> query(String texts, QueryCriteria params) throws ApiException {
        return query(Collections.singletonList(texts), params);
    }

    public QueryResponse<QueryResult> query(String ...texts) throws ApiException {
        return query(List.of(texts), QueryCriteria.builder().build());
    }

    /**
     * Queries the database with the given documents and parameters, returning the query result.
     *
     * @param documents The list of documents to query against.
     * @param params    The query parameters.
     * @return The query response containing the result.
     * @throws ApiException If the query fails.
     */
    public QueryResponse<QueryResult> query(Documents documents, QueryCriteria params) throws ApiException {
        return query(documents.asList(), params);
    }

    /**
     * Queries the database with the given documents, returning the query result. It will use the default values as query parameters
     *
     * @param documents The list of documents to query against.
     * @return The query response containing the result.
     * @throws ApiException If the query fails.
     */
    public QueryResponse<QueryResult> query(Documents documents) throws ApiException {
        return query(documents.asList(), QueryCriteria.builder().build());
    }

    /**
     * Retrieves the number of documents in this collection.
     *
     * @return A QueryResponse object containing the number of documents in this collection
     * @throws ApiException If there was an error communicating with the database
     */
    public QueryResponse<Integer> count() throws ApiException {
        return QueryResponse.ofInteger(this.api.count(this.collectionId));
    }

    /**
     * Returns the name of the collection.
     *
     * @return The name of the collection.
     */
    public CollectionName getName() {
        return name;
    }

    /**
     * Returns the ID of this collection
     *
     * @return The ID of this collection.
     */
    public String getId() {
        return collectionId;
    }

    /**
     * Returns the name of the person as a String object.
     *
     * @return the name of the person as a String object.
     */
    public String getNameAsString() {
        return name.getName();
    }

    public static class CreateParams {
        private final CollectionName name;
        private Metadata metadata = new Metadata().addMetadata("client", "java-client"); // Metadata cannot be empty
        private boolean getOrCreate = true;
        private EmbeddingFunction embeddingsFunction = EmbeddingFunction.defaultFunction();

        public CreateParams(CollectionName collectionName) {
            this.name = collectionName;
        }

        /**
         * Sets the metadata for the params object.
         *
         * @param metadata the metadata to set
         * @return this CreateParams object
         */
        public CreateParams withMetadata(Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        /**
         * Sets whether the request should get an existing item if it exists or create a new one
         *
         * @return this instance, useful for chaining multiple calls together
         */
        public CreateParams shouldGetIfExistsOrCreate() {
            this.getOrCreate = true;
            return this;
        }

        /**
         * Sets whether or not to get the data from cache if it exists, but only for this specific instance of CreateParams.
         *
         * @return This instance of CreateParams for chaining.
         */
        public CreateParams shouldOnlyGetIfExists() {
            this.getOrCreate = false;
            return this;
        }

        /**
         * Creates a new collection request with the specified name, getOrCreate option, and metadata.
         *
         * @return CreateCollection request object
         */
        public CreateCollection request() {
            CreateCollection req = new CreateCollection();
            req.setName(name.getName());
            req.setGetOrCreate(getOrCreate);
            req.setMetadata(metadata.toMap());
            return req;
        }

        /**
         * Returns an {@link EmbeddingFunction} object that can be used to embed data into a dense vector space using word2vec or other embedding techniques.
         */
        public EmbeddingFunction embeddingsFunction() {
            return embeddingsFunction;
        }

        public static CreateParams create(CollectionName collectionName) {
            return new CreateParams(collectionName);
        }

        public static CreateParams create(String collectionName) {
            return new CreateParams(CollectionName.of(collectionName));
        }


        public CreateParams withEmbeddingFunction(EmbeddingFunction embeddingFunction) {
            this.embeddingsFunction = embeddingFunction;
            return this;
        }
    }
}
