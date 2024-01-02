package com.acepero13.chromadb.client.model;

import com.acepero13.chromadb.client.model.CreateCollection;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateParamsTest {
    @Test
    void createRequestExistsOrCreate() {
        CreateCollection actual = new Collection.CreateParams(CollectionName.of("name"))
                .withMetadata(Metadata.of("meta", "value"))
                .shouldGetIfExistsOrCreate()
                .withEmbeddingFunction(null)
                .request();

        CreateCollection expected = new CreateCollection()
                .getOrCreate(true)
                .name("name")
                .metadata(Map.of("meta", "value"));

        assertEquals(expected, actual);
    }

    @Test
    void createDoesNotCreateIfNotExists() {
        CreateCollection actual = new Collection.CreateParams(CollectionName.of("name"))
                .withMetadata(Metadata.of("meta", "value"))
                .shouldOnlyGetIfExists()
                .withEmbeddingFunction(null)
                .request();

        CreateCollection expected = new CreateCollection()
                .getOrCreate(false)
                .name("name")
                .metadata(Map.of("meta", "value"));

        assertEquals(expected, actual);
    }

}