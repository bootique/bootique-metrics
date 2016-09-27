package io.bootique.metrics.web;

import io.bootique.metrics.healthcheck.HealthCheckOutcome;
import io.bootique.metrics.healthcheck.HealthCheckRegistry;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HealthCheckServletTest {

    private HealthCheckRegistry mockRegistry;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;

    @Before
    public void before() {
        mockRegistry = mock(HealthCheckRegistry.class);
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
    }

    @Test
    public void testDoGet_NoHealthChecks() throws ServletException, IOException {

        when(mockRegistry.runHealthChecks()).thenReturn(new TreeMap<>());

        StringWriter writer = new StringWriter();
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(writer));

        HealthCheckServlet servlet = new HealthCheckServlet(mockRegistry);

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setStatus(501);
        assertEquals("! No health checks registered.\n", writer.toString());
    }

    @Test
    public void testDoGet_Success() throws ServletException, IOException {

        SortedMap<String, HealthCheckOutcome> testResults = new TreeMap<>();
        testResults.put("h1", HealthCheckOutcome.healthy());
        testResults.put("h2", HealthCheckOutcome.healthy("I am healthy"));

        when(mockRegistry.runHealthChecks()).thenReturn(testResults);

        StringWriter writer = new StringWriter();
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(writer));

        HealthCheckServlet servlet = new HealthCheckServlet(mockRegistry);

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setStatus(200);
        assertEquals("* h1: OK\n"
                + "* h2: OK - I am healthy\n", writer.toString());
    }

    @Test
    public void testDoGet_Mixed() throws ServletException, IOException {

        SortedMap<String, HealthCheckOutcome> testResults = new TreeMap<>();
        testResults.put("h1", HealthCheckOutcome.healthy());
        testResults.put("h2", HealthCheckOutcome.healthy("I am healthy"));
        testResults.put("h3", HealthCheckOutcome.unhealthy("I am not healthy"));

        when(mockRegistry.runHealthChecks()).thenReturn(testResults);

        StringWriter writer = new StringWriter();
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(writer));

        HealthCheckServlet servlet = new HealthCheckServlet(mockRegistry);

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setStatus(500);
        assertEquals("* h1: OK\n"
                + "* h2: OK - I am healthy\n"
                + "! h3: ERROR - I am not healthy\n", writer.toString());
    }

    @Test
    public void testDoGet_StackTrace() throws ServletException, IOException {

        SortedMap<String, HealthCheckOutcome> testResults = new TreeMap<>();
        try {
            throw new RuntimeException("Test exception");
        } catch (RuntimeException e) {
            testResults.put("h4", HealthCheckOutcome.unhealthy(e));
        }

        when(mockRegistry.runHealthChecks()).thenReturn(testResults);

        StringWriter writer = new StringWriter();
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(writer));

        HealthCheckServlet servlet = new HealthCheckServlet(mockRegistry);

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setStatus(500);
        assertTrue(writer.toString().startsWith("! h4: ERROR - Test exception\n" +
                "\n" +
                "java.lang.RuntimeException: Test exception\n"));
    }
}
