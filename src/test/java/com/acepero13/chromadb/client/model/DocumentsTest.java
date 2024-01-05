package com.acepero13.chromadb.client.model;

import org.junit.jupiter.api.Test;

import javax.print.Doc;

import static org.junit.jupiter.api.Assertions.*;

class DocumentsTest {
    @Test void empty(){
        assertEquals(0, Documents.empty().total());
    }

    @Test void testToString(){
        assertEquals("[1, 2]", Documents.of("1", "2").toString());
    }

    @Test void contains(){
        assertTrue(Documents.of("Hello", "World").contains("World"));
    }

    @Test void testHashCode(){
        assertEquals(Documents.of("text").hashCode(), Documents.of("text").hashCode());
    }

}