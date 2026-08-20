package com.oreilly.servlet;

import org.junit.Assume;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;

/**
 * Throughput benchmarks for the Base64 stream and string APIs.
 * <p>
 * Skipped unless {@code -Dcos.perf=true} is passed, because benchmark numbers
 * are machine-dependent and would slow down the regular build:
 * {@code mvn test -Dcos.perf=true -Dtest='*PerformanceTest'}
 * <p>
 * Correctness is verified before timing (encode -> decode -> compare), so a
 * regression that loses or corrupts data fails the benchmark instead of
 * silently inflating the throughput numbers.
 */
public class Base64PerformanceTest {

    private static final int DATA_SIZE = 1024 * 1024;  // 1MB
    private static final long WINDOW_NANOS = 2_000_000_000L;  // 2s per measurement

    @Test
    public void streamEncodeDecodeThroughput() throws Exception {
        Assume.assumeTrue(Boolean.getBoolean("cos.perf"));
        byte[] data = randomBytes(DATA_SIZE);

        // Warm up the JIT
        for (int i = 0; i < 5; i++) {
            assertArrayEquals(data, decodeStream(encodeStream(data)));
        }

        // Encode: verify one round-trip first, then time pure encoding
        assertArrayEquals(data, decodeStream(encodeStream(data)));
        long t0 = System.nanoTime();
        int encIters = 0;
        while (System.nanoTime() - t0 < WINDOW_NANOS) {
            encodeStream(data);
            encIters++;
        }
        long encNanos = System.nanoTime() - t0;

        // Decode: verify correctness once, then time pure decoding
        byte[] encoded = encodeStream(data);
        assertArrayEquals(data, decodeStream(encoded));
        t0 = System.nanoTime();
        int decIters = 0;
        while (System.nanoTime() - t0 < WINDOW_NANOS) {
            decodeStream(encoded);
            decIters++;
        }
        long decNanos = System.nanoTime() - t0;

        double encMBps = DATA_SIZE / 1048576.0 * encIters / (encNanos / 1e9);
        double decMBps = encoded.length / 1048576.0 * decIters / (decNanos / 1e9);
        System.out.printf("[perf] Base64 stream: encode %.1f MB/s (%d iters), decode %.1f MB/s (%d iters)%n",
                encMBps, encIters, decMBps, decIters);
    }

    @Test
    public void stringApiThroughput() throws Exception {
        Assume.assumeTrue(Boolean.getBoolean("cos.perf"));
        byte[] data = randomBytes(DATA_SIZE);

        String encoded = null;
        for (int i = 0; i < 5; i++) {
            encoded = Base64Encoder.encode(data);
            assertArrayEquals(data, Base64Decoder.decodeToBytes(encoded));
        }
        assertArrayEquals(data, Base64Decoder.decodeToBytes(encoded));

        long t0 = System.nanoTime();
        int encIters = 0;
        while (System.nanoTime() - t0 < WINDOW_NANOS) {
            encoded = Base64Encoder.encode(data);
            encIters++;
        }
        long encNanos = System.nanoTime() - t0;

        t0 = System.nanoTime();
        int decIters = 0;
        while (System.nanoTime() - t0 < WINDOW_NANOS) {
            Base64Decoder.decodeToBytes(encoded);
            decIters++;
        }
        long decNanos = System.nanoTime() - t0;

        double encMBps = DATA_SIZE / 1048576.0 * encIters / (encNanos / 1e9);
        double decMBps = encoded.length() / 1048576.0 * decIters / (decNanos / 1e9);
        System.out.printf("[perf] Base64 string API: encode %.1f MB/s (%d iters), decode %.1f MB/s (%d iters)%n",
                encMBps, encIters, decMBps, decIters);
    }

    private static byte[] encodeStream(byte[] data) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream((int) (data.length * 1.4));
        Base64Encoder encoder = new Base64Encoder(out);
        encoder.write(data, 0, data.length);
        encoder.close();
        return out.toByteArray();
    }

    private static byte[] decodeStream(byte[] encoded) throws IOException {
        Base64Decoder decoder = new Base64Decoder(new ByteArrayInputStream(encoded));
        byte[] buf = new byte[8192];
        ByteArrayOutputStream out = new ByteArrayOutputStream((int) (encoded.length * 0.75));
        int n;
        while ((n = decoder.read(buf)) != -1) {
            out.write(buf, 0, n);
        }
        return out.toByteArray();
    }

    private static byte[] randomBytes(int size) {
        byte[] data = new byte[size];
        new Random(42).nextBytes(data);
        return data;
    }
}
