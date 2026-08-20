package com.oreilly.servlet;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class CacheHttpServletTest {

    /** HttpServletResponse stub that records status and output body. */
    static class RecordingResponse extends StubHttpServletResponse {
        final ByteArrayOutputStream body = new ByteArrayOutputStream();
        int status = 200;

        @Override public ServletOutputStream getOutputStream() throws IOException {
            return new ServletOutputStream() {
                @Override public void write(int b) { body.write(b); }
                @Override public boolean isReady() { return true; }
                @Override public void setWriteListener(WriteListener l) {}
            };
        }

        @Override public void setStatus(int sc) { this.status = sc; }
    }

    @Test
    public void shouldCacheGetResponseAndServeFromCache() throws Exception {
        final int[] doGetCount = {0};
        CacheHttpServlet servlet = new CacheHttpServlet() {
            @Override public long getLastModified(HttpServletRequest req) { return 1000L; }
            @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
                doGetCount[0]++;
                res.getWriter().println("cached-body");
            }
        };
        HttpServletRequest req = new StubHttpServletRequest();

        RecordingResponse res1 = new RecordingResponse();
        servlet.service(req, res1);
        assertEquals(1, doGetCount[0]);
        assertEquals("cached-body" + System.lineSeparator(),
            new String(res1.body.toByteArray(), StandardCharsets.ISO_8859_1));

        // Second request hits the cache: doGet must not run again
        RecordingResponse res2 = new RecordingResponse();
        servlet.service(req, res2);
        assertEquals(1, doGetCount[0]);
        assertEquals("cached-body" + System.lineSeparator(),
            new String(res2.body.toByteArray(), StandardCharsets.ISO_8859_1));
    }

    @Test
    public void shouldInvalidateCacheWhenQueryStringChanges() throws Exception {
        final int[] doGetCount = {0};
        CacheHttpServlet servlet = new CacheHttpServlet() {
            @Override public long getLastModified(HttpServletRequest req) { return 1000L; }
            @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
                doGetCount[0]++;
                res.getWriter().println("body");
            }
        };

        servlet.service(new StubHttpServletRequest(), new RecordingResponse());
        assertEquals(1, doGetCount[0]);

        HttpServletRequest reqWithQuery = new StubHttpServletRequest() {
            @Override public String getQueryString() { return "a=1"; }
        };
        servlet.service(reqWithQuery, new RecordingResponse());
        assertEquals(2, doGetCount[0]);
    }

    @Test
    public void shouldSendNotModifiedWhenClientCacheIsCurrent() throws Exception {
        final int[] doGetCount = {0};
        CacheHttpServlet servlet = new CacheHttpServlet() {
            @Override public long getLastModified(HttpServletRequest req) { return 1000L; }
            @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
                doGetCount[0]++;
            }
        };
        HttpServletRequest req = new StubHttpServletRequest() {
            @Override public long getDateHeader(String name) { return 1000000L; }
        };

        RecordingResponse res = new RecordingResponse();
        servlet.service(req, res);

        assertEquals(0, doGetCount[0]);
        assertEquals(HttpServletResponse.SC_NOT_MODIFIED, res.status);
    }

    @Test
    public void shouldNotCacheWhenLastModifiedIsMinusOne() throws Exception {
        final int[] doGetCount = {0};
        CacheHttpServlet servlet = new CacheHttpServlet() {
            @Override public long getLastModified(HttpServletRequest req) { return -1L; }
            @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
                doGetCount[0]++;
            }
        };
        HttpServletRequest req = new StubHttpServletRequest();

        servlet.service(req, new RecordingResponse());
        servlet.service(req, new RecordingResponse());
        assertEquals(2, doGetCount[0]);
    }

    @Test
    public void shouldNotCachePostRequests() throws Exception {
        final int[] doPostCount = {0};
        CacheHttpServlet servlet = new CacheHttpServlet() {
            @Override public long getLastModified(HttpServletRequest req) { return 1000L; }
            @Override protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
                doPostCount[0]++;
            }
        };
        HttpServletRequest req = new StubHttpServletRequest() {
            @Override public String getMethod() { return "POST"; }
        };

        servlet.service(req, new RecordingResponse());
        servlet.service(req, new RecordingResponse());
        assertEquals(2, doPostCount[0]);
    }

    @Test
    public void shouldNotCacheErrorPages() throws Exception {
        final int[] doGetCount = {0};
        CacheHttpServlet servlet = new CacheHttpServlet() {
            @Override public long getLastModified(HttpServletRequest req) { return 1000L; }
            @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
                doGetCount[0]++;
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        };
        HttpServletRequest req = new StubHttpServletRequest();

        servlet.service(req, new RecordingResponse());
        servlet.service(req, new RecordingResponse());
        // Error responses are not cached (isValid() is false), so doGet runs each time
        assertEquals(2, doGetCount[0]);
    }
}
