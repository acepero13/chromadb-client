package com.acepero13.chromadb.client.embeddings.openai;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.acepero13.chromadb.client.model.Embedding;

import java.util.List;
import java.util.stream.Collectors;

final class CreateEmbeddingResponse {
    @SerializedName("data")
    private List<DataItem> data;

    @SerializedName("model")
    private String model;

    @SerializedName("object")
    private String object;

    @SerializedName("usage")
    private Usage usage;

    public static CreateEmbeddingResponse of(String json) {
        return new Gson().fromJson(json, CreateEmbeddingResponse.class);
    }


    public List<DataItem> getData() {
        return data;
    }

    public String getModel() {
        return model;
    }


    public String getObject() {
        return object;
    }


    public Usage getUsage() {
        return usage;
    }

    public List<Embedding> getEmbeddings() {
        return data.stream().map(DataItem::getEmbedding).collect(Collectors.toList());
    }


    public static class DataItem {
        @SerializedName("embedding")
        private List<Double> embedding;

        @SerializedName("index")
        private int index;

        @SerializedName("object")
        private String object;

        // Getter and setter methods

        public List<Double> getEmbeddings() {
            return embedding;
        }

        public Embedding getEmbedding(){
            return new Embedding(embedding.stream().map(Double::floatValue).collect(Collectors.toList()));
        }


        public int getIndex() {
            return index;
        }

        public String getObject() {
            return object;
        }


    }

    public static class Usage {
        @SerializedName("prompt_tokens")
        private int promptTokens;

        @SerializedName("total_tokens")
        private int totalTokens;

        // Getter and setter methods

        public int getPromptTokens() {
            return promptTokens;
        }


        public int getTotalTokens() {
            return totalTokens;
        }

    }
}
