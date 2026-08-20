package com.oreilly.servlet.multipart;

import com.oreilly.servlet.StubHttpServletRequest;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class MultipartParserTest {

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

    private static HttpServletRequest multipartRequest(final byte[] body) {
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

    @Test
    public void shouldReadParamPart() throws IOException {
        MultipartParser parser = new MultipartParser(multipartRequest(body().getBytes(StandardCharsets.ISO_8859_1)), 1024 * 1024);

        Part part = parser.readNextPart();
        assertNotNull(part);
        assertTrue(part.isParam());
        assertEquals("field1", part.getName());
        assertEquals("value1", ((ParamPart) part).getStringValue());
    }

    @Test
    public void shouldReadFilePart() throws IOException {
        MultipartParser parser = new MultipartParser(multipartRequest(body().getBytes(StandardCharsets.ISO_8859_1)), 1024 * 1024);

        // Skip the param part
        parser.readNextPart();

        Part part = parser.readNextPart();
        assertNotNull(part);
        assertTrue(part.isFile());
        FilePart filePart = (FilePart) part;
        assertEquals("file1", filePart.getName());
        assertEquals("test.txt", filePart.getFileName());
        assertEquals("text/plain", filePart.getContentType());
    }

    @Test
    public void shouldReturnNullAtEnd() throws IOException {
        MultipartParser parser = new MultipartParser(multipartRequest(body().getBytes(StandardCharsets.ISO_8859_1)), 1024 * 1024);

        assertNotNull(parser.readNextPart());
        assertNotNull(parser.readNextPart());
        assertNull(parser.readNextPart());
    }

    @Test
    public void shouldReadFileContentViaInputStream() throws IOException {
        MultipartParser parser = new MultipartParser(multipartRequest(body().getBytes(StandardCharsets.ISO_8859_1)), 1024 * 1024);

        parser.readNextPart();
        FilePart filePart = (FilePart) parser.readNextPart();

        StringBuilder content = new StringBuilder();
        int b;
        while ((b = filePart.getInputStream().read()) != -1) {
            content.append((char) b);
        }
        assertEquals("file content", content.toString());
    }

    @Test
    public void shouldWriteFilePartToFile() throws IOException {
        MultipartParser parser = new MultipartParser(multipartRequest(body().getBytes(StandardCharsets.ISO_8859_1)), 1024 * 1024);

        parser.readNextPart();
        FilePart filePart = (FilePart) parser.readNextPart();

        java.io.File file = java.io.File.createTempFile("cos-upload-", ".txt");
        try {
            filePart.writeTo(file);
            assertEquals("file content",
                new String(java.nio.file.Files.readAllBytes(file.toPath()), StandardCharsets.ISO_8859_1));
        } finally {
            file.delete();
        }
    }

    @Test(expected = ExceededSizeException.class)
    public void shouldThrowWhenPostExceedsMaxSize() throws IOException {
        byte[] body = body().getBytes(StandardCharsets.ISO_8859_1);
        // Max size smaller than the declared content length
        new MultipartParser(multipartRequest(body), 10);
    }

    @Test
    public void shouldUseGivenEncoding() throws IOException {
        MultipartParser parser = new MultipartParser(multipartRequest(body().getBytes(StandardCharsets.ISO_8859_1)),
            1024 * 1024, true, true, "ISO-8859-1");

        Part part = parser.readNextPart();
        assertEquals("value1", ((ParamPart) part).getStringValue());
    }
}
