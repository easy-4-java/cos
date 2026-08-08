package com.oreilly.servlet.multipart;

import org.junit.Test;
import static org.junit.Assert.*;

public class ExceededSizeExceptionTest {

    @Test
    public void shouldCreateWithNoMessage() {
        ExceededSizeException ex = new ExceededSizeException();
        assertNull(ex.getMessage());
    }

    @Test
    public void shouldCreateWithMessage() {
        ExceededSizeException ex = new ExceededSizeException("size exceeded");
        assertEquals("size exceeded", ex.getMessage());
    }

    @Test
    public void shouldCreateWithThrowable() {
        Exception cause = new RuntimeException("cause");
        ExceededSizeException ex = new ExceededSizeException(cause);
        assertEquals(cause, ex.getCause());
    }
}
