package com.acepero13.chromadb.client.model.query.criteria;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CriteriaValidatorTest {
    @Test void testValid(){
        CriteriaValidator.ValidationResult valid = CriteriaValidator.valid();
        assertTrue(valid.isValid());
        assertTrue(valid.errorMsg().isEmpty());
    }

    @Test void testInvalid(){
        CriteriaValidator.ValidationResult valid = CriteriaValidator.invalid("Error");
        assertFalse(valid.isValid());
        assertEquals("Error", valid.errorMsg());
    }

}