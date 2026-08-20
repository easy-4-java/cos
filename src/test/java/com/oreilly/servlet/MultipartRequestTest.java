package com.oreilly.servlet;

import com.oreilly.servlet.multipart.ExceededSizeException;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class MultipartRequestTest {

    private static final String BOUNDARY = "testboundary";

    private static String body() {
        StringBuilder sb = new StringBuilder();
        sb.append("--").append(BOUNDARY).append("\r\n");
        sb.append("Content-Disposition: form-data; name=\"field1\"").append("\r\n");
        sb.append("\r\n");
        sb.append("value1").append("\r\n");
        sb.append("--").append(BOUNDARY).append("\r\n");
        sb.append("Content-Disposition: form-data; name=\"file1\"; filename=\"test.txt\"").append("\r\n");
        sb.append("Content-Type: text/plain").append("\r\n");
        sb.append("\r\n");
        sb.append("file content").append("\r\n");
        sb.append("--").append(BOUNDARY).append("--").append("\r\n");
        return sb.toString();
    }

    private static HttpServletRequest multipartRequest(final byte[] body, final String queryString) {
        return new StubHttpServletRequest() {
            @Override public String getContentType() {
                return "multipart/form-data; boundary=" + BOUNDARY;
            }
            @Override public String getHeader(String name) {
                if ("Content-Type".equals(name)) {
                    return "multipart/form-data; boundary=" + BOUNDARY;
                }
                return null;
            }
            @Override public String getQueryString() { return queryString; }
            @Override public long getContentLengthLong() { return body.length; }
            @Override public ServletInputStream getInputStream() {
                return new ServletInputStream() {
                    private final ByteArrayInputStream in = new ByteArrayInputStream(body);
                    @Override public int read() { return in.read(); }
                    @Override public boolean isFinished() { return in.available() == 0; }
                    @Override public boolean isReady() { return true; }
                    @Override public void setReadListener(ReadListener l) {}
                };
            }
        };
    }

    private static File createSaveDir() throws IOException {
        File dir = Files.createTempDirectory("cos-multipart-test").toFile();
        dir.deleteOnExit();
        return dir;
    }

    @Test
    public void shouldParseParameters() throws IOException {
        File saveDir = createSaveDir();
        try {
            MultipartRequest req = new MultipartRequest(
                multipartRequest(body().getBytes(StandardCharsets.ISO_8859_1), null),
                saveDir.getAbsolutePath());

            assertEquals("value1", req.getParameter("field1"));
            assertNull(req.getParameter("missing"));
        } finally {
            saveDir.delete();
        }
    }

    @Test
    public void shouldParseUploadedFile() throws IOException {
        File saveDir = createSaveDir();
        try {
            MultipartRequest req = new MultipartRequest(
                multipartRequest(body().getBytes(StandardCharsets.ISO_8859_1), null),
                saveDir.getAbsolutePath());

            assertNotNull(req.getFile("file1"));
            assertEquals("test.txt", req.getFilesystemName("file1"));
            assertEquals("test.txt", req.getOriginalFileName("file1"));
            assertEquals("text/plain", req.getContentType("file1"));
            assertEquals("file content",
                new String(Files.readAllBytes(req.getFile("file1").toPath()), StandardCharsets.ISO_8859_1));
        } finally {
            saveDir.delete();
        }
    }

    @Test
    public void shouldMergeQueryStringParameters() throws IOException {
        File saveDir = createSaveDir();
        try {
            MultipartRequest req = new MultipartRequest(
                multipartRequest(body().getBytes(StandardCharsets.ISO_8859_1), "qs=fromquery"),
                saveDir.getAbsolutePath());

            assertEquals("fromquery", req.getParameter("qs"));
            // Multipart body parameters still work alongside query parameters
            assertEquals("value1", req.getParameter("field1"));
        } finally {
            saveDir.delete();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectNullSaveDirectory() throws IOException {
        new MultipartRequest(
            multipartRequest(body().getBytes(StandardCharsets.ISO_8859_1), null), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectNonPositiveMaxPostSize() throws IOException {
        File saveDir = createSaveDir();
        try {
            new MultipartRequest(
                multipartRequest(body().getBytes(StandardCharsets.ISO_8859_1), null),
                saveDir.getAbsolutePath(), 0);
        } finally {
            saveDir.delete();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectNonDirectorySavePath() throws IOException {
        File file = File.createTempFile("cos-not-a-dir", ".tmp");
        try {
            new MultipartRequest(
                multipartRequest(body().getBytes(StandardCharsets.ISO_8859_1), null),
                file.getAbsolutePath());
        } finally {
            file.delete();
        }
    }

    @Test(expected = ExceededSizeException.class)
    public void shouldThrowWhenPostExceedsMaxSize() throws IOException {
        File saveDir = createSaveDir();
        try {
            new MultipartRequest(
                multipartRequest(body().getBytes(StandardCharsets.ISO_8859_1), null),
                saveDir.getAbsolutePath(), 10);
        } finally {
            saveDir.delete();
        }
    }

    @Test
    public void shouldSupportFileRenamePolicy() throws IOException {
        File saveDir = createSaveDir();
        try {
            com.oreilly.servlet.multipart.FileRenamePolicy policy =
                new com.oreilly.servlet.multipart.DefaultFileRenamePolicy();
            MultipartRequest req = new MultipartRequest(
                multipartRequest(body().getBytes(StandardCharsets.ISO_8859_1), null),
                saveDir.getAbsolutePath(), 1024 * 1024, "ISO-8859-1", policy);

            assertEquals("test.txt", req.getFilesystemName("file1"));
            assertTrue(req.getFile("file1").exists());
        } finally {
            saveDir.delete();
        }
    }
}
