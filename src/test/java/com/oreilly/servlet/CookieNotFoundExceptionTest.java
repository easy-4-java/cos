package com.oreilly.servlet;

import org.junit.Test;
import static org.junit.Assert.*;

public class CookieNotFoundExceptionTest {

    @Test
    public void shouldCreateWithNoMessage() {
        CookieNotFoundException ex = new CookieNotFoundException();
        assertNull(ex.getMessage());
    }

    @Test
    public void shouldCreateWithMessage() {
        CookieNotFoundException ex = new CookieNotFoundException("cookie not found");
        assertEquals("cookie not found", ex.getMessage());
    }
}
