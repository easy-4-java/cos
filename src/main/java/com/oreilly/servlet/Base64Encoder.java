// Copyright (C) 1999-2002 by Jason Hunter <jhunter_AT_acm_DOT_org>.
// All rights reserved.  Use of this class is limited.
// Please see the LICENSE for more information.

package com.oreilly.servlet;

import lombok.NonNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** 
 * A class to encode Base64 streams and strings.  
 * See RFC 1521 section 5.2 for details of the Base64 algorithm.
 * &lt;p&gt;
 * This class can be used for encoding strings:
 * &lt;blockquote&gt;&lt;pre&gt;
 * String unencoded = "webmaster:try2gueSS";
 * String encoded = Base64Encoder.encode(unencoded);
 * &lt;/pre&gt;&lt;/blockquote&gt;
 * or for encoding streams:
 * &lt;blockquote&gt;&lt;pre&gt;
 * OutputStream out = new Base64Encoder(System.out);
 * &lt;/pre&gt;&lt;/blockquote&gt;
 *
 * @author &lt;b&gt;Jason Hunter&lt;/b&gt;, Copyright &#169; 2000
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see Base64Decoder
 * @version 1.2, 2002/11/01, added encode(byte[]) method to better handle
 *                           binary data (thanks to Sean Graham)
 * @version 1.1, 2000/11/17, fixed bug with sign bit for char values
 * @version 1.0, 2000/06/11
 */
public class Base64Encoder extends FilterOutputStream {

  // Line-wrapping follows the classic implementation: every 57 input bytes
  // produce one 76-char line followed by a bare LF, including the final
  // line when it is exactly full. The JDK MIME encoder only inserts its
  // separator *between* lines, so we use the basic encoder and write the
  // line feed ourselves.
  private static final Base64.Encoder ENCODER = Base64.getEncoder();
  private static final byte[] LF = {'\n'};

  // 57 input bytes produce one 76-char output line plus the line feed;
  // flushing each full line as it arrives keeps streaming memory O(1)
  private static final int LINE_BYTES = 57;

  private final byte[] line = new byte[LINE_BYTES];
  private int lineCount;

  /**
   * Constructs a new Base64 encoder that writes output to the given
   * OutputStream.
   *
   * @param out the output stream
   */
  public Base64Encoder(OutputStream out) {
    super(out);
  }

  /**
   * Writes the given byte to the output stream in an encoded form.
   *
   * @exception IOException if an I/O error occurs
   */
  public void write(int b) throws IOException {
    line[lineCount++] = (byte) b;
    if (lineCount == LINE_BYTES) {
      flushLine();
    }
  }

  /**
   * Writes the given byte array to the output stream in an 
   * encoded form.
   *
   * @param buf the data to be written
   * @param off the start offset of the data
   * @param len the length of the data
   * @exception IOException if an I/O error occurs
   */
  public void write(byte @NonNull [] buf, int off, int len) throws IOException {
    while (len > 0) {
      int n = Math.min(LINE_BYTES - lineCount, len);
      System.arraycopy(buf, off, line, lineCount, n);
      lineCount += n;
      off += n;
      len -= n;
      if (lineCount == LINE_BYTES) {
        flushLine();
      }
    }
  }

  // Encode and emit one full 76-char line plus the line feed
  private void flushLine() throws IOException {
    out.write(ENCODER.encode(line));
    out.write(LF);
    lineCount = 0;
  }

  /**
   * Closes the stream, this MUST be called to ensure proper padding is
   * written to the end of the output stream.
   *
   * @exception IOException if an I/O error occurs
   */
  public void close() throws IOException {
    if (lineCount > 0) {
      byte[] tail = new byte[lineCount];
      System.arraycopy(line, 0, tail, 0, lineCount);
      out.write(ENCODER.encode(tail));
    }
    super.close();
  }

  /**
   * Returns the encoded form of the given unencoded string.  The encoder
   * uses the ISO-8859-1 (Latin-1) encoding to convert the string to bytes.
   * For greater control over the encoding, encode the string to bytes
   * yourself and use encode(byte[]).
   *
   * @param unencoded the string to encode
   * @return the encoded form of the unencoded string
   */
  public static String encode(String unencoded) {
    byte[] bytes = null;
      bytes = unencoded.getBytes(StandardCharsets.ISO_8859_1);
      return encode(bytes);
  }

  /**
   * Returns the encoded form of the given unencoded string.
   *
   * @param bytes the bytes to encode
   * @return the encoded form of the unencoded string
   */
  public static String encode(byte[] bytes) {
    // Encode in one JDK call (SIMD-accelerated), then insert a line feed
    // after every 76-char line, including the final line when it is exactly
    // full — matching the classic line-wrapping behaviour
    String encoded = ENCODER.encodeToString(bytes);
    if (encoded.length() < 76) {
      return encoded;
    }
    StringBuilder sb = new StringBuilder(encoded.length() + encoded.length() / 76);
    int i = 0;
    for (; i + 76 <= encoded.length(); i += 76) {
      sb.append(encoded, i, i + 76).append('\n');
    }
    if (i < encoded.length()) {
      sb.append(encoded, i, encoded.length());
    }
    return sb.toString();
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.err.println(
        "Usage: java com.oreilly.servlet.Base64Encoder fileToEncode");
      return;
    }

      try (Base64Encoder encoder = new Base64Encoder(System.out); BufferedInputStream in = new BufferedInputStream(new FileInputStream(args[0]))) {

          byte[] buf = new byte[4 * 1024];  // 4K buffer
          int bytesRead;
          while ((bytesRead = in.read(buf)) != -1) {
              encoder.write(buf, 0, bytesRead);
          }
      }
  }
}
