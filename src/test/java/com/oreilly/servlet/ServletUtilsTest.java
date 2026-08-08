package com.oreilly.servlet;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.net.URL;

import static org.junit.Assert.*;

public class ServletUtilsTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void shouldReturnFile() throws Exception {
        File tempFile = tempFolder.newFile("test.txt");
        try (FileWriter fw = new FileWriter(tempFile)) {
            fw.write("Hello World");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ServletUtils.returnFile(tempFile.getAbsolutePath(), out);
        assertEquals("Hello World", out.toString("UTF-8"));
    }

    @Test(expected = FileNotFoundException.class)
    public void shouldThrowForMissingFile() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ServletUtils.returnFile("/nonexistent/file.txt", out);
    }

    @Test
    public void shouldReturnURL() throws Exception {
        File tempFile = tempFolder.newFile("urltest.txt");
        try (FileWriter fw = new FileWriter(tempFile)) {
            fw.write("URL Content");
        }

        URL url = tempFile.toURI().toURL();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ServletUtils.returnURL(url, out);
        assertEquals("URL Content", out.toString("UTF-8"));
    }

    @Test
    public void shouldReturnURLToWriter() throws Exception {
        File tempFile = tempFolder.newFile("urltest2.txt");
        try (FileWriter fw = new FileWriter(tempFile)) {
            fw.write("Writer Content");
        }

        URL url = tempFile.toURI().toURL();
        StringWriter out = new StringWriter();
        ServletUtils.returnURL(url, out);
        assertEquals("Writer Content", out.toString());
    }

    @Test
    public void shouldGetStackTraceAsString() {
        Exception ex = new RuntimeException("test error");
        String stackTrace = ServletUtils.getStackTraceAsString(ex);
        assertNotNull(stackTrace);
        assertTrue(stackTrace.contains("test error"));
        assertTrue(stackTrace.contains("RuntimeException"));
    }

    @Test
    public void shouldSplitString() {
        String[] result = ServletUtils.split("a,b,c", ",");
        assertEquals(3, result.length);
        assertEquals("a", result[0]);
        assertEquals("b", result[1]);
        assertEquals("c", result[2]);
    }

    @Test
    public void shouldSplitStringWithMultipleDelimiters() {
        String[] result = ServletUtils.split("a;b;c", ";");
        assertEquals(3, result.length);
    }

    @Test
    public void shouldSplitEmptyDelimitedString() {
        String[] result = ServletUtils.split("abc", ",");
        assertEquals(1, result.length);
        assertEquals("abc", result[0]);
    }
}
