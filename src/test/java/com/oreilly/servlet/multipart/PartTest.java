package com.oreilly.servlet.multipart;

import org.junit.Test;
import static org.junit.Assert.*;

public class PartTest {

    @Test
    public void shouldIdentifyFilePart() {
        // FilePart should report isFile=true, isParam=false
        // We test through the Part abstract class behavior
        Part filePart = new Part("field") {
            @Override
            public boolean isFile() { return true; }
        };
        assertTrue(filePart.isFile());
        assertFalse(filePart.isParam());
        assertEquals("field", filePart.getName());
    }

    @Test
    public void shouldIdentifyParamPart() {
        Part paramPart = new Part("field") {
            @Override
            public boolean isParam() { return true; }
        };
        assertFalse(paramPart.isFile());
        assertTrue(paramPart.isParam());
        assertEquals("field", paramPart.getName());
    }

    @Test
    public void shouldDefaultToNotFileAndNotParam() {
        Part part = new Part("field") {};
        assertFalse(part.isFile());
        assertFalse(part.isParam());
    }
}
