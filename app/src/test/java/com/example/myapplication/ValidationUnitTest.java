package com.example.myapplication;

import com.example.myapplication.utils.Validation;

import junit.framework.TestCase;

public class ValidationUnitTest extends TestCase {
    public void testValidateFieldIsNotEmpty() {
        assertTrue(Validation.validateFields("qweqwe"));
    }

    public void testValidateFieldIsEmpty() {
        assertFalse(Validation.validateFields(""));
    }

    public void testEmailIsValid() {
        assertTrue(Validation.validateEmail("test@example.com"));
    }

    public void testEmailIsInvalid() {
        assertFalse(Validation.validateEmail("123"));
    }

}
