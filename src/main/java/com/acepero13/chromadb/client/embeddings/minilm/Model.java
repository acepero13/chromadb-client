package com.acepero13.chromadb.client.embeddings.minilm;

public enum Model {
    BGE_SMALL_EN_v1_5("BAAI/bge-small-en-v1.5", 0.13, 384, 51.68),
    STELA_BASE_EN_V2("infgrad/stella-base-en-v2", 0.22, 768, 50.1),
    GTE_SMALL("thenlper/gte-small", 0.07, 384, 49.46),
    MINI_LM_L6_v2("sentence-transformers/all-MiniLM-L6-v2", 0.09, 384, 42.69);

    private final String modelPath;
    private final double size;
    private final int dimensions;
    private final double retrievalAverage;

    Model(String modelPath, double size, int dimensions, double retrievalAverage) {
        this.modelPath = modelPath;
        this.size = size;
        this.dimensions = dimensions;
        this.retrievalAverage = retrievalAverage;
    }

    public String getModelPath() {
        return modelPath;
    }

    public double getSizeInGb() {
        return size;
    }

    public double getRetrievalAverage() {
        return retrievalAverage;
    }

    public int getDimensions() {
        return dimensions;
    }

    @Override
    public String toString() {
        return "Model{" +
                "modelPath='" + modelPath + '\'' +
                ", size=" + size +
                ", dimensions=" + dimensions +
                ", retrievalAverage=" + retrievalAverage +
                '}';
    }
}
