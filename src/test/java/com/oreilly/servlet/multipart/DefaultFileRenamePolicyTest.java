package com.oreilly.servlet.multipart;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class DefaultFileRenamePolicyTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void shouldReturnSameFileWhenNoConflict() throws IOException {
        DefaultFileRenamePolicy policy = new DefaultFileRenamePolicy();
        File f = new File(tempFolder.getRoot(), "unique.txt");
        File result = policy.rename(f);
        assertEquals(f.getName(), result.getName());
        assertTrue(result.exists());
    }

    @Test
    public void shouldRenameOnConflict() throws IOException {
        DefaultFileRenamePolicy policy = new DefaultFileRenamePolicy();
        File existing = tempFolder.newFile("conflict.txt");
        File f = new File(tempFolder.getRoot(), "conflict.txt");
        File result = policy.rename(f);
        assertNotEquals("conflict.txt", result.getName());
        assertTrue(result.getName().startsWith("conflict"));
        assertTrue(result.exists());
    }

    @Test
    public void shouldHandleMultipleConflicts() throws IOException {
        DefaultFileRenamePolicy policy = new DefaultFileRenamePolicy();
        tempFolder.newFile("multi.txt");
        tempFolder.newFile("multi1.txt");
        tempFolder.newFile("multi2.txt");
        File f = new File(tempFolder.getRoot(), "multi.txt");
        File result = policy.rename(f);
        assertTrue(result.exists());
        assertFalse(result.getName().equals("multi.txt"));
    }

    @Test
    public void shouldHandleFileWithoutExtension() throws IOException {
        DefaultFileRenamePolicy policy = new DefaultFileRenamePolicy();
        File f = new File(tempFolder.getRoot(), "noext");
        File result = policy.rename(f);
        assertEquals("noext", result.getName());
    }

    @Test
    public void shouldHandleFileWithoutExtensionOnConflict() throws IOException {
        DefaultFileRenamePolicy policy = new DefaultFileRenamePolicy();
        tempFolder.newFile("noext2");
        File f = new File(tempFolder.getRoot(), "noext2");
        File result = policy.rename(f);
        assertTrue(result.exists());
        assertNotEquals("noext2", result.getName());
    }
}
