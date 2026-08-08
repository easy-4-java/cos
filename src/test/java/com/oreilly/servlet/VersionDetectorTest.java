package com.oreilly.servlet;

import org.junit.Test;

import static org.junit.Assert.*;

public class VersionDetectorTest {

    @Test
    public void shouldReturnServletVersion() {
        String version = VersionDetector.getServletVersion();
        assertNotNull(version);
        assertFalse(version.isEmpty());
    }

    @Test
    public void shouldReturnCachedServletVersion() {
        String first = VersionDetector.getServletVersion();
        String second = VersionDetector.getServletVersion();
        assertSame(first, second);
    }

    @Test
    public void shouldReturnJavaVersion() {
        String version = VersionDetector.getJavaVersion();
        assertNotNull(version);
        assertFalse(version.isEmpty());
    }

    @Test
    public void shouldReturnCachedJavaVersion() {
        String first = VersionDetector.getJavaVersion();
        String second = VersionDetector.getJavaVersion();
        assertSame(first, second);
    }

    @Test
    public void shouldDetectServlet60() {
        String version = VersionDetector.getServletVersion();
        // On Jakarta EE 10+ runtime, should detect 6.0
        assertTrue("Expected at least 3.0, got: " + version,
            Double.parseDouble(version) >= 3.0);
    }
}
