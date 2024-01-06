package com.acepero13.chromadb.client.embeddings.openai;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

final class CreateEmbeddingRequest {
    @SerializedName("input")
    private String input;
    @SerializedName("model")
    private String model ;
    @SerializedName("user")
    private String user ;

    public static Builder builder(String input) {
        return new Builder(input);
    }

    public String json() {
        return new Gson().toJson(this, CreateEmbeddingRequest.class);
    }

    public String getInput() {
        return input;
    }

    public String getModel() {
        return model;
    }

    public String getUser() {
        return user;
    }


    public static final class Builder {
        private final String input;
        private String model = "text-embedding-ada-002";
        private String user= "java-client=0.0.1";

        private Builder(String input) {
            this.input = input;
        }


        public Builder withModel(String model) {
            this.model = model;
            return this;
        }

        public Builder withUser(String user) {
            this.user = user;
            return this;
        }

        CreateEmbeddingRequest build() {
            CreateEmbeddingRequest createEmbeddingRequest = new CreateEmbeddingRequest();
            createEmbeddingRequest.user = this.user;
            createEmbeddingRequest.input = this.input;
            createEmbeddingRequest.model = this.model;
            return createEmbeddingRequest;
        }
    }
}
