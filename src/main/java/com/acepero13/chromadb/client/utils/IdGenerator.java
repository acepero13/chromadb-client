package com.acepero13.chromadb.client.utils;

import com.acepero13.chromadb.client.model.Embedding;

import java.util.List;

public interface IdGenerator {


    List<String> generate(List<Embedding> embeddings);


    static IdGenerator defaultIdGenerator() {
        return new Sha256IdGenerator();
    }


}
