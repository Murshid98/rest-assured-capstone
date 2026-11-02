package com.perficient.restassured.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perficient.restassured.config.ConfigManager;
import com.perficient.restassured.reports.ExtentLoggingFilter;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Reusable HTTP helper ("common methods" utility).
 *
 * <p>Every request in the framework goes through this class, so the following is
 * configured in ONE place: base URL (from the property file), JSON headers,
 * authentication and report logging.</p>
 *
 * <p><b>Authentication technique used:</b> Bearer token. GoRest requires the header
 * {@code Authorization: Bearer <token>} for create / update / delete calls.</p>
 */
public final class ApiClient {

    /** Converts POJOs to JSON text. */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ApiClient() {
    }

    /**
     * Builds the common request settings (a "request specification").
     * Created fresh for every call so a changed token/environment is always honoured.
     */
    private static RequestSpecification spec() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(ConfigManager.getBaseUrl())    // e.g. https://gorest.co.in
                .setBasePath(ConfigManager.getBasePath())  // e.g. /public/v2
                .setContentType(ContentType.JSON)          // we send JSON
                .setAccept(ContentType.JSON)               // we want JSON back
                .addFilter(new ExtentLoggingFilter());     // log into the HTML report

        // Authentication: attach the bearer token when one is configured
        String token = ConfigManager.getToken();
        if (!token.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + token);
        }
        return builder.build();
    }

    /** Converts any POJO to a JSON string (so the exact body can be logged). */
    private static String toJson(Object body) {
        try {
            return MAPPER.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Could not convert request body to JSON", e);
        }
    }

    /** GET request. pathParams fill the {placeholders} of the path in order. */
    public static Response get(String path, Object... pathParams) {
        return given().spec(spec()).when().get(path, pathParams);
    }

    /** GET request with query parameters, e.g. /posts?title=hello. */
    public static Response getWithQuery(String path, Map<String, ?> queryParams) {
        return given().spec(spec()).queryParams(queryParams).when().get(path);
    }

    /** POST request. The body is a POJO that is converted to JSON. */
    public static Response post(String path, Object body, Object... pathParams) {
        return given().spec(spec()).body(toJson(body)).when().post(path, pathParams);
    }

    /** PUT request (full update). The body is a POJO that is converted to JSON. */
    public static Response put(String path, Object body, Object... pathParams) {
        return given().spec(spec()).body(toJson(body)).when().put(path, pathParams);
    }

    /** DELETE request. */
    public static Response delete(String path, Object... pathParams) {
        return given().spec(spec()).when().delete(path, pathParams);
    }
}
