package com.acepero13.chromadb.client.model;

import com.acepero13.chromadb.client.model.QueryEmbedding;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IncludesTest {
    @Test
    void testDefaultInclude() {
        List<Includes> defaultIncludes = Includes.defaultInclude();

        assertEquals(3, defaultIncludes.size());
        assertEquals(List.of(Includes.DOCUMENTS, Includes.METADATAS, Includes.DISTANCES), defaultIncludes);
    }

    @Test
    void testDefaultIncludeAsString() {
        List<String> defaultIncludeStrings = Includes.defaultIncludeAsString();

        assertEquals(3, defaultIncludeStrings.size());
        assertEquals(List.of("documents", "metadatas", "distances"), defaultIncludeStrings);
    }

    @Test
    void testToRequestInclude() {
        assertEquals(QueryEmbedding.IncludeEnum.DOCUMENTS, Includes.DOCUMENTS.toRequestInclude());
        assertEquals(QueryEmbedding.IncludeEnum.METADATAS, Includes.METADATAS.toRequestInclude());
        assertEquals(QueryEmbedding.IncludeEnum.DISTANCES, Includes.DISTANCES.toRequestInclude());
        assertEquals(QueryEmbedding.IncludeEnum.EMBEDDINGS, Includes.EMBEDDINGS.toRequestInclude());

    }
}