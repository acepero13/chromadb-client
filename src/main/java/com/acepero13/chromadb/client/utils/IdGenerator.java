package com.acepero13.chromadb.client.utils;

import com.acepero13.chromadb.client.model.Embedding;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

public interface IdGenerator {


    List<String> generate(List<Embedding> embeddings);



    static IdGenerator defaultIdGenerator() {
        return new IdGenerator() {
            private final static int MAX_LENGTH = 50;

            @Override
            public List<String> generate(List<Embedding> embeddings) {
                return embeddings.stream()
                        .map(this::generateUniqueIdFor)
                        .collect(Collectors.toList());
            }

            private String generateUniqueIdFor(Embedding embedding) {
                String listAsString = embedding.raw().toString();

                try {
                    return generateUsingSha256(listAsString);
                } catch (NoSuchAlgorithmException e) {
                    throw  new RuntimeException("Cannot generate unique ids because SHA-256 algorithm does not exist in the current classpath. Consider using  .add(List<String> ids, AddCriteria params) ");
                }


            }



            private String generateUsingSha256(String listAsString) throws NoSuchAlgorithmException {
                MessageDigest digest;
                digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(listAsString.getBytes());

                // Convert byte array to a hexadecimal string
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) {
                        hexString.append('0');
                    }
                    hexString.append(hex);
                }

                // Ensure the length is between 3 and 50 characters
                return hexString.length() > MAX_LENGTH ? hexString.substring(0, MAX_LENGTH) : hexString.toString();
            }
        };
    }


}
