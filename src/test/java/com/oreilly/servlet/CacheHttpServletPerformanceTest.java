package com.oreilly.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Assume;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

/**
 * Concurrency benchmark for CacheHttpServlet cache-hit throughput.
 * <p>
 * Skipped unless {@code -Dcos.perf=true} is passed:
 * {@code mvn test -Dcos.perf=true -Dtest='*PerformanceTest'}
 */
public class CacheHttpServletPerformanceTest {

    @Test
    public void concurrentCacheHitThroughput() throws Exception {
        Assume.assumeTrue(Boolean.getBoolean("cos.perf"));
        final int threads = 4;
        final int perThread = 500_000;

        CacheHttpServlet servlet = new CacheHttpServlet() {
            @Override public long getLastModified(HttpServletRequest req) { return 1000L; }
            @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
                res.getWriter().println("cached-body");
            }
        };
        HttpServletRequest req = new StubHttpServletRequest();

        // Prime the cache, then warm up
        servlet.service(req, new StubHttpServletResponse());
        for (int i = 0; i < 1000; i++) {
            servlet.service(req, new StubHttpServletResponse());
        }

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger errors = new AtomicInteger();
        long t0 = System.nanoTime();
        for (int t = 0; t < threads; t++) {
            new Thread(() -> {
                try {
                    start.await();
                    for (int i = 0; i < perThread; i++) {
                        try {
                            servlet.service(req, new StubHttpServletResponse());
                        } catch (Exception e) {
                            errors.incrementAndGet();
                        }
                    }
                } catch (InterruptedException e) {
                    errors.incrementAndGet();
                } finally {
                    done.countDown();
                }
            }).start();
        }
        start.countDown();
        done.await();
        long nanos = System.nanoTime() - t0;

        assertEquals("benchmark run must be error-free", 0, errors.get());
        long total = (long) threads * perThread;
        double opsPerSec = total / (nanos / 1e9);
        System.out.printf("[perf] CacheHttpServlet cache hit: %d threads, %.0f ops/s (%d total, %d errors)%n",
                threads, opsPerSec, total, errors.get());
    }
}
