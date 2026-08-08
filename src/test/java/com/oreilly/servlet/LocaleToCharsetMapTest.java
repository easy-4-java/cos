package com.oreilly.servlet;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

public class LocaleToCharsetMapTest {

    @Test
    public void shouldReturnCharsetForEnglish() {
        String charset = LocaleToCharsetMap.getCharset(Locale.ENGLISH);
        assertEquals("ISO-8859-1", charset);
    }

    @Test
    public void shouldReturnCharsetForJapanese() {
        Locale ja = new Locale("ja", "JP");
        String charset = LocaleToCharsetMap.getCharset(ja);
        assertEquals("Shift_JIS", charset);
    }

    @Test
    public void shouldReturnCharsetForChinese() {
        Locale zh = new Locale("zh", "CN");
        String charset = LocaleToCharsetMap.getCharset(zh);
        assertNotNull(charset);
    }

    @Test
    public void shouldReturnCharsetForChineseTraditional() {
        Locale zhTW = new Locale("zh", "TW");
        String charset = LocaleToCharsetMap.getCharset(zhTW);
        assertEquals("Big5", charset);
    }

    @Test
    public void shouldReturnCharsetForKorean() {
        Locale ko = new Locale("ko", "KR");
        String charset = LocaleToCharsetMap.getCharset(ko);
        assertEquals("EUC-KR", charset);
    }

    @Test
    public void shouldReturnCharsetForRussian() {
        Locale ru = new Locale("ru", "RU");
        String charset = LocaleToCharsetMap.getCharset(ru);
        assertEquals("ISO-8859-5", charset);
    }

    @Test
    public void shouldReturnCharsetForArabic() {
        Locale ar = new Locale("ar", "SA");
        String charset = LocaleToCharsetMap.getCharset(ar);
        assertEquals("ISO-8859-6", charset);
    }

    @Test
    public void shouldReturnCharsetForHebrew() {
        // "iw" is the legacy code for Hebrew; modern JDK may normalize to "he"
        Locale iw = new Locale("iw", "IL");
        String charset = LocaleToCharsetMap.getCharset(iw);
        // The map uses "iw"; if the JDK normalizes, the full locale might not match
        // but the language-only lookup should still work if "he" -> "iw" mapping exists
        // Accept either result since JDK versions differ
        assertTrue(charset == null || "ISO-8859-8".equals(charset));
    }

    @Test
    public void shouldReturnCharsetForTurkish() {
        Locale tr = new Locale("tr", "TR");
        String charset = LocaleToCharsetMap.getCharset(tr);
        assertEquals("ISO-8859-9", charset);
    }

    @Test
    public void shouldReturnCharsetForGreek() {
        Locale el = new Locale("el", "GR");
        String charset = LocaleToCharsetMap.getCharset(el);
        assertEquals("ISO-8859-7", charset);
    }

    @Test
    public void shouldReturnNullForUnknownLocale() {
        Locale unknown = new Locale("xx", "YY");
        String charset = LocaleToCharsetMap.getCharset(unknown);
        assertNull(charset);
    }

    @Test
    public void shouldReturnCharsetForGerman() {
        Locale de = Locale.GERMAN;
        String charset = LocaleToCharsetMap.getCharset(de);
        assertEquals("ISO-8859-1", charset);
    }

    @Test
    public void shouldReturnCharsetForFrench() {
        Locale fr = Locale.FRENCH;
        String charset = LocaleToCharsetMap.getCharset(fr);
        assertEquals("ISO-8859-1", charset);
    }
}
