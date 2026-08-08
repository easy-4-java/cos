package com.oreilly.servlet.multipart;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class MacBinaryDecoderOutputStreamTest {

    @Test
    public void shouldPassThroughNonMacBinaryData() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MacBinaryDecoderOutputStream decoder = new MacBinaryDecoderOutputStream(out);

        // Write data that doesn't trigger MacBinary decoding
        // (bytes 0-82 are header, bytes 83-86 are data fork length)
        for (int i = 0; i < 83; i++) {
            decoder.write(i & 0xff);
        }
        // Set data fork length at bytes 83-86 to 0
        decoder.write(0);
        decoder.write(0);
        decoder.write(0);
        decoder.write(0);
        // Bytes 87-127 are more header
        for (int i = 87; i < 128; i++) {
            decoder.write(i & 0xff);
        }
        // No data fork since length is 0

        // Output should be empty since data fork length is 0
        assertEquals(0, out.size());
    }

    @Test
    public void shouldExtractDataFork() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MacBinaryDecoderOutputStream decoder = new MacBinaryDecoderOutputStream(out);

        // Write header bytes 0-82
        for (int i = 0; i < 83; i++) {
            decoder.write(0);
        }

        // Write data fork length = 5 at bytes 83-86 (big-endian)
        decoder.write(0);
        decoder.write(0);
        decoder.write(0);
        decoder.write(5);

        // Write remaining header bytes 87-127
        for (int i = 87; i < 128; i++) {
            decoder.write(0);
        }

        // Write data fork (5 bytes)
        byte[] dataFork = {1, 2, 3, 4, 5};
        for (byte b : dataFork) {
            decoder.write(b);
        }

        // Write trailing bytes (should be ignored)
        decoder.write(99);
        decoder.write(100);

        assertEquals(5, out.size());
        byte[] result = out.toByteArray();
        assertArrayEquals(dataFork, result);
    }

    @Test
    public void shouldWriteByteArrayDirectly() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MacBinaryDecoderOutputStream decoder = new MacBinaryDecoderOutputStream(out);

        // Write header with data fork length = 3
        byte[] header = new byte[128];
        header[85] = 0;
        header[86] = 3; // data fork length = 3
        decoder.write(header);

        // Write data fork as byte array
        byte[] data = {10, 20, 30};
        decoder.write(data);
        assertEquals(3, out.size());
    }

    @Test
    public void shouldWriteByteArrayWithOffset() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MacBinaryDecoderOutputStream decoder = new MacBinaryDecoderOutputStream(out);

        // Write header with data fork length = 2
        byte[] header = new byte[128];
        header[86] = 2;
        decoder.write(header);

        // Write data fork as byte array with offset
        byte[] data = {0, 0, 10, 20, 0, 0};
        decoder.write(data, 2, 2);
        assertEquals(2, out.size());
        assertEquals(10, out.toByteArray()[0]);
        assertEquals(20, out.toByteArray()[1]);
    }

    @Test
    public void shouldIgnoreBytesPastDataFork() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MacBinaryDecoderOutputStream decoder = new MacBinaryDecoderOutputStream(out);

        // Write header with data fork length = 0
        byte[] header = new byte[128];
        decoder.write(header);

        // Write trailing bytes - should be ignored
        decoder.write(1);
        decoder.write(2);
        assertEquals(0, out.size());
    }

    @Test
    public void shouldHandleBulkWritePastEnd() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MacBinaryDecoderOutputStream decoder = new MacBinaryDecoderOutputStream(out);

        // Write header with data fork length = 0
        byte[] header = new byte[128];
        decoder.write(header);

        // Bulk write past end - should be ignored
        byte[] trailing = {1, 2, 3, 4, 5};
        decoder.write(trailing, 0, trailing.length);
        assertEquals(0, out.size());
    }

    @Test
    public void shouldHandleBulkWriteEntirelyInDataFork() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MacBinaryDecoderOutputStream decoder = new MacBinaryDecoderOutputStream(out);

        // Write header with data fork length = 5
        byte[] header = new byte[128];
        header[86] = 5;
        decoder.write(header);

        // Bulk write entirely within data fork
        byte[] data = {1, 2, 3, 4, 5};
        decoder.write(data, 0, 5);
        assertEquals(5, out.size());
    }
}
