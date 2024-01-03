package com.acepero13.chromadb.client.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ListUtils {
    private ListUtils() {

    }

    public static boolean isEmpty(List<?> list) {
        return (list == null || list.isEmpty());
    }

    public static boolean allHaveSameLength(List<?>... lists) {
        if (lists == null || lists.length == 0) {
            return true;
        }

        return Arrays.stream(lists)
                .filter(Objects::nonNull)
                .map(List::size)
                .distinct() // Get distinct sizes
                .count() <= 1; // Check if there is only one distinct size
    }

    public static boolean notAllHaveSameLength(List<?> ...lists) {
        return !allHaveSameLength(lists);
    }

    public static boolean isNotEmpty(List<?> list) {
        return !isEmpty(list);
    }

    public static <T> Predicate<List<T>> filterAny( Predicate<T> predicate){
        return list -> list.stream().anyMatch(predicate);
    }

    public static <T> Stream<T> flatten(Predicate<? super T> predicate, List<T> list) {
        return list.stream()
                .filter(predicate);
    }
 }
