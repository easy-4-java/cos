package com.oreilly.servlet;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class Base64DecoderTest {

    @Test
    public void shouldDecodeSimpleString() {
        // "Hello" in Base64 is "SGVsbG8="
        String decoded = Base64Decoder.decode("SGVsbG8=");
        assertEquals("Hello", decoded);
    }

    @Test
    public void shouldDecodeToBytes() {
        byte[] bytes = Base64Decoder.decodeToBytes("SGVsbG8=");
        assertNotNull(bytes);
        assertEquals("Hello", new String(bytes, StandardCharsets.ISO_8859_1));
    }

    @Test
    public void shouldDecodeEmptyString() {
        String decoded = Base64Decoder.decode("");
        assertEquals("", decoded);
    }

    @Test
    public void shouldDecodeViaInputStream() throws IOException {
        String encoded = "SGVsbG8gV29ybGQ=";
        byte[] encodedBytes = encoded.getBytes(StandardCharsets.ISO_8859_1);
        Base64Decoder decoder = new Base64Decoder(new ByteArrayInputStream(encodedBytes));

        byte[] buf = new byte[1024];
        int bytesRead = decoder.read(buf);
        String result = new String(buf, 0, bytesRead);
        assertEquals("Hello World", result);
    }

    @Test
    public void shouldReadSingleByte() throws IOException {
        String encoded = "QQ==";  // "A"
        byte[] encodedBytes = encoded.getBytes(StandardCharsets.ISO_8859_1);
        Base64Decoder decoder = new Base64Decoder(new ByteArrayInputStream(encodedBytes));

        int b = decoder.read();
        assertEquals('A', b);
    }

    @Test
    public void shouldReturnMinusOneAtEndOfStream() throws IOException {
        Base64Decoder decoder = new Base64Decoder(new ByteArrayInputStream(new byte[0]));
        assertEquals(-1, decoder.read());
    }

    @Test
    public void shouldHandlePadding() throws IOException {
        // "AB" -> "QUI="
        String encoded = "QUI=";
        byte[] encodedBytes = encoded.getBytes(StandardCharsets.ISO_8859_1);
        Base64Decoder decoder = new Base64Decoder(new ByteArrayInputStream(encodedBytes));

        int b1 = decoder.read();
        assertEquals('A', b1);
        int b2 = decoder.read();
        assertEquals('B', b2);
    }

    @Test(expected = IOException.class)
    public void shouldThrowOnTooSmallBuffer() throws IOException {
        String encoded = "SGVsbG8=";
        byte[] encodedBytes = encoded.getBytes(StandardCharsets.ISO_8859_1);
        Base64Decoder decoder = new Base64Decoder(new ByteArrayInputStream(encodedBytes));

        byte[] tinyBuf = new byte[1];
        decoder.read(tinyBuf, 5, 10);
    }

    @Test
    public void shouldReturnMinusOneWhenReadBufAtEnd() throws IOException {
        Base64Decoder decoder = new Base64Decoder(new ByteArrayInputStream(new byte[0]));
        byte[] buf = new byte[10];
        assertEquals(-1, decoder.read(buf, 0, 10));
    }

    @Test
    public void shouldReturnZeroWhenLenIsZero() throws IOException {
        Base64Decoder decoder = new Base64Decoder(new ByteArrayInputStream(new byte[0]));
        byte[] buf = new byte[10];
        // This test just ensures no crash
        decoder.read(buf, 0, 0);
    }

    @Test
    public void shouldDecodeLongerString() {
        String original = "The quick brown fox jumps over the lazy dog";
        String encoded = Base64Encoder.encode(original);
        String decoded = Base64Decoder.decode(encoded);
        assertEquals(original, decoded);
    }

    @Test
    public void shouldDecodeWithWhitespace() throws IOException {
        // Encoded string with whitespace should be skipped
        String encoded = "SG Vs\nbG8=";
        byte[] encodedBytes = encoded.getBytes(StandardCharsets.ISO_8859_1);
        Base64Decoder decoder = new Base64Decoder(new ByteArrayInputStream(encodedBytes));

        byte[] buf = new byte[1024];
        int bytesRead = decoder.read(buf);
        assertTrue(bytesRead > 0);
        String result = new String(buf, 0, bytesRead);
        assertEquals("Hello", result);
    }
}
