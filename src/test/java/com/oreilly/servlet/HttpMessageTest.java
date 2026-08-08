package com.oreilly.servlet;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import static org.junit.Assert.*;

public class HttpMessageTest {

    @Test
    public void shouldConstructWithURL() throws Exception {
        URL url = new URL("http://localhost:1/test");
        HttpMessage msg = new HttpMessage(url);
        assertNotNull(msg);
    }

    @Test
    public void shouldSetHeader() throws Exception {
        URL url = new URL("http://localhost:1/test");
        HttpMessage msg = new HttpMessage(url);
        msg.setHeader("Accept", "text/html");
        // Verify no exception thrown
    }

    @Test
    public void shouldSetCookie() throws Exception {
        URL url = new URL("http://localhost:1/test");
        HttpMessage msg = new HttpMessage(url);
        msg.setCookie("JSESSIONID", "abc123");
        // Verify no exception thrown
    }

    @Test
    public void shouldSetMultipleCookies() throws Exception {
        URL url = new URL("http://localhost:1/test");
        HttpMessage msg = new HttpMessage(url);
        msg.setCookie("JSESSIONID", "abc123");
        msg.setCookie("user", "test");
        // Verify no exception thrown
    }

    @Test
    public void shouldSetAuthorization() throws Exception {
        URL url = new URL("http://localhost:1/test");
        HttpMessage msg = new HttpMessage(url);
        msg.setAuthorization("guest", "password");
        // Verify no exception thrown
    }
}
