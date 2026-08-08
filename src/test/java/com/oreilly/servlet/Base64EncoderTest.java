package com.oreilly.servlet;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class Base64EncoderTest {

    @Test
    public void shouldEncodeSimpleString() {
        String encoded = Base64Encoder.encode("Hello");
        assertEquals("SGVsbG8=", encoded);
    }

    @Test
    public void shouldEncodeEmptyString() {
        String encoded = Base64Encoder.encode("");
        assertEquals("", encoded);
    }

    @Test
    public void shouldEncodeBytes() {
        byte[] bytes = "Hello".getBytes(StandardCharsets.ISO_8859_1);
        String encoded = Base64Encoder.encode(bytes);
        assertEquals("SGVsbG8=", encoded);
    }

    @Test
    public void shouldEncodeViaOutputStream() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Base64Encoder encoder = new Base64Encoder(out);

        byte[] data = "Hello".getBytes(StandardCharsets.ISO_8859_1);
        encoder.write(data, 0, data.length);
        encoder.close();

        String result = out.toString("ISO-8859-1");
        assertEquals("SGVsbG8=", result);
    }

    @Test
    public void shouldWriteSingleByte() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Base64Encoder encoder = new Base64Encoder(out);

        encoder.write('A'); // 0x41
        encoder.close();

        String result = out.toString("ISO-8859-1");
        assertEquals("QQ==", result);
    }

    @Test
    public void shouldWriteNegativeByte() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Base64Encoder encoder = new Base64Encoder(out);

        // Negative byte (-1 = 0xFF) should be handled by adding 256
        encoder.write(-1);
        encoder.close();

        String result = out.toString("ISO-8859-1");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void shouldAddNewlineEvery76Chars() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Base64Encoder encoder = new Base64Encoder(out);

        // 57 input bytes = 76 output chars (57 * 4/3 = 76)
        byte[] data = new byte[114]; // 57 * 2 = 114 bytes = 2 lines of 76 chars
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) ('A' + (i % 26));
        }
        encoder.write(data, 0, data.length);
        encoder.close();

        String result = out.toString("ISO-8859-1");
        assertTrue(result.contains("\n"));
    }

    @Test
    public void shouldRoundTrip() {
        String original = "The quick brown fox jumps over the lazy dog 0123456789";
        String encoded = Base64Encoder.encode(original);
        String decoded = Base64Decoder.decode(encoded);
        assertEquals(original, decoded);
    }

    @Test
    public void shouldEncodeWithPaddingOneLeftover() {
        // "A" is 1 byte, needs 2 '=' padding
        String encoded = Base64Encoder.encode("A");
        assertEquals("QQ==", encoded);
    }

    @Test
    public void shouldEncodeWithPaddingTwoLeftovers() {
        // "AB" is 2 bytes, needs 1 '=' padding
        String encoded = Base64Encoder.encode("AB");
        assertEquals("QUI=", encoded);
    }

    @Test
    public void shouldEncodeWithNoPadding() {
        // "ABC" is 3 bytes, no padding needed
        String encoded = Base64Encoder.encode("ABC");
        assertEquals("QUJD", encoded);
    }
}
