package com.oreilly.servlet;

import org.junit.Test;
import static org.junit.Assert.*;

public class ParameterNotFoundExceptionTest {

    @Test
    public void shouldCreateWithNoMessage() {
        ParameterNotFoundException ex = new ParameterNotFoundException();
        assertNull(ex.getMessage());
    }

    @Test
    public void shouldCreateWithMessage() {
        ParameterNotFoundException ex = new ParameterNotFoundException("param not found");
        assertEquals("param not found", ex.getMessage());
    }
}
