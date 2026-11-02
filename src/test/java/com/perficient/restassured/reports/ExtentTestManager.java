package com.perficient.restassured.reports;

import com.aventstack.extentreports.ExtentTest;

/**
 * Keeps track of the {@link ExtentTest} that belongs to the test method that is
 * running right now. A ThreadLocal makes it safe even if tests run in parallel.
 * Lets any class (e.g. the request logger) write into the current test's report.
 */
public final class ExtentTestManager {

    private static final ThreadLocal<ExtentTest> CURRENT = new ThreadLocal<>();

    private ExtentTestManager() {
    }

    public static void setTest(ExtentTest test) {
        CURRENT.set(test);
    }

    /** @return the current test node, or null if no test is running. */
    public static ExtentTest getTest() {
        return CURRENT.get();
    }

    public static void remove() {
        CURRENT.remove();
    }
}
