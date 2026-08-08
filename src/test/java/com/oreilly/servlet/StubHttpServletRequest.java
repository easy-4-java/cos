package com.oreilly.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;
import java.util.Collection;

/**
 * Minimal stub of HttpServletRequest for testing purposes.
 */
public class StubHttpServletRequest implements HttpServletRequest {
    @Override public String getAuthType() { return null; }
    @Override public Cookie[] getCookies() { return new Cookie[0]; }
    @Override public long getDateHeader(String name) { return -1; }
    @Override public String getHeader(String name) { return null; }
    @Override public Enumeration<String> getHeaders(String name) { return Collections.emptyEnumeration(); }
    @Override public Enumeration<String> getHeaderNames() { return Collections.emptyEnumeration(); }
    @Override public int getIntHeader(String name) { return -1; }
    @Override public String getMethod() { return "GET"; }
    @Override public String getPathInfo() { return null; }
    @Override public String getPathTranslated() { return null; }
    @Override public String getContextPath() { return ""; }
    @Override public String getQueryString() { return null; }
    @Override public String getRemoteUser() { return null; }
    @Override public boolean isUserInRole(String role) { return false; }
    @Override public Principal getUserPrincipal() { return null; }
    @Override public String getRequestedSessionId() { return null; }
    @Override public String getRequestURI() { return "/test"; }
    @Override public StringBuffer getRequestURL() { return new StringBuffer("http://localhost/test"); }
    @Override public String getServletPath() { return "/test"; }
    @Override public HttpSession getSession(boolean create) { return null; }
    @Override public HttpSession getSession() { return null; }
    @Override public String changeSessionId() { return null; }
    @Override public boolean isRequestedSessionIdValid() { return false; }
    @Override public boolean isRequestedSessionIdFromCookie() { return false; }
    @Override public boolean isRequestedSessionIdFromURL() { return false; }
    @SuppressWarnings("deprecation") @Override public boolean isRequestedSessionIdFromUrl() { return false; }
    @SuppressWarnings("deprecation") @Override public String getRealPath(String path) { return null; }
    @Override public boolean authenticate(HttpServletResponse response) throws IOException { return false; }
    @Override public void login(String username, String password) {}
    @Override public void logout() {}
    @Override public Collection<Part> getParts() throws IOException { return Collections.emptyList(); }
    @Override public Part getPart(String name) throws IOException { return null; }
    @Override public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException { return null; }
    @Override public Object getAttribute(String name) { return null; }
    @Override public Enumeration<String> getAttributeNames() { return Collections.emptyEnumeration(); }
    @Override public String getCharacterEncoding() { return "ISO-8859-1"; }
    @Override public void setCharacterEncoding(String env) throws UnsupportedEncodingException {}
    @Override public int getContentLength() { return -1; }
    @Override public long getContentLengthLong() { return -1; }
    @Override public String getContentType() { return null; }
    @Override public ServletInputStream getInputStream() throws IOException { return null; }
    @Override public String getParameter(String name) { return null; }
    @Override public Enumeration<String> getParameterNames() { return Collections.emptyEnumeration(); }
    @Override public String[] getParameterValues(String name) { return null; }
    @Override public Map<String, String[]> getParameterMap() { return Collections.emptyMap(); }
    @Override public String getProtocol() { return "HTTP/1.1"; }
    @Override public String getScheme() { return "http"; }
    @Override public String getServerName() { return "localhost"; }
    @Override public int getServerPort() { return 80; }
    @Override public BufferedReader getReader() throws IOException { return null; }
    @Override public String getRemoteAddr() { return "127.0.0.1"; }
    @Override public String getRemoteHost() { return "localhost"; }
    @Override public void setAttribute(String name, Object o) {}
    @Override public void removeAttribute(String name) {}
    @Override public Locale getLocale() { return Locale.getDefault(); }
    @Override public Enumeration<Locale> getLocales() { return Collections.enumeration(Collections.singletonList(Locale.getDefault())); }
    @Override public boolean isSecure() { return false; }
    @Override public RequestDispatcher getRequestDispatcher(String path) { return null; }
    @Override public int getRemotePort() { return 12345; }
    @Override public String getLocalName() { return "localhost"; }
    @Override public String getLocalAddr() { return "127.0.0.1"; }
    @Override public int getLocalPort() { return 80; }
    @Override public ServletContext getServletContext() { return null; }
    @Override public AsyncContext startAsync() throws IllegalStateException { return null; }
    @Override public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException { return null; }
    @Override public boolean isAsyncStarted() { return false; }
    @Override public boolean isAsyncSupported() { return false; }
    @Override public AsyncContext getAsyncContext() { return null; }
    @Override public DispatcherType getDispatcherType() { return DispatcherType.REQUEST; }
}
