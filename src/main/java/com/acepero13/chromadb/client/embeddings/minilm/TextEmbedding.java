package com.acepero13.chromadb.client.embeddings.minilm;

import ai.djl.MalformedModelException;
import ai.djl.huggingface.translator.TextEmbeddingTranslatorFactory;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import com.acepero13.chromadb.client.model.Embedding;
import com.acepero13.chromadb.client.model.EmbeddingFunction;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TextEmbedding implements EmbeddingFunction {
    private final String modelPath;
    String DJL_PATH = "djl://ai.djl.huggingface.pytorch/";

    public TextEmbedding() {
        this(Model.BGE_SMALL_EN_v1_5);
    }

    public TextEmbedding(String modelPath) {
        this.modelPath = modelPath;
    }

    public TextEmbedding(Model model) {
        this(model.getModelPath());
    }

    @Override
    public List<Embedding> createEmbeddings(List<String> documents) {
        return documents.stream().map(this::createFor).collect(Collectors.toList());
    }

    @Override
    public List<Embedding> createEmbeddings(List<String> documents, String model) {
        return createEmbeddings(documents);
    }

    private Embedding createFor(String text) {
        Criteria<String, float[]> criteria = buildModelCriteria();

        try (ZooModel<String, float[]> model = criteria.loadModel();
             Predictor<String, float[]> predictor = model.newPredictor()) {
            float[] res = predictor.predict(text);
            return new Embedding(res);
        } catch (ModelNotFoundException | MalformedModelException | IOException | TranslateException e) {
            System.out.println("e = " + e); // TODO: Log this
            return new Embedding(new ArrayList<>());
            //throw new RuntimeException(e);
        }
    }

    @NotNull
    private Criteria<String, float[]> buildModelCriteria() {
        return
                Criteria.builder()
                        .setTypes(String.class, float[].class)
                        .optModelUrls(DJL_PATH + modelPath)
                        .optEngine("PyTorch")
                        .optTranslatorFactory(new TextEmbeddingTranslatorFactory())
                        .optProgress(new ProgressBar())
                        .build();
    }
}
