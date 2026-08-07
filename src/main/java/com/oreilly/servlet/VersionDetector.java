// Copyright (C) 2000-2001 by Jason Hunter <jhunter_AT_acm_DOT_org>.
// All rights reserved.  Use of this class is limited.
// Please see the LICENSE for more information.

package com.oreilly.servlet;

/**
 * A class to determine the current Servlet API version number, and the
 * current JDK version number.  It looks at the available classes and
 * variables to make the determination.
 * <p>
 * Servlet API detection probes the runtime classpath for signature APIs of
 * each major Jakarta Servlet / Servlet release.  It can detect Servlet 3.0+
 * and Jakarta Servlet 4.0+ (Jakarta EE 8-10+).  The result is reported as
 * the major.minor version string of the Jakarta Servlet API actually
 * present at runtime (e.g. {@code "6.0"} for {@code jakarta.servlet-api 6.0.x}).
 * <p>
 * JDK version detection uses the {@link Runtime.Version} introduced in
 * JDK 9, which is the canonical source of truth; on a JDK 8 or older JVM
 * (rare today) it falls back to the legacy {@code java.specification.version}
 * system property.  The result is reported as the JDK feature (major)
 * version, e.g. {@code "17"} or {@code "21"}.
 * <p>
 * It can be used like this:
 * <blockquote><pre>
 * String servletVersion = VersionDetector.getServletVersion();
 *
 * String javaVersion = VersionDetector.getJavaVersion();
 * </pre></blockquote>
 *
 * @author <b>Jason Hunter</b>, Copyright &#169; 2000
 * @version 2.0, 2026/08/07, rewritten for Jakarta EE 9-10 and JDK 9+;
 *                           uses {@code Runtime.Version} for JDK detection
 */
public class VersionDetector {

  // volatile so concurrent first-callers see a fully-published value
  private static volatile String servletVersion;
  private static volatile String javaVersion;

  /**
   * Determines the Servlet API major.minor version number by probing
   * the runtime classpath for signature classes/fields of each release.
   * <p>
   * The probe ladder is intentionally coarse: it stops at the highest
   * version whose signature is present, and returns the highest version
   * whose probe succeeded.  If even the Servlet 3.0 baseline
   * ({@code jakarta.servlet.AsyncContext}) is missing, {@code "2.x"}
   * is returned as a sentinel for "pre-Servlet-3.0" — though in practice
   * this branch is unreachable on any supported Jakarta EE runtime.
   *
   * @return a String representation of the servlet major.minor version,
   *         e.g. {@code "6.0"}; never {@code null}
   */
  public static String getServletVersion() {
    // Fast path: double-checked style via volatile read
    String cached = servletVersion;
    if (cached != null) {
      return cached;
    }

    synchronized (VersionDetector.class) {
      cached = servletVersion;
      if (cached != null) {
        return cached;
      }

      servletVersion = detectServletVersion();
      return servletVersion;
    }
  }

  /**
   * Walks the probe ladder against either the {@code jakarta.*} or
   * {@code javax.*} Servlet package namespace, whichever is on the
   * classpath.  Returns the highest major.minor version whose signature
   * class is present, or {@code "2.x"} as a baseline sentinel when no
   * Servlet 3.0+ signature can be located.
   */
  private static String detectServletVersion() {
    // Determine which Servlet package namespace is on the classpath.
    // Jakarta EE 9+ uses jakarta.*; Jakarta EE 8 and earlier (including
    // all javax.servlet-api releases) uses javax.*.  We probe a class
    // that exists in both namespaces from Servlet 3.0 onward.
    String pkg;
    if (classExists("jakarta.servlet.AsyncContext")) {
      pkg = "jakarta.servlet";
    } else if (classExists("javax.servlet.AsyncContext")) {
      pkg = "javax.servlet";
    } else {
      // No Servlet 3.0+ API on the classpath at all.
      return "2.x";
    }

    String ver = "3.0"; // baseline once AsyncContext is found

    // Servlet 4.0 / Jakarta EE 8: PushBuilder under <ns>.http
    if (classExists(pkg + ".http.PushBuilder")) {
      ver = "4.0";
    }

    // Jakarta Servlet 5.0 (Jakarta EE 9) is a namespace-only rename
    // (javax.* -> jakarta.*); there is no signature class that
    // distinguishes 5.0 from 4.0 on its own.  When the runtime is on
    // the jakarta.* namespace, the verdict "4.0" actually means
    // "Jakarta Servlet 5.0 or newer (pre-6.0)" — bump it to "5.0".
    if ("jakarta.servlet".equals(pkg) && "4.0".equals(ver)) {
      ver = "5.0";
    }

    // Jakarta Servlet 6.0 / Jakarta EE 10: ServletConnection lives in
    // the top-level jakarta.servlet package (no javax equivalent —
    // ServletConnection was introduced after the namespace rename).
    if ("jakarta.servlet".equals(pkg) && classExists("jakarta.servlet.ServletConnection")) {
      ver = "6.0";
    }

    return ver;
  }

  /**
   * Returns {@code true} if the given fully-qualified class name can be
   * resolved by the current classloader.  Only the failure modes that
   * legitimately mean "the class is not present" are caught
   * ({@link LinkageError} / {@link ClassNotFoundException}); any other
   * exception is re-thrown so genuine problems aren't swallowed.
   */
  private static boolean classExists(String fqcn) {
    try {
      Class.forName(fqcn);
      return true;
    } catch (LinkageError | ClassNotFoundException notPresent) {
      return false;
    }
  }

  /**
   * Determines the JDK version number.
   * <p>
   * On JDK 9 and later this uses {@link Runtime#version()} (via reflection,
   * so this source compiles on JDK 8 too) and returns the feature (major)
   * version as a string (e.g. {@code "17"}).  On JDK 8 or older it falls
   * back to parsing {@code java.specification.version} (e.g. {@code "1.8"}
   * is normalized to {@code "8"}).
   *
   * @return a String representation of the JDK feature version, never
   *         {@code null}
   */
  public static String getJavaVersion() {
    String cached = javaVersion;
    if (cached != null) {
      return cached;
    }

    synchronized (VersionDetector.class) {
      cached = javaVersion;
      if (cached != null) {
        return cached;
      }

      javaVersion = detectJavaVersion();
      return javaVersion;
    }
  }

  /**
   * JDK version detection using {@code Runtime.version()} via reflection
   * (so the source still compiles on JDK 8).  Falls back to
   * {@code java.specification.version} when {@code Runtime.version()} is
   * not available — which on any supported runtime means JDK 8 or older.
   */
  private static String detectJavaVersion() {
    // Try Runtime.version().feature() via reflection (JDK 9+).
    try {
      Runtime runtime = Runtime.getRuntime();
      java.lang.reflect.Method versionMethod = Runtime.class.getMethod("version");
      Object version = versionMethod.invoke(runtime);
      if (version != null) {
        java.lang.reflect.Method featureMethod =
            version.getClass().getMethod("feature");
        Object feature = featureMethod.invoke(version);
        if (feature instanceof Integer) {
          return feature.toString();
        }
      }
    } catch (LinkageError | ReflectiveOperationException ignore) {
      // Runtime.version() not available — fall through to the legacy path.
    }

    // Legacy path: parse java.specification.version (JDK 8 and earlier).
    String spec = System.getProperty("java.specification.version", "");
    if (spec.isEmpty()) {
      return "unknown";
    }
    if (spec.startsWith("1.") && spec.length() >= 3) {
      // "1.8" -> "8"; "1.7" -> "7"
      try {
        int minor = Integer.parseInt(spec.substring(2));
        return Integer.toString(minor);
      } catch (NumberFormatException nfe) {
        return spec;
      }
    }
    // Newer scheme (e.g. "17") — return as-is.
    return spec;
  }
}