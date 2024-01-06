package com.acepero13.chromadb.client.embeddings.openai;

import com.acepero13.chromadb.client.model.Embedding;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

import static com.acepero13.chromadb.client.embeddings.openai.OpenAiClient.JSON;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class OpenAiEmbeddingsFunctionTest {
    public static final String BASE_URL = "http://fake-open.ai/";
    public static final String API_KEY = "fake";
    private final OkHttpClient mockClient = Mockito.mock(OkHttpClient.class);

    @Test
    void createEmbeddings() throws IOException {
        OpenAiEmbeddingsFunction function = OpenAiEmbeddingsFunction.create(API_KEY, BASE_URL, mockClient);

        String json = CreateEmbeddingRequest.builder("Hello world").build().json();
        Request expected = new Request.Builder()
                .url(BASE_URL + "embeddings")
                .post(RequestBody.create(json, JSON))
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();
        String response = "{\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"embedding\": [\n" +
                "        -0.006929283495992422,\n" +
                "        -0.005336422007530928,\n" +
                "        -4.547132266452536e-05,\n" +
                "        -0.024047505110502243\n" +
                "      ],\n" +
                "      \"index\": 0,\n" +
                "      \"object\": \"embedding\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"model\": \"text-embedding-ada-002\",\n" +
                "  \"object\": \"list\",\n" +
                "  \"usage\": {\n" +
                "    \"prompt_tokens\": 5,\n" +
                "    \"total_tokens\": 5\n" +
                "  }\n" +
                "}";
        Call call = Mockito.mock(Call.class);
        Mockito.when(call.execute()).thenReturn(new Response.Builder()
                .code(200)
                .message("Ok")
                .request(expected)
                .protocol(okhttp3.Protocol.HTTP_1_1)
                .body(ResponseBody.create(response, okhttp3.MediaType.parse("application/json")))
                .build());
        Mockito.when(mockClient.newCall(any())).thenReturn(call);
        List<Embedding> embeddings = function.createEmbeddings(List.of("Hello", "World"));

        List<Float> expectedEmbedding = List.of(-0.0069292835f, -0.005336422f, -4.5471323E-5f, -0.024047505f);
        assertEquals(expectedEmbedding, embeddings.get(0).raw());


    }

    @Test void nullApiKey(){
        RuntimeException error = assertThrows(RuntimeException.class, () -> OpenAiEmbeddingsFunction.create(null, null));
        assertTrue(error.getMessage().contains("Api key cannot be null or empty"));
    }

    @Test void missingApiKey(){
        RuntimeException error = assertThrows(RuntimeException.class, () -> OpenAiEmbeddingsFunction.create("", null));
        assertTrue(error.getMessage().contains("Api key cannot be null or empty"));
    }

    @Test void createsClient(){
        assertNotNull(OpenAiEmbeddingsFunction.create("apiKey"));
    }

    @Test void createsClientWithHttpClient(){
        assertNotNull(OpenAiEmbeddingsFunction.create("apiKey", new OkHttpClient()));
    }

    @Test
    void createEmbeddingsWithModel() throws IOException {
        OpenAiEmbeddingsFunction function = OpenAiEmbeddingsFunction.create(API_KEY, BASE_URL, mockClient);

        String json = CreateEmbeddingRequest.builder("Hello world").build().json();
        Request expected = new Request.Builder()
                .url(BASE_URL + "embeddings")
                .post(RequestBody.create(json, JSON))
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();
        String response = "{\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"embedding\": [\n" +
                "        -0.006929283495992422,\n" +
                "        -0.005336422007530928,\n" +
                "        -4.547132266452536e-05,\n" +
                "        -0.024047505110502243\n" +
                "      ],\n" +
                "      \"index\": 0,\n" +
                "      \"object\": \"embedding\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"model\": \"text-embedding-ada-002\",\n" +
                "  \"object\": \"list\",\n" +
                "  \"usage\": {\n" +
                "    \"prompt_tokens\": 5,\n" +
                "    \"total_tokens\": 5\n" +
                "  }\n" +
                "}";
        Call call = Mockito.mock(Call.class);
        Mockito.when(call.execute()).thenReturn(new Response.Builder()
                .code(200)
                .message("Ok")
                .request(expected)
                .protocol(okhttp3.Protocol.HTTP_1_1)
                .body(ResponseBody.create(response, okhttp3.MediaType.parse("application/json")))
                .build());
        Mockito.when(mockClient.newCall(any())).thenReturn(call);
        List<Embedding> embeddings = function.createEmbeddings(List.of("Hello", "World"), "customModel");

        List<Float> expectedEmbedding = List.of(-0.0069292835f, -0.005336422f, -4.5471323E-5f, -0.024047505f);
        assertEquals(expectedEmbedding, embeddings.get(0).raw());


    }

}