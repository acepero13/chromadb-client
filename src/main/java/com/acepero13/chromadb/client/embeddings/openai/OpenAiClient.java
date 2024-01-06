package com.acepero13.chromadb.client.embeddings.openai;

import okhttp3.*;

import java.io.IOException;

public class OpenAiClient {
    static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final String apiKey;
    private final String baseUrl;
    private final OkHttpClient client;

    public OpenAiClient(String apiKey, String baseUrl, OkHttpClient client) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.client = client;
    }

    CreateEmbeddingResponse createEmbedding(CreateEmbeddingRequest req) {
        Request request = new Request.Builder()
                .url(this.baseUrl + "embeddings")
                .post(RequestBody.create(req.json(), JSON))
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return handleResponse(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private CreateEmbeddingResponse handleResponse(Response response) throws IOException {
        if (!response.isSuccessful() || response.body() == null) {
            throw new IOException("Unexpected code " + response);
        }

        String responseData = response.body().string();

        return CreateEmbeddingResponse.of(responseData);
    }
}
