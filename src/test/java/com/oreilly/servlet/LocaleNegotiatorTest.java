package com.oreilly.servlet;

import org.junit.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.*;

public class LocaleNegotiatorTest {

    @Test
    public void shouldUseDefaultLocaleWhenNoLanguages() {
        LocaleNegotiator negotiator = new LocaleNegotiator("test", null, null);
        assertNotNull(negotiator.getLocale());
        assertNotNull(negotiator.getCharset());
    }

    @Test
    public void shouldNegotiateEnglishLocale() {
        LocaleNegotiator negotiator = new LocaleNegotiator("test", "en", null);
        assertEquals("en", negotiator.getLocale().getLanguage());
        assertEquals("ISO-8859-1", negotiator.getCharset());
    }

    @Test
    public void shouldFallBackToDefaultsWhenNoMatch() {
        LocaleNegotiator negotiator = new LocaleNegotiator("test", "xx-YY", null);
        assertNotNull(negotiator.getLocale());
        assertNotNull(negotiator.getCharset());
    }

    @Test
    public void shouldHandleMultipleLanguages() {
        LocaleNegotiator negotiator = new LocaleNegotiator("test", "en,fr,de", null);
        assertNotNull(negotiator.getLocale());
    }

    @Test
    public void shouldHandleLanguageWithQValue() {
        LocaleNegotiator negotiator = new LocaleNegotiator("test", "en;q=0.9,fr;q=0.8", null);
        assertNotNull(negotiator.getLocale());
    }

    @Test
    public void shouldReturnNullBundleWhenNoBundleExists() {
        LocaleNegotiator negotiator = new LocaleNegotiator(
            "com.nonexistent.Bundle", "en", null);
        assertNull(negotiator.getBundle());
    }

    @Test
    public void shouldReturnCharset() {
        LocaleNegotiator negotiator = new LocaleNegotiator("test", "en", null);
        assertNotNull(negotiator.getCharset());
    }

    @Test
    public void shouldReturnLocale() {
        LocaleNegotiator negotiator = new LocaleNegotiator("test", "en", null);
        assertNotNull(negotiator.getLocale());
    }
}
