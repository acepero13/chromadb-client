# Chroma Vector Database Java Client

This is a basic implementation of a java client for the Chroma Vector Database API

This project is heavily inspired in [chromadb-java-client](https://github.com/amikos-tech/chromadb-java-client) project. 

It tries to provide a more user-friendly API for working within java with chromaDB instance. 
Besides that, it supports Sentence Transformers via djl

## Features

### Embeddings Support

- [x] OpenAI API
- [x] Sentence Transformers (Hugging Face)

### Feature Parity with ChromaDB API

- [x] Reset
- [x] Heartbeat
- [x] List Collections
- [ ] Raw SQL
- [x] Get Version
- [x] Create Collection
- [x] Delete Collection
- [x] Collection Add
- [x] Collection Get (partial without additional parameters)
- [x] Collection Count
- [x] Collection Query
- [x] Collection Modify
- [x] Collection Update
- [x] Collection Upsert
- [x] Collection Create Index
- [x] Collection Delete - delete documents in collection

## Usage

Ensure you have a running instance of Chroma running. You can check in the documentation:

- Official documentation - https://docs.trychroma.com/usage-guide#running-chroma-in-clientserver-mode


### Example Default Embedding Function

In this example the default embeddings function (_BAAI/bge-small-en-v1.5_) is used to generate embeddings for our documents.

```java
public class Main {
    public static void main(String[] args) {
        DbClient client = DbClient.create("http://localhost:8000");
        Collection collection = client.createCollection("test-collection");

        collection.add(List.of("1", "2"), AddCriteria.builder()
                .withDocuments("Hello, my name is John. I am a Data Scientist.", "Hello, my name is Bond. I am a Spy.")
                .withMetadata(Metadata.of("type", "scientist"), Metadata.of("type", "spy"))
                .build());

        QueryResponse<QueryResult> response = collection.query("Who is the spy");
    }
}

```


The above should output:

```Bash
{"documents":[["Hello, my name is Bond. I am a Spy.","Hello, my name is John. I am a Data Scientist."]],"embeddings":[],"ids":[["2","1"]],"metadatas":[[{"type":"spy"},{"type":"scientist"}]],"distances":[[0.47604156,0.9098707]]}
```

It is also possible to build complex queries using a fluent interface:

```java
public class Main {
    public static void main(String[] args) {
        DbClient client = DbClient.create("http://localhost:8000");
        Collection collection = client.createCollection("test-collection");

        collection.add(List.of("1", "2"), AddCriteria.builder()
                .withDocuments("Hello, my name is John. I am a Data Scientist.", "Hello, my name is Bond. I am a Spy.")
                .withMetadata(Metadata.of("type", "scientist"), Metadata.of("type", "spy"))
                .build());

        QueryCriteria criteria = QueryCriteria.builder()
                .whereMetadata(
                        or(
                                cond("type", isEqualTo("scientist")), cond("type", isEqualTo("driver"))
                        )
                )
                .whereDocument(or(contains("Scientist"), (contains("Spy"))))
                .withNumberOfResults(5)
                .build();
        
        // Also possible:
        // QueryCriteria.builder().whereMetadata("type", isEqualTo("random"))
        
        QueryResponse<QueryResult> result = collection.query("Who is the spy", criteria);
    }
}

```