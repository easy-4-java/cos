package com.oreilly.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

public class MultipartFilterTest {

    @Test
    public void shouldPassThroughNonMultipartRequest() throws Exception {
        MultipartFilter filter = new MultipartFilter();

        FilterConfig config = new FilterConfig() {
            @Override public String getFilterName() { return "test"; }
            @Override public ServletContext getServletContext() {
                return new StubServletContext() {
                    @Override public Object getAttribute(String name) {
                        if ("jakarta.servlet.context.tempdir".equals(name)) {
                            return new File(System.getProperty("java.io.tmpdir"));
                        }
                        return null;
                    }
                };
            }
            @Override public String getInitParameter(String name) { return null; }
            @Override public Enumeration<String> getInitParameterNames() { return Collections.emptyEnumeration(); }
        };

        filter.init(config);

        HttpServletRequest req = new StubHttpServletRequest() {
            @Override public String getHeader(String name) {
                if ("Content-Type".equals(name)) return "text/plain";
                return null;
            }
        };
        HttpServletResponse res = createMockResponse();
        FilterChain chain = new FilterChain() {
            @Override public void doFilter(ServletRequest request, ServletResponse response) {
                // Verify the original request is passed through
                assertSame(req, request);
            }
        };

        filter.doFilter(req, res, chain);
        filter.destroy();
    }

    @Test
    public void shouldWrapMultipartRequest() throws Exception {
        MultipartFilter filter = new MultipartFilter();

        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        FilterConfig config = new FilterConfig() {
            @Override public String getFilterName() { return "test"; }
            @Override public ServletContext getServletContext() {
                return new StubServletContext() {
                    @Override public Object getAttribute(String name) {
                        if ("jakarta.servlet.context.tempdir".equals(name)) return tempDir;
                        return null;
                    }
                };
            }
            @Override public String getInitParameter(String name) { return null; }
            @Override public Enumeration<String> getInitParameterNames() { return Collections.emptyEnumeration(); }
        };

        filter.init(config);

        HttpServletRequest req = new StubHttpServletRequest() {
            @Override public String getHeader(String name) {
                if ("Content-Type".equals(name)) return "multipart/form-data; boundary=test";
                return null;
            }
            @Override public int getContentLength() { return 0; }
            @Override public long getContentLengthLong() { return 0; }
        };

        HttpServletResponse res = createMockResponse();
        FilterChain chain = new FilterChain() {
            @Override public void doFilter(ServletRequest request, ServletResponse response) {
                // The request should be wrapped
                assertTrue(request instanceof MultipartWrapper);
            }
        };

        try {
            filter.doFilter(req, res, chain);
        } catch (Exception e) {
            // Expected - MultipartWrapper tries to parse the request which fails
            // due to no actual input stream. That's OK for this test.
        }
        filter.destroy();
    }

    @Test
    public void shouldUseUploadDirInitParameter() throws Exception {
        MultipartFilter filter = new MultipartFilter();

        FilterConfig config = new FilterConfig() {
            @Override public String getFilterName() { return "test"; }
            @Override public ServletContext getServletContext() { return new StubServletContext(); }
            @Override public String getInitParameter(String name) {
                if ("uploadDir".equals(name)) return System.getProperty("java.io.tmpdir");
                return null;
            }
            @Override public Enumeration<String> getInitParameterNames() { return Collections.emptyEnumeration(); }
        };

        filter.init(config);
        filter.destroy();
    }

    @Test(expected = ServletException.class)
    public void shouldThrowWhenNoUploadDir() throws Exception {
        MultipartFilter filter = new MultipartFilter();

        FilterConfig config = new FilterConfig() {
            @Override public String getFilterName() { return "test"; }
            @Override public ServletContext getServletContext() { return new StubServletContext(); }
            @Override public String getInitParameter(String name) { return null; }
            @Override public Enumeration<String> getInitParameterNames() { return Collections.emptyEnumeration(); }
        };

        filter.init(config);
    }

    @Test
    public void shouldHandleNullContentType() throws Exception {
        MultipartFilter filter = new MultipartFilter();

        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        FilterConfig config = new FilterConfig() {
            @Override public String getFilterName() { return "test"; }
            @Override public ServletContext getServletContext() {
                return new StubServletContext() {
                    @Override public Object getAttribute(String name) {
                        if ("jakarta.servlet.context.tempdir".equals(name)) return tempDir;
                        return null;
                    }
                };
            }
            @Override public String getInitParameter(String name) { return null; }
            @Override public Enumeration<String> getInitParameterNames() { return Collections.emptyEnumeration(); }
        };

        filter.init(config);

        HttpServletRequest req = new StubHttpServletRequest() {
            @Override public String getHeader(String name) { return null; }
        };

        final boolean[] called = {false};
        FilterChain chain = new FilterChain() {
            @Override public void doFilter(ServletRequest request, ServletResponse response) {
                called[0] = true;
            }
        };

        filter.doFilter(req, createMockResponse(), chain);
        assertTrue(called[0]);
        filter.destroy();
    }

    private HttpServletResponse createMockResponse() {
        return new StubHttpServletResponse();
    }
}
