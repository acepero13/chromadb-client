package com.acepero13.chromadb.client.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CollectionNameTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "valid_name",
            "valid-name-123",
            "valid_name_123",
            "a.b.c.d.e.f.g.h.i.j.k.l.m.n.o.p.q.r.s.t.u.v.w.x.y.z",
            "abc",
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            "a.b.c-d_e",
            "1validname",
            "myJavaTestCollection2",
    })
    void validNames(String name) {
        assertDoesNotThrow(() -> CollectionName.of(name));
    }

    @ParameterizedTest
    @CsvSource({
            "invalid_name_, Name must start and end with a lowercase or a digit",
            "invalid..name, Name must not contain consecutive dots",
            "InvalidName, Name must start and end with a lowercase or a digit",
            ".invalidname, Name must start and end with a lowercase or a digit",
            "invalidname., Name must start and end with a lowercase or a digit",
            "invalid_name_with_very_long_length_invalid_name_with_very_long_length_invalid_name_with_very_long_length, The length of the name must be between 3 and 63 characters",
            ", Name cannot be null or empty",
            " , Name cannot be null or empty",
            "ab, The length of the name must be between 3 and 63 characters"
    })
    void invalidNames(String name, String expectedReason) {

        RuntimeException exception = assertThrows(RuntimeException.class, () -> CollectionName.of(name));

        // Assert that the exception message contains the expected error reason
        assertEquals("Collection name is not valid. Reason: " + expectedReason,  exception.getMessage());
    }

    @Test void testToString(){
        var name = CollectionName.of("name");
        assertEquals("name", name.toString());
    }


}