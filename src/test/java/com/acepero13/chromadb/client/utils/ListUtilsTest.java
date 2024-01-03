package com.acepero13.chromadb.client.utils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ListUtilsTest {
    @Test void testFilterById(){
        List<List<Integer>> ids = List.of(List.of(1, 2, 3), List.of(1, 5, 6));

        List<List<Integer>> result = ids.stream()
                .filter(ListUtils.filterAny(i -> i > 3))
                .collect(Collectors.toList());

        assertEquals(List.of(List.of(1, 5, 6)), result);

    }

    @Test void testAllHaveSameLength(){
        assertTrue(ListUtils.allHaveSameLength(List.of(1, 2), List.of(3,4)));
    }

    @Test void testOneArrayIsSmaller(){
        assertFalse(ListUtils.allHaveSameLength(List.of(1, 2), List.of(3)));
    }

    @Test void testNull(){
        assertTrue(ListUtils.allHaveSameLength());
    }

    @Test void testFlatten(){
        List<List<Integer>> ids = List.of(List.of(1, 2, 4), List.of(1, 5, 6));

        List<Integer> result = ids.stream()
                .flatMap(list -> ListUtils.flatten(i -> i > 3, list))
                .collect(Collectors.toList());

        assertEquals(List.of(4, 5, 6), result);

    }

}