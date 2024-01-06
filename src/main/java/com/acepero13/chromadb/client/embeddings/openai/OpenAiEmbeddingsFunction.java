package com.acepero13.chromadb.client.embeddings.openai;

import com.acepero13.chromadb.client.model.Embedding;
import com.acepero13.chromadb.client.model.EmbeddingFunction;
import okhttp3.OkHttpClient;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class OpenAiEmbeddingsFunction implements EmbeddingFunction {
    public static final String BASE_URL = "https://api.openai.com/v1/";
    private final OpenAiClient client;

    public static OpenAiEmbeddingsFunction create(String apiKey, String baseUrl, OkHttpClient client) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("Api key cannot be null or empty");
        }
        return new OpenAiEmbeddingsFunction(new OpenAiClient(apiKey, Objects.requireNonNull(baseUrl), Objects.requireNonNull(client)));
    }

    public static OpenAiEmbeddingsFunction create(String apiKey, OkHttpClient client) {
        return create(apiKey, BASE_URL, client);
    }

    public static OpenAiEmbeddingsFunction create(String apiKey) {
        return create(apiKey, BASE_URL, new OkHttpClient());
    }

    public static OpenAiEmbeddingsFunction createUsingApiKeyFromEnv() {
        return create(System.getenv("OPENAI_API_KEY"), BASE_URL, new OkHttpClient());
    }


    public OpenAiEmbeddingsFunction(OpenAiClient client) {
        this.client = client;
    }

    @Override
    public List<Embedding> createEmbeddings(List<String> documents) {
        return documents.stream()
                .map(doc -> client.createEmbedding(CreateEmbeddingRequest.builder(doc).build()))
                .filter(Objects::nonNull)
                .flatMap(data -> data.getEmbeddings().stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<Embedding> createEmbeddings(List<String> documents, String model) {
        return documents.stream()
                .map(doc -> client.createEmbedding(CreateEmbeddingRequest.builder(doc).withModel(model).build()))
                .filter(Objects::nonNull)
                .flatMap(data -> data.getEmbeddings().stream())
                .collect(Collectors.toList());
    }
}
