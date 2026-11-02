package com.perficient.restassured.reports;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/**
 * Rest Assured filter: every request sent through ApiClient passes through here,
 * so the method, URL, request body, status code, response time and response body
 * are automatically written to the Extent report. Makes failures easy to debug.
 * The Authorization header is intentionally NOT logged (keeps the token secret).
 */
public class ExtentLoggingFilter implements Filter {

    /** Long responses are cut so the report stays readable. */
    private static final int MAX_BODY_LENGTH = 2000;

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {

        // Send the real request and capture the response
        Response response = ctx.next(requestSpec, responseSpec);

        ExtentTest test = ExtentTestManager.getTest();
        if (test != null) {
            test.info("<b>" + requestSpec.getMethod() + "</b> " + requestSpec.getURI());

            Object requestBody = requestSpec.getBody();
            if (requestBody != null) {
                test.info("Request body:");
                test.info(MarkupHelper.createCodeBlock(requestBody.toString(), CodeLanguage.JSON));
            }

            test.info("Status code: <b>" + response.getStatusCode()
                    + "</b> | Response time: <b>" + response.getTime() + " ms</b>");

            String responseBody = response.asString();
            if (responseBody != null && !responseBody.isEmpty()) {
                if (responseBody.length() > MAX_BODY_LENGTH) {
                    responseBody = responseBody.substring(0, MAX_BODY_LENGTH) + " ...(truncated)";
                }
                test.info("Response body:");
                test.info(MarkupHelper.createCodeBlock(responseBody, CodeLanguage.JSON));
            }
        }
        return response;
    }
}
