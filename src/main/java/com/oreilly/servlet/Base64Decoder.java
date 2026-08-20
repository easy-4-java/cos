// Copyright (C) 1999-2002 by Jason Hunter <jhunter_AT_acm_DOT_org>.
// All rights reserved.  Use of this class is limited.
// Please see the LICENSE for more information.

package com.oreilly.servlet;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/** 
 * A class to decode Base64 streams and strings.  
 * See RFC 1521 section 5.2 for details of the Base64 algorithm.
 * &lt;p&gt;
 * This class can be used for decoding strings:
 * &lt;blockquote&gt;&lt;pre&gt;
 * String encoded = "d2VibWFzdGVyOnRyeTJndWVTUw";
 * String decoded = Base64Decoder.decode(encoded);
 * &lt;/pre&gt;&lt;/blockquote&gt;
 * or for decoding streams:
 * &lt;blockquote&gt;&lt;pre&gt;
 * InputStream in = new Base64Decoder(System.in);
 * &lt;/pre&gt;&lt;/blockquote&gt;
 *
 * @author &lt;b&gt;Jason Hunter&lt;/b&gt;, Copyright &#169; 2000
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see Base64Encoder
 * @version 1.1, 2002/11/01, added decodeToBytes() to better handle binary
 *                           data (thanks to Sean Graham)
 * @version 1.0, 2000/06/11
 */
public class Base64Decoder extends FilterInputStream {

  // Decode in bounded chunks so streaming large inputs stays O(1) in memory.
  // Chunk size is a multiple of four so chunk boundaries never split a
  // 4-character group; the final chunk (at EOF or padding) is padded.
  private static final int CHUNK_SIZE = 8 * 1024;

  // State of the current decoding pass
  private static final Base64.Decoder DECODER = Base64.getDecoder();
  private final byte[] cleaned = new byte[CHUNK_SIZE];
  private final byte[] chunk = new byte[4096];
  private int chunkPos;
  private int chunkLen;
  private int cleanedCount;
  private byte[] decodedBuf = new byte[0];
  private int decodedPos;
  private int decodedLen;
  private boolean paddingSeen;

  /**
   * Constructs a new Base64 decoder that reads input from the given
   * InputStream.
   *
   * @param in the input stream
   */
  public Base64Decoder(InputStream in) {
    super(in);
  }

  /**
   * Returns the next decoded character from the stream, or -1 if
   * end of stream was reached.
   *
   * @return  the decoded character, or -1 if the end of the
   *      input stream is reached
   * @exception IOException if an I/O error occurs
   */
  public int read() throws IOException {
    if (decodedPos >= decodedLen) {
      if (!fillDecoded()) {
        return -1;
      }
    }
    return decodedBuf[decodedPos++] & 0xFF;
  }

  /**
   * Reads decoded data into an array of bytes and returns the actual 
   * number of bytes read, or -1 if end of stream was reached.
   *
   * @param buf the buffer into which the data is read
   * @param off the start offset of the data
   * @param len the maximum number of bytes to read
   * @return  the actual number of bytes read, or -1 if the end of the
   *      input stream is reached
   * @exception IOException if an I/O error occurs
   */
  public int read(byte[] buf, int off, int len) throws IOException {
    if (buf.length < (len + off - 1)) {
      throw new IOException("The input buffer is too small: " + len + 
       " bytes requested starting at offset " + off + " while the buffer " +
       " is only " + buf.length + " bytes long.");
    }

    // This could of course be optimized
    int i;
    for (i = 0; i < len; i++) {
      int x = read();
      if (x == -1 && i == 0) {  // an immediate -1 returns -1
        return -1;
      }
      else if (x == -1) {       // a later -1 returns the chars read so far
        break;
      }
      buf[off + i] = (byte) x;
    }
    return i;
  }

  // Read the next chunk of encoded input, skipping whitespace and stopping
  // at the '=' padding or end of stream, then decode it into decodedBuf.
  // Returns false when no further decoded bytes can be produced.
  private boolean fillDecoded() throws IOException {
    if (paddingSeen) {
      return false;
    }
    cleanedCount = 0;
    while (cleanedCount < CHUNK_SIZE) {
      // Refill the scan buffer, preserving any unprocessed tail from the
      // previous fillDecoded() call via chunkPos/chunkLen
      if (chunkPos >= chunkLen) {
        chunkLen = in.read(chunk, 0, chunk.length);
        chunkPos = 0;
        if (chunkLen == -1) {
          break;
        }
      }
      for (; chunkPos < chunkLen && cleanedCount < CHUNK_SIZE; chunkPos++) {
        int x = chunk[chunkPos] & 0xFF;
        // Fast path for the whitespace that actually occurs in Base64
        // input, falling back to the full Unicode check for the rest
        if (x == ' ' || x == '\n' || x == '\r' || x == '\t' || x == '\f'
            || Character.isWhitespace((char) x)) {
          continue;
        }
        if (x == '=') {
          paddingSeen = true;
          break;
        }
        cleaned[cleanedCount++] = (byte) x;
      }
      if (paddingSeen) {
        break;
      }
    }
    if (cleanedCount == 0) {
      return false;
    }
    try {
      // Pad up to a multiple of four so the JDK decoder accepts partial
      // final groups (2 chars -> 1 byte, 3 chars -> 2 bytes)
      int padding = (4 - cleanedCount % 4) % 4;
      byte[] padded = new byte[cleanedCount + padding];
      System.arraycopy(cleaned, 0, padded, 0, cleanedCount);
      for (int i = cleanedCount; i < padded.length; i++) {
        padded[i] = '=';
      }
      decodedBuf = DECODER.decode(padded);
      decodedLen = decodedBuf.length;
      decodedPos = 0;
      return true;
    }
    catch (IllegalArgumentException e) {
      // Malformed input: behave as end of stream, like the classic decoder
      return false;
    }
  }

  /**
   * Returns the decoded form of the given encoded string, as a String.
   * Note that not all binary data can be represented as a String, so this
   * method should only be used for encoded String data.  Use decodeToBytes()
   * otherwise.
   *
   * @param encoded the string to decode
   * @return the decoded form of the encoded string
   */
  public static String decode(String encoded) {
    return new String(Objects.requireNonNull(decodeToBytes(encoded)),
        StandardCharsets.ISO_8859_1);
  }

  /**
   * Returns the decoded form of the given encoded string, as bytes.
   *
   * @param encoded the string to decode
   * @return the decoded form of the encoded string
   */
  public static byte[] decodeToBytes(String encoded) {
    byte[] bytes = null;
      bytes = encoded.getBytes(StandardCharsets.ISO_8859_1);

    // Skip whitespace, stop at the first '=' padding (matching the
    // stream-based behaviour of the classic decoder)
    ByteArrayOutputStream cleaned = new ByteArrayOutputStream(bytes.length);
    boolean padded = false;
    for (byte b : bytes) {
      if (padded) {
        break;
      }
      if (Character.isWhitespace((char) b)) {
        continue;
      }
      if (b == '=') {
        padded = true;
        break;
      }
      cleaned.write(b);
    }
    byte[] cleanedBytes = cleaned.toByteArray();
    if (cleanedBytes.length == 0) {
      return new byte[0];
    }
    int padding = (4 - cleanedBytes.length % 4) % 4;
    byte[] paddedBytes = cleanedBytes;
    if (padding > 0) {
      paddedBytes = new byte[cleanedBytes.length + padding];
      System.arraycopy(cleanedBytes, 0, paddedBytes, 0, cleanedBytes.length);
      for (int i = cleanedBytes.length; i < paddedBytes.length; i++) {
        paddedBytes[i] = '=';
      }
    }
    try {
      return Base64.getDecoder().decode(paddedBytes);
    }
    catch (IllegalArgumentException e) { return null; }
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.err.println("Usage: java Base64Decoder fileToDecode");
      return;
    }

      try (Base64Decoder decoder = new Base64Decoder(
              new BufferedInputStream(
                      new FileInputStream(args[0])))) {
          byte[] buf = new byte[4 * 1024];  // 4K buffer
          int bytesRead;
          while ((bytesRead = decoder.read(buf)) != -1) {
              System.out.write(buf, 0, bytesRead);
          }
      }
  }
}
