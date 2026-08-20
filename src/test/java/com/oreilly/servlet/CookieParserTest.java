package com.oreilly.servlet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class CookieParserTest {

    private HttpServletRequest createMockRequest(Cookie... cookies) {
        return new StubHttpServletRequest() {
            @Override
            public Cookie[] getCookies() {
                return cookies;
            }
        };
    }

    @Test
    public void shouldGetStringCookie() throws CookieNotFoundException {
        Cookie cookie = new Cookie("name", "value");
        CookieParser parser = new CookieParser(createMockRequest(cookie));
        assertEquals("value", parser.getStringCookie("name"));
    }

    @Test(expected = CookieNotFoundException.class)
    public void shouldThrowWhenCookieNotFound() throws CookieNotFoundException {
        CookieParser parser = new CookieParser(createMockRequest());
        parser.getStringCookie("missing");
    }

    @Test
    public void shouldReturnDefaultWhenCookieNotFound() {
        CookieParser parser = new CookieParser(createMockRequest());
        assertEquals("default", parser.getStringCookie("missing", "default"));
    }

    @Test
    public void shouldGetBooleanCookie() throws CookieNotFoundException {
        Cookie cookie = new Cookie("flag", "true");
        CookieParser parser = new CookieParser(createMockRequest(cookie));
        assertTrue(parser.getBooleanCookie("flag"));
    }

    @Test
    public void shouldReturnDefaultBooleanWhenNotFound() {
        CookieParser parser = new CookieParser(createMockRequest());
        assertTrue(parser.getBooleanCookie("missing", true));
    }

    @Test
    public void shouldGetByteCookie() throws CookieNotFoundException {
        Cookie cookie = new Cookie("byteVal", "42");
        CookieParser parser = new CookieParser(createMockRequest(cookie));
        assertEquals(42, parser.getByteCookie("byteVal"));
    }

    @Test
    public void shouldReturnDefaultByteWhenNotFound() {
        CookieParser parser = new CookieParser(createMockRequest());
        assertEquals(10, parser.getByteCookie("missing", (byte) 10));
    }

    @Test
    public void shouldGetCharCookie() throws CookieNotFoundException {
        Cookie cookie = new Cookie("charVal", "X");
        CookieParser parser = new CookieParser(createMockRequest(cookie));
        assertEquals('X', parser.getCharCookie("charVal"));
    }

    @Test(expected = CookieNotFoundException.class)
    public void shouldThrowForEmptyCharCookie() throws CookieNotFoundException {
        Cookie cookie = new Cookie("charVal", "");
        CookieParser parser = new CookieParser(createMockRequest(cookie));
        parser.getCharCookie("charVal");
    }

    @Test
    public void shouldReturnDefaultCharWhenNotFound() {
        CookieParser parser = new CookieParser(createMockRequest());
        assertEquals('Z', parser.getCharCookie("missing", 'Z'));
    }

    @Test
    public void shouldGetDoubleCookie() throws CookieNotFoundException {
        Cookie cookie = new Cookie("doubleVal", "3.14");
        CookieParser parser = new CookieParser(createMockRequest(cookie));
        assertEquals(3.14, parser.getDoubleCookie("doubleVal"), 0.001);
    }

    @Test
    public void shouldReturnDefaultDoubleWhenNotFound() {
        CookieParser parser = new CookieParser(createMockRequest());
        assertEquals(1.0, parser.getDoubleCookie("missing", 1.0), 0.001);
    }

    @Test
    public void shouldGetFloatCookie() throws CookieNotFoundException {
        Cookie cookie = new Cookie("floatVal", "2.5");
        CookieParser parser = new CookieParser(createMockRequest(cookie));
        assertEquals(2.5f, parser.getFloatCookie("floatVal"), 0.001f);
    }

    @Test
    public void shouldReturnDefaultFloatWhenNotFound() {
        CookieParser parser = new CookieParser(createMockRequest());
        assertEquals(1.0f, parser.getFloatCookie("missing", 1.0f), 0.001f);
    }

    @Test
    public void shouldGetIntCookie() throws CookieNotFoundException {
        Cookie cookie = new Cookie("intVal", "100");
        CookieParser parser = new CookieParser(createMockRequest(cookie));
        assertEquals(100, parser.getIntCookie("intVal"));
    }

    @Test
    public void shouldReturnDefaultIntWhenNotFound() {
        CookieParser parser = new CookieParser(createMockRequest());
        assertEquals(99, parser.getIntCookie("missing", 99));
    }

    @Test
    public void shouldGetLongCookie() throws CookieNotFoundException {
        Cookie cookie = new Cookie("longVal", "123456789");
        CookieParser parser = new CookieParser(createMockRequest(cookie));
        assertEquals(123456789L, parser.getLongCookie("longVal"));
    }

    @Test
    public void shouldReturnDefaultLongWhenNotFound() {
        CookieParser parser = new CookieParser(createMockRequest());
        assertEquals(42L, parser.getLongCookie("missing", 42L));
    }

    @Test
    public void shouldGetShortCookie() throws CookieNotFoundException {
        Cookie cookie = new Cookie("shortVal", "123");
        CookieParser parser = new CookieParser(createMockRequest(cookie));
        assertEquals(123, parser.getShortCookie("shortVal"));
    }

    @Test
    public void shouldReturnDefaultShortWhenNotFound() {
        CookieParser parser = new CookieParser(createMockRequest());
        assertEquals(10, parser.getShortCookie("missing", (short) 10));
    }

    @Test
    public void shouldHandleNullCookies() throws CookieNotFoundException {
        CookieParser parser = new CookieParser(new StubHttpServletRequest() {
            @Override
            public Cookie[] getCookies() { return null; }
        });
        assertEquals("default", parser.getStringCookie("any", "default"));
    }
}
