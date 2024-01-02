package com.acepero13.chromadb.client.model.query.criteria;

import com.acepero13.chromadb.client.model.Includes;
import com.acepero13.chromadb.client.model.query.condition.Conditions;
import com.acepero13.chromadb.client.model.GetEmbedding;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.acepero13.chromadb.client.model.query.condition.Conditions.cond;
import static com.acepero13.chromadb.client.model.query.condition.Conditions.or;
import static com.acepero13.chromadb.client.model.query.matchers.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GetCriteriaTest {
    @Test void testCriteria(){
        GetCriteria criteria = GetCriteria.builder()
                .whereMetadata("field", in(10, 20))
                .whereDocument(contains("text"))
                .withOffset(10)
                .withLimit(30)
                .include(Includes.METADATAS, Includes.DISTANCES, Includes.DOCUMENTS, Includes.EMBEDDINGS)
                .build();

        GetEmbedding expected = new GetEmbedding();
        expected.ids(List.of("id1", "id2"))
                .where(Map.of("field", Map.of("$in", List.of(10, 20))))
                .whereDocument(Map.of("$contains", "text"))
                .limit(30)
                .offset(10)
                .include(List.of(GetEmbedding.IncludeEnum.METADATAS, GetEmbedding.IncludeEnum.DISTANCES, GetEmbedding.IncludeEnum.DOCUMENTS, GetEmbedding.IncludeEnum.EMBEDDINGS))
                ;

        assertEquals(expected, criteria.toRequest(List.of("id1", "id2"), new FakeFunction()));
    }

    @Test void testWhereMetadataCondition(){
        GetCriteria criteria = GetCriteria.builder()
                .whereMetadata(Conditions.or(cond("field", isEqualTo(10)), cond("field", isEqualTo(20))))
                .build();

        GetEmbedding expected = new GetEmbedding();
        expected.ids(List.of("id1", "id2"))
                .whereDocument(new HashMap<>())
                .limit(10)
                .offset(0)
                .include(List.of(GetEmbedding.IncludeEnum.DOCUMENTS, GetEmbedding.IncludeEnum.METADATAS, GetEmbedding.IncludeEnum.DISTANCES))
                .where(Map.of("$or", List.of(
                        Map.of("field", Map.of("$eq", 10)),
                        Map.of("field", Map.of("$eq", 20)))));

        assertEquals(expected, criteria.toRequest(List.of("id1", "id2"), new FakeFunction()));


    }


    @Test void testWhereDocumentCondition(){
        GetCriteria criteria = GetCriteria.builder()
                .whereDocument(or(contains("text"), notContains("anotherText")))
                .build();

        GetEmbedding expected = new GetEmbedding();
        expected.ids(List.of("id1", "id2"))
                .where(new HashMap<>())
                .limit(10)
                .offset(0)
                .include(List.of(GetEmbedding.IncludeEnum.DOCUMENTS, GetEmbedding.IncludeEnum.METADATAS, GetEmbedding.IncludeEnum.DISTANCES))
                .whereDocument(Map.of("$or", List.of(
                        Map.of("$contains", "text"),
                        Map.of("$not_contains", "anotherText"))));

        assertEquals(expected, criteria.toRequest(List.of("id1", "id2"), new FakeFunction()));


    }

}