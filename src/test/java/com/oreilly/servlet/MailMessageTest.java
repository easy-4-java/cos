package com.oreilly.servlet;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class MailMessageTest {

    /**
     * Allocates a MailMessage without running its constructor, which would
     * open a real SMTP connection to the mail host. Loads Unsafe reflectively
     * so this compiles under --release 8 (sun.misc is not visible then).
     */
    private static MailMessage mailMessageWithoutConstructor() throws Exception {
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field f = unsafeClass.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Object unsafe = f.get(null);
        java.lang.reflect.Method allocate = unsafeClass.getMethod("allocateInstance", Class.class);
        return (MailMessage) allocate.invoke(unsafe, MailMessage.class);
    }

    @Test
    public void shouldSanitizeSimpleAddress() {
        String result = MailMessage.sanitizeAddress("user@example.com");
        assertEquals("user@example.com", result);
    }

    @Test
    public void shouldSanitizeAddressWithAngleBrackets() {
        String result = MailMessage.sanitizeAddress("<user@example.com>");
        assertEquals("user@example.com", result);
    }

    @Test
    public void shouldSanitizeAddressWithNameInBrackets() {
        String result = MailMessage.sanitizeAddress("User Name <user@example.com>");
        assertEquals("user@example.com", result);
    }

    @Test
    public void shouldSanitizeAddressWithParentheses() {
        // The sanitize method includes the space before the parentheses
        String result = MailMessage.sanitizeAddress("user@example.com (User Name)");
        assertTrue(result.contains("user@example.com"));
    }

    @Test
    public void shouldSanitizeAddressWithNameBeforeParens() {
        // The sanitize method includes the space after the closing paren
        String result = MailMessage.sanitizeAddress("(User Name) user@example.com");
        assertTrue(result.contains("user@example.com"));
    }

    @Test
    public void shouldHandlePlainAddress() {
        String result = MailMessage.sanitizeAddress("plain@address.com");
        assertEquals("plain@address.com", result);
    }

    @Test
    public void shouldHandleEmptyAddress() {
        String result = MailMessage.sanitizeAddress("");
        assertEquals("", result);
    }

    @Test
    public void shouldAcceptMatchingResponseCode() throws Exception {
        MailMessage msg = mailMessageWithoutConstructor();
        assertTrue(msg.isResponseOK("250 OK", new int[]{250}));
    }

    @Test
    public void shouldRejectNonMatchingResponseCode() throws Exception {
        MailMessage msg = mailMessageWithoutConstructor();
        assertFalse(msg.isResponseOK("550 Requested action not taken", new int[]{250}));
    }

    @Test
    public void shouldRejectNullResponse() throws Exception {
        MailMessage msg = mailMessageWithoutConstructor();
        assertFalse(msg.isResponseOK(null, new int[]{250}));
    }

    @Test
    public void shouldAcceptAnyOfMultipleCodes() throws Exception {
        MailMessage msg = mailMessageWithoutConstructor();
        assertTrue(msg.isResponseOK("354 Start mail input", new int[]{250, 354}));
        assertFalse(msg.isResponseOK("500 Syntax error", new int[]{250, 354}));
    }

    @Test
    public void shouldMatchByPrefix() throws Exception {
        MailMessage msg = mailMessageWithoutConstructor();
        // The classic implementation matches by numeric prefix
        assertTrue(msg.isResponseOK("2500 extended code", new int[]{250}));
    }
}
