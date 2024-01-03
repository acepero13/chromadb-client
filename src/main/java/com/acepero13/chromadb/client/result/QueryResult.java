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
import java.util.stream.IntStream;

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
        var filter = new ListFilter<T>(range, distances);
        return filter.filterByDistanceRange(list);

    }

    private static class ListFilter<T> {
        private final DistanceRange range;
        private final List<List<Float>> distances;

        private ListFilter(DistanceRange range, List<List<Float>> distances){
            this.range = range;
            this.distances = distances;
        }
        private List<List<T>> filterByDistanceRange(List<List<T>> list) {
            if (!isValidRange()) {
                return list;
            }

            return IntStream.range(0, distances.size())
                    .mapToObj(i -> filterElements(list, distances.get(i), i, range))
                    .filter(Predicate.not(List::isEmpty))
                    .collect(Collectors.toList());

        }

        private  List<T> filterElements(List<List<T>> list, List<Float> distanceList, int index, DistanceRange range) {
            return IntStream.range(0, distanceList.size())
                    .filter(j -> range.inRange(distanceList.get(j)))
                    .filter(j -> list.size() > index && list.get(index).size() > j)
                    .mapToObj(j -> list.get(index).get(j))
                    .collect(Collectors.toList());
        }

        private  boolean isValidRange() {
            return range != null;
        }
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
