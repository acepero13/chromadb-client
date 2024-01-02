package com.acepero13.chromadb.client.result;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.acepero13.chromadb.client.model.Documents;
import com.acepero13.chromadb.client.model.Embedding;
import com.acepero13.chromadb.client.model.Metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class QueryResult {
    public static final DistanceRange DEFAULT_RANGE = DistanceRange.between(-0.5f, 0.5f);

    @SerializedName("documents")
    private List<List<String>> documents;
    @SerializedName("embeddings")
    private List<List<Float>> embeddings = new ArrayList<>();
    @SerializedName("ids")
    private List<List<String>> ids;
    @SerializedName("metadatas")
    private List<List<Map<String, Object>>> metadatas;
    @SerializedName("distances")
    private List<List<Float>> distances;

    public static QueryResult of(String json) {
        return new Gson().fromJson(json, QueryResult.class);
    }

    public List<Documents> getDocuments() {
        return documents.stream().map(Documents::new).collect(Collectors.toList());
    }

    public List<Embedding> getEmbeddings() {
        return embeddings.stream().map(Embedding::new).collect(Collectors.toList());
    }

    public List<List<String>> getIds() {
        return ids;
    }

    public List<List<Metadata>> getMetadatas() {
        return metadatas.stream().map(Metadata::of).collect(Collectors.toList());
    }

    public List<List<Float>> getDistances() {
        return distances;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


    public List<List<String>> getIds(DistanceRange range) {
        return filter(ids, range);
    }

    public List<Documents> getDocuments(DistanceRange range) {
        return filter(documents, range).stream().map(Documents::new).collect(Collectors.toList());
    }

    public List<List<Metadata>> getMetadatas(DistanceRange range) {
        return filter(getMetadatas(), range);
    }

    public List<Embedding> getEmbeddings(DistanceRange range) {
        return filter(embeddings, range).stream().map(Embedding::new).collect(Collectors.toList());
    }

    private <T> List<List<T>> filter(List<List<T>> list, DistanceRange range) {
        if (range == null) {
            // If range is null, return the original list of ids
            return list;
        }

        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < distances.size(); i++) {
            List<Float> distanceList = distances.get(i);
            List<T> elements = new ArrayList<>();

            for (int j = 0; j < distanceList.size(); j++) {
                float currentValue = distanceList.get(j);
                if (range.inRange(currentValue)) {
                    if (list.size() > i && list.get(i).size() > j) {
                        elements.add(list.get(i).get(j));
                    } else {
                        // TODO: Log warning
                    }
                }
            }
            if (!elements.isEmpty()) {
                result.add(elements);
            }
        }

        return result;

    }


    public static class DistanceRange {
        private final Float lowerBound;
        private final Float upperBound;
        private final Predicate<Float> predicate;


        private DistanceRange(Float lowerBound, Float upperBound, Predicate<Float> predicate) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.predicate = predicate;
        }

        public static DistanceRange distanceIsLessThan(float limit) {
            return new DistanceRange(null, limit, (current) -> current < limit);
        }

        public static DistanceRange distanceIsBiggerThan(float limit) {
            return new DistanceRange(limit, null, current -> current > limit);
        }

        public static DistanceRange between(float lowerBound, float upperBound) {
            return new DistanceRange(lowerBound, upperBound, current -> current > lowerBound && current < upperBound);
        }

        public static DistanceRange distanceIsEqualTo(float value, double delta) {
            return new DistanceRange(value, value, current -> Math.abs(current - value) < delta);
        }

        public boolean inRange(float value) {
            return predicate.test(value);
        }

        @Override
        public String toString() {
            return "DistanceRange{" +
                    "lowerBound=" + lowerBound +
                    ", upperBound=" + upperBound +
                    '}';
        }


    }

}
