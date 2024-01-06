package com.acepero13.chromadb.client.embeddings.minilm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    void testModelPath() {
        assertEquals("BAAI/bge-small-en-v1.5", Model.BGE_SMALL_EN_v1_5.getModelPath());
        assertEquals("infgrad/stella-base-en-v2", Model.STELA_BASE_EN_V2.getModelPath());
        assertEquals("thenlper/gte-small", Model.GTE_SMALL.getModelPath());
        assertEquals("sentence-transformers/all-MiniLM-L6-v2", Model.MINI_LM_L6_v2.getModelPath());
    }

    @Test
    void testSizeInGb() {
        assertEquals(0.13, Model.BGE_SMALL_EN_v1_5.getSizeInGb(), 0.01);
        assertEquals(0.22, Model.STELA_BASE_EN_V2.getSizeInGb(), 0.01);
        assertEquals(0.07, Model.GTE_SMALL.getSizeInGb(), 0.01);
        assertEquals(0.09, Model.MINI_LM_L6_v2.getSizeInGb(), 0.01);
    }

    @Test
    void testRetrievalAverage() {
        assertEquals(51.68, Model.BGE_SMALL_EN_v1_5.getRetrievalAverage(), 0.01);
        assertEquals(50.1, Model.STELA_BASE_EN_V2.getRetrievalAverage(), 0.01);
        assertEquals(49.46, Model.GTE_SMALL.getRetrievalAverage(), 0.01);
        assertEquals(42.69, Model.MINI_LM_L6_v2.getRetrievalAverage(), 0.01);
    }

    @Test
    void testDimensions() {
        assertEquals(384, Model.BGE_SMALL_EN_v1_5.getDimensions());
        assertEquals(768, Model.STELA_BASE_EN_V2.getDimensions());
        assertEquals(384, Model.GTE_SMALL.getDimensions());
        assertEquals(384, Model.MINI_LM_L6_v2.getDimensions());
    }

    @Test
    void testToString() {
        assertEquals("Model{modelPath='BAAI/bge-small-en-v1.5', size=0.13, dimensions=384, retrievalAverage=51.68}",
                Model.BGE_SMALL_EN_v1_5.toString());
    }

}