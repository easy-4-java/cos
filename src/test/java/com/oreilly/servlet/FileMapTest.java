package com.oreilly.servlet;

import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.junit.Assert.*;

public class FileMapTest {

    @Test
    public void shouldPutAndGet() {
        FileMap map = new FileMap();
        UploadedFile uf = new UploadedFile("/tmp", "test.txt", "test.txt", "text/plain");
        map.put("file", uf);
        assertNotNull(map.get("file"));
    }

    @Test
    public void shouldHandleDuplicateKeys() {
        FileMap map = new FileMap();
        UploadedFile uf1 = new UploadedFile("/tmp", "a.txt", "a.txt", "text/plain");
        UploadedFile uf2 = new UploadedFile("/tmp", "b.txt", "b.txt", "text/plain");
        map.put("file", uf1);
        map.put("file", uf2);
        // First file should be accessible under "file"
        assertNotNull(map.get("file"));
        // Second file should be accessible under "file_1" (index starts at 0)
        assertNotNull(map.get("file_0"));
    }

    @Test
    public void shouldReturnNullForMissingKey() {
        FileMap map = new FileMap();
        assertNull(map.get("missing"));
    }

    @Test
    public void shouldEnumerateKeys() {
        FileMap map = new FileMap();
        UploadedFile uf = new UploadedFile("/tmp", "test.txt", "test.txt", "text/plain");
        map.put("file1", uf);
        map.put("file2", uf);
        Enumeration<String> keys = map.keys();
        assertTrue(keys.hasMoreElements());
        assertNotNull(keys.nextElement());
    }

    @Test
    public void shouldReturnFileNameSet() {
        FileMap map = new FileMap();
        UploadedFile uf = new UploadedFile("/tmp", "test.txt", "test.txt", "text/plain");
        map.put("file1", uf);
        Set<String> names = map.getFileNameSet();
        assertNotNull(names);
        assertTrue(names.contains("file1"));
    }

    @Test
    public void shouldDeleteAllFiles() {
        FileMap map = new FileMap();
        // Put a file with null dir (no actual file to delete)
        UploadedFile uf = new UploadedFile(null, null, null, null);
        map.put("file1", uf);
        // Should not throw
        map.deleteAllFiles();
    }
}
