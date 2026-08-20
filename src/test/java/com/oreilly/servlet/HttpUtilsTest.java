package com.oreilly.servlet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class HttpUtilsTest {

    @Test
    public void shouldParseSimpleQueryString() {
        Hashtable<String, String[]> result = HttpUtils.parseQueryString("name=value");
        assertNotNull(result);
        assertArrayEquals(new String[]{"value"}, result.get("name"));
    }

    @Test
    public void shouldParseMultipleParams() {
        Hashtable<String, String[]> result = HttpUtils.parseQueryString("a=1&b=2&c=3");
        assertEquals(3, result.size());
        assertArrayEquals(new String[]{"1"}, result.get("a"));
        assertArrayEquals(new String[]{"2"}, result.get("b"));
        assertArrayEquals(new String[]{"3"}, result.get("c"));
    }

    @Test
    public void shouldParseDuplicateKeys() {
        Hashtable<String, String[]> result = HttpUtils.parseQueryString("color=red&color=blue");
        String[] colors = result.get("color");
        assertNotNull(colors);
        assertEquals(2, colors.length);
        assertEquals("red", colors[0]);
        assertEquals("blue", colors[1]);
    }

    @Test
    public void shouldDecodePlusAsSpace() {
        Hashtable<String, String[]> result = HttpUtils.parseQueryString("name=hello+world");
        assertEquals("hello world", result.get("name")[0]);
    }

    @Test
    public void shouldDecodePercentEncoding() {
        Hashtable<String, String[]> result = HttpUtils.parseQueryString("name=%41%42%43");
        assertEquals("ABC", result.get("name")[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowForNullQueryString() {
        HttpUtils.parseQueryString(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowForMalformedQueryString() {
        HttpUtils.parseQueryString("novalue");
    }

    @Test
    public void shouldReconstructRequestURL() {
        HttpServletRequest req = new StubHttpServletRequest() {
            @Override public String getScheme() { return "http"; }
            @Override public String getServerName() { return "example.com"; }
            @Override public int getServerPort() { return 8080; }
            @Override public String getRequestURI() { return "/servlet/TestServlet"; }
        };
        StringBuffer url = HttpUtils.getRequestURL(req);
        assertEquals("http://example.com:8080/servlet/TestServlet", url.toString());
    }

    @Test
    public void shouldOmitPort80ForHttp() {
        HttpServletRequest req = new StubHttpServletRequest() {
            @Override public String getScheme() { return "http"; }
            @Override public String getServerName() { return "example.com"; }
            @Override public int getServerPort() { return 80; }
            @Override public String getRequestURI() { return "/path"; }
        };
        StringBuffer url = HttpUtils.getRequestURL(req);
        assertEquals("http://example.com/path", url.toString());
    }

    @Test
    public void shouldOmitPort443ForHttps() {
        HttpServletRequest req = new StubHttpServletRequest() {
            @Override public String getScheme() { return "https"; }
            @Override public String getServerName() { return "example.com"; }
            @Override public int getServerPort() { return 443; }
            @Override public String getRequestURI() { return "/path"; }
        };
        StringBuffer url = HttpUtils.getRequestURL(req);
        assertEquals("https://example.com/path", url.toString());
    }

    @Test
    public void shouldIncludeNonStandardPort() {
        HttpServletRequest req = new StubHttpServletRequest() {
            @Override public String getScheme() { return "https"; }
            @Override public String getServerName() { return "example.com"; }
            @Override public int getServerPort() { return 8443; }
            @Override public String getRequestURI() { return "/secure"; }
        };
        StringBuffer url = HttpUtils.getRequestURL(req);
        assertEquals("https://example.com:8443/secure", url.toString());
    }

    @Test
    public void shouldHandlePercentEncodedValues() {
        Hashtable<String, String[]> result = HttpUtils.parseQueryString("q=hello%20world");
        assertEquals("hello world", result.get("q")[0]);
    }
}
