package com.oreilly.servlet;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import static org.junit.Assert.*;

public class MultipartResponseTest {

    private HttpServletResponse createMockResponse(ByteArrayOutputStream out) {
        return new StubHttpServletResponse() {
            @Override public ServletOutputStream getOutputStream() {
                return new ServletOutputStream() {
                    @Override public void write(int b) { out.write(b); }
                    @Override public boolean isReady() { return true; }
                    @Override public void setWriteListener(WriteListener l) {}
                };
            }
        };
    }

    @Test
    public void shouldCreateMultipartResponse() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HttpServletResponse mockRes = createMockResponse(out);
        MultipartResponse multi = new MultipartResponse(mockRes);
        assertNotNull(multi);
    }

    @Test
    public void shouldStartAndEndResponse() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HttpServletResponse mockRes = createMockResponse(out);
        MultipartResponse multi = new MultipartResponse(mockRes);

        multi.startResponse("text/plain");
        multi.endResponse();
        String content = out.toString();
        assertTrue(content.contains("--End"));
    }

    @Test
    public void shouldAutoEndPreviousResponse() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HttpServletResponse mockRes = createMockResponse(out);
        MultipartResponse multi = new MultipartResponse(mockRes);

        multi.startResponse("text/plain");
        // Starting a new response without ending the previous one
        multi.startResponse("text/html");
        multi.endResponse();
        String content = out.toString();
        assertTrue(content.contains("Content-type: text/plain"));
        assertTrue(content.contains("Content-type: text/html"));
    }

    @Test
    public void shouldFinish() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HttpServletResponse mockRes = createMockResponse(out);
        MultipartResponse multi = new MultipartResponse(mockRes);

        multi.startResponse("text/plain");
        multi.endResponse();
        multi.finish();
        String content = out.toString();
        assertTrue(content.contains("--End--"));
    }
}
