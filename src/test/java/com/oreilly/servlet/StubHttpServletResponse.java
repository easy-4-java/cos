package com.oreilly.servlet;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

/**
 * Minimal stub of HttpServletResponse for testing purposes.
 */
public class StubHttpServletResponse implements HttpServletResponse {
    @Override public void addCookie(Cookie cookie) {}
    @Override public boolean containsHeader(String name) { return false; }
    @Override public String encodeURL(String url) { return url; }
    @Override public String encodeRedirectURL(String url) { return url; }
    @Override public void sendError(int sc, String msg) throws IOException {}
    @Override public void sendError(int sc) throws IOException {}
    @Override public void sendRedirect(String location) throws IOException {}
    @Override public void setDateHeader(String name, long date) {}
    @Override public void addDateHeader(String name, long date) {}
    @Override public void setHeader(String name, String value) {}
    @Override public void addHeader(String name, String value) {}
    @Override public void setIntHeader(String name, int value) {}
    @Override public void addIntHeader(String name, int value) {}
    @Override public void setStatus(int sc) {}
    @Override public int getStatus() { return 200; }
    @Override public String getHeader(String name) { return null; }
    @Override public Collection<String> getHeaders(String name) { return Collections.emptyList(); }
    @Override public Collection<String> getHeaderNames() { return Collections.emptyList(); }
    @Override public String getCharacterEncoding() { return "ISO-8859-1"; }
    @Override public String getContentType() { return null; }
    @Override public ServletOutputStream getOutputStream() throws IOException {
        return new ServletOutputStream() {
            @Override public void write(int b) {}
            @Override public boolean isReady() { return true; }
            @Override public void setWriteListener(WriteListener l) {}
        };
    }
    @Override public PrintWriter getWriter() throws IOException { return new PrintWriter(System.out); }
    @Override public void setCharacterEncoding(String charset) {}
    @Override public void setContentLength(int len) {}
    @Override public void setContentLengthLong(long len) {}
    @Override public void setContentType(String type) {}
    @Override public void setBufferSize(int size) {}
    @Override public int getBufferSize() { return 4096; }
    @Override public void flushBuffer() {}
    @Override public boolean isCommitted() { return false; }
    @Override public void reset() {}
    @Override public void resetBuffer() {}
    @Override public void setLocale(Locale loc) {}
    @Override public Locale getLocale() { return Locale.getDefault(); }
    public void sendRedirect(String location, int sc, boolean clearBuffer) throws IOException {}
}
