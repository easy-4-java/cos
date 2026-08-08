// Copyright (C) 2007 by Jason Hunter <jhunter_AT_acm_DOT_org>.
// All rights reserved.  Use of this class is limited.
// Please see the LICENSE for more information.

package com.oreilly.servlet.multipart;

/**
 * Thrown to indicate an upload exceeded the maximum size.
 *
 * @see com.oreilly.servlet.multipart.MultipartParser
 *
 * @author &lt;b&gt;Jason Hunter&lt;/b&gt;, Copyright &#169; 2007
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @version 1.0, 2007/04/11
 */
public class ExceededSizeException extends RuntimeException {

  /**
   * Constructs a new ExceededSizeException with no detail message.
   */
  public ExceededSizeException() {
    super();
  }

  /**
   * Constructs a new ExceededSizeException with the specified
   * detail message.
   *
   * @param s the detail message
   */
  public ExceededSizeException(String s) {
    super(s);
  }
  
  /**
   * 便于 jfinal 中的同名类 com.jfinal.upload.ExceededSizeException 继承
   * 让用户代码中的 try catch 同时支持两种 ExceededSizeException 类型
   */
  public ExceededSizeException(Throwable t) {
	  super(t);
  }
}
