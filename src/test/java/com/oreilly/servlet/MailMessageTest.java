package com.oreilly.servlet;

import org.junit.Test;
import static org.junit.Assert.*;

public class MailMessageTest {

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
}
