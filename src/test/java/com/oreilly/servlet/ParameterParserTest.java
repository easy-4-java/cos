package com.oreilly.servlet;

import javax.servlet.ServletRequest;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class ParameterParserTest {

    private ServletRequest createMockRequest(Map<String, String[]> params) {
        return new StubServletRequest() {
            @Override
            public String getParameter(String name) {
                String[] vals = params.get(name);
                return vals != null && vals.length > 0 ? vals[0] : null;
            }

            @Override
            public String[] getParameterValues(String name) {
                return params.get(name);
            }

            @Override
            public Enumeration<String> getParameterNames() {
                return Collections.enumeration(params.keySet());
            }
        };
    }

    @Test
    public void shouldGetStringParameter() throws ParameterNotFoundException {
        Map<String, String[]> params = new HashMap<>();
        params.put("name", new String[]{"value"});
        ParameterParser parser = new ParameterParser(createMockRequest(params));
        assertEquals("value", parser.getStringParameter("name"));
    }

    @Test(expected = ParameterNotFoundException.class)
    public void shouldThrowWhenParameterNotFound() throws ParameterNotFoundException {
        ParameterParser parser = new ParameterParser(createMockRequest(new HashMap<>()));
        parser.getStringParameter("missing");
    }

    @Test(expected = ParameterNotFoundException.class)
    public void shouldThrowWhenParameterEmpty() throws ParameterNotFoundException {
        Map<String, String[]> params = new HashMap<>();
        params.put("empty", new String[]{""});
        ParameterParser parser = new ParameterParser(createMockRequest(params));
        parser.getStringParameter("empty");
    }

    @Test
    public void shouldReturnDefaultStringWhenNotFound() {
        ParameterParser parser = new ParameterParser(createMockRequest(new HashMap<>()));
        assertEquals("default", parser.getStringParameter("missing", "default"));
    }

    @Test
    public void shouldGetBooleanParameterTrue() throws ParameterNotFoundException {
        Map<String, String[]> params = new HashMap<>();
        params.put("flag", new String[]{"true"});
        ParameterParser parser = new ParameterParser(createMockRequest(params));
        assertTrue(parser.getBooleanParameter("flag"));
    }

    @Test
    public void shouldGetBooleanParameterOn() throws ParameterNotFoundException {
        Map<String, String[]> params = new HashMap<>();
        params.put("flag", new String[]{"on"});
        ParameterParser parser = new ParameterParser(createMockRequest(params));
        assertTrue(parser.getBooleanParameter("flag"));
    }

    @Test
    public void shouldGetBooleanParameterYes() throws ParameterNotFoundException {
        Map<String, String[]> params = new HashMap<>();
        params.put("flag", new String[]{"yes"});
        ParameterParser parser = new ParameterParser(createMockRequest(params));
        assertTrue(parser.getBooleanParameter("flag"));
    }

    @Test
    public void shouldGetBooleanParameterFalse() throws ParameterNotFoundException {
        Map<String, String[]> params = new HashMap<>();
        params.put("flag", new String[]{"false"});
        ParameterParser parser = new ParameterParser(createMockRequest(params));
        assertFalse(parser.getBooleanParameter("flag"));
    }

    @Test
    public void shouldGetBooleanParameterOff() throws ParameterNotFoundException {
        Map<String, String[]> params = new HashMap<>();
        params.put("flag", new String[]{"off"});
        ParameterParser parser = new ParameterParser(createMockRequest(params));
        assertFalse(parser.getBooleanParameter("flag"));
    }

    @Test
    public void shouldGetBooleanParameterNo() throws ParameterNotFoundException {
        Map<String, String[]> params = new HashMap<>();
        params.put("flag", new String[]{"no"});
        ParameterParser parser = new ParameterParser(createMockRequest(params));
        assertFalse(parser.getBooleanParameter("flag"));
    }

    @Test(expected = NumberFormatException.class)
    public void shouldThrowForInvalidBoolean() throws ParameterNotFoundException {
        Map<String, String[]> params = new HashMap<>();
        params.put("flag", new String[]{"invalid"});
        ParameterParser parser = new ParameterParser(createMockRequest(params));
        parser.getBooleanParameter("flag");
    }

    @Test
    public void shouldReturnDefaultBooleanWhenNotFound() {
        ParameterParser parser = new ParameterParser(createMockRequest(new HashMap<>()));
        assertTrue(parser.getBooleanParameter("missing", true));
    }

    @Test
    public void shouldGetByteParameter() throws ParameterNotFoundException {
        Map<String, String[]> params = new HashMap<>();
        params.put("val", new String[]{"42"});
        ParameterParser parser = new ParameterParser(createMockRequest(params));
        assertEquals(42, parser.getByteParameter("val"));
    }

    @Test
    public void shouldReturnDefaultByteWhenNotFound() {
        ParameterParser parser = new ParameterParser(createMockRequest(new HashMap<>()));
        assertEquals(10, parser.getByteParameter("missing", (byte) 10));
    }

    @Test
    public void shouldGetCharParameter() throws ParameterNotFoundException {
        Map<String, String[]> params = new HashMap<>();
        params.put("ch", new String[]{"X"});
        ParameterParser parser = new ParameterParser(createMockRequest(params));
        assertEquals('X', parser.getCharParameter("ch"));
    }

    @Test(expected = ParameterNotFoundException.class)
    public void shouldThrowForEmptyCharParameter() throws ParameterNotFoundException {
        Map<String, String[]> params = new HashMap<>();
        params.put("ch", new String[]{""});
        ParameterParser parser = new ParameterParser(createMockRequest(params));
        parser.getCharParameter("ch");
    }

    @Test
    public void shouldReturnDefaultCharWhenNotFound() {
        ParameterParser parser = new ParameterParser(createMockRequest(new HashMap<>()));
        assertEquals('Z', parser.getCharParameter("missing", 'Z'));
    }

    @Test
    public void shouldGetDoubleParameter() throws ParameterNotFoundException {
        Map<String, String[]> params = new HashMap<>();
        params.put("val", new String[]{"3.14"});
        ParameterParser parser = new ParameterParser(createMockRequest(params));
        assertEquals(3.14, parser.getDoubleParameter("val"), 0.001);
    }

    @Test
    public void shouldReturnDefaultDoubleWhenNotFound() {
        ParameterParser parser = new ParameterParser(createMockRequest(new HashMap<>()));
        assertEquals(1.0, parser.getDoubleParameter("missing", 1.0), 0.001);
    }

    @Test
    public void shouldGetFloatParameter() throws ParameterNotFoundException {
        Map<String, String[]> params = new HashMap<>();
        params.put("val", new String[]{"2.5"});
        ParameterParser parser = new ParameterParser(createMockRequest(params));
        assertEquals(2.5f, parser.getFloatParameter("val"), 0.001f);
    }

    @Test
    public void shouldReturnDefaultFloatWhenNotFound() {
        ParameterParser parser = new ParameterParser(createMockRequest(new HashMap<>()));
        assertEquals(1.0f, parser.getFloatParameter("missing", 1.0f), 0.001f);
    }

    @Test
    public void shouldGetIntParameter() throws ParameterNotFoundException {
        Map<String, String[]> params = new HashMap<>();
        params.put("val", new String[]{"100"});
        ParameterParser parser = new ParameterParser(createMockRequest(params));
        assertEquals(100, parser.getIntParameter("val"));
    }

    @Test
    public void shouldReturnDefaultIntWhenNotFound() {
        ParameterParser parser = new ParameterParser(createMockRequest(new HashMap<>()));
        assertEquals(99, parser.getIntParameter("missing", 99));
    }

    @Test
    public void shouldGetLongParameter() throws ParameterNotFoundException {
        Map<String, String[]> params = new HashMap<>();
        params.put("val", new String[]{"123456789"});
        ParameterParser parser = new ParameterParser(createMockRequest(params));
        assertEquals(123456789L, parser.getLongParameter("val"));
    }

    @Test
    public void shouldReturnDefaultLongWhenNotFound() {
        ParameterParser parser = new ParameterParser(createMockRequest(new HashMap<>()));
        assertEquals(42L, parser.getLongParameter("missing", 42L));
    }

    @Test
    public void shouldGetShortParameter() throws ParameterNotFoundException {
        Map<String, String[]> params = new HashMap<>();
        params.put("val", new String[]{"123"});
        ParameterParser parser = new ParameterParser(createMockRequest(params));
        assertEquals(123, parser.getShortParameter("val"));
    }

    @Test
    public void shouldReturnDefaultShortWhenNotFound() {
        ParameterParser parser = new ParameterParser(createMockRequest(new HashMap<>()));
        assertEquals(10, parser.getShortParameter("missing", (short) 10));
    }

    @Test
    public void shouldGetMissingParameters() {
        Map<String, String[]> params = new HashMap<>();
        params.put("fname", new String[]{"John"});
        ParameterParser parser = new ParameterParser(createMockRequest(params));
        String[] required = {"fname", "lname", "account"};
        String[] missing = parser.getMissingParameters(required);
        assertNotNull(missing);
        assertEquals(2, missing.length);
    }

    @Test
    public void shouldReturnNullWhenAllParametersPresent() {
        Map<String, String[]> params = new HashMap<>();
        params.put("fname", new String[]{"John"});
        params.put("lname", new String[]{"Doe"});
        ParameterParser parser = new ParameterParser(createMockRequest(params));
        String[] required = {"fname", "lname"};
        assertNull(parser.getMissingParameters(required));
    }

    @Test
    public void shouldSetCharacterEncoding() throws Exception {
        Map<String, String[]> params = new HashMap<>();
        params.put("name", new String[]{"value"});
        ParameterParser parser = new ParameterParser(createMockRequest(params));
        parser.setCharacterEncoding("UTF-8");
        assertEquals("value", parser.getStringParameter("name"));
    }

    @Test(expected = java.io.UnsupportedEncodingException.class)
    public void shouldThrowForInvalidEncoding() throws Exception {
        ParameterParser parser = new ParameterParser(createMockRequest(new HashMap<>()));
        parser.setCharacterEncoding("INVALID-ENCODING-XYZ");
    }
}
