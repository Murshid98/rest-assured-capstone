package com.perficient.restassured.utils;

import com.perficient.restassured.config.ConfigManager;
import io.restassured.response.Response;
import org.testng.Assert;

/**
 * Reusable validations shared by all tests (status code and response time),
 * so the same checks are not copy-pasted in every test.
 * A failed check throws an AssertionError that the listener writes to the report.
 */
public final class ResponseValidator {

    private ResponseValidator() {
    }

    /** Verifies the HTTP status code; the response body is added to the message to help debugging. */
    public static void assertStatusCode(Response response, int expectedStatus) {
        Assert.assertEquals(response.getStatusCode(), expectedStatus,
                "Unexpected status code. Response body: " + response.asString());
    }

    /** Verifies the response arrived within the limit set by maxResponseTimeMs in the property file. */
    public static void assertResponseTime(Response response) {
        long maxMs = ConfigManager.getMaxResponseTimeMs();
        long actualMs = response.getTime();
        Assert.assertTrue(actualMs <= maxMs,
                "Response too slow: " + actualMs + " ms (allowed: " + maxMs + " ms)");
    }
}
