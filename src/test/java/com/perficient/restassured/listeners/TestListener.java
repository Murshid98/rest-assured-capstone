package com.perficient.restassured.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.perficient.restassured.reports.ExtentManager;
import com.perficient.restassured.reports.ExtentTestManager;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener that connects test execution to the Extent report.
 * TestNG calls these methods automatically (registered in testng.xml), so the
 * test classes themselves contain NO reporting code.
 */
public class TestListener implements ITestListener {

    /** Called before each &lt;test&gt; block of testng.xml starts. */
    @Override
    public void onStart(ITestContext context) {
        ExtentManager.getInstance(); // make sure the report is created
    }

    /** Called right before a test method runs: create its entry in the report. */
    @Override
    public void onTestStart(ITestResult result) {
        ExtentTestManager.setTest(createTestEntry(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTestManager.getTest().pass("Test PASSED");
        ExtentTestManager.remove();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        // Records the assertion message / exception stack trace in the report
        ExtentTestManager.getTest().fail(result.getThrowable());
        ExtentTestManager.remove();
    }

    /** Called when a test is skipped, e.g. because a test it depends on failed. */
    @Override
    public void onTestSkipped(ITestResult result) {
        // A test skipped due to dependsOnMethods never "started", so create its entry now
        if (ExtentTestManager.getTest() == null) {
            ExtentTestManager.setTest(createTestEntry(result));
        }
        ExtentTestManager.getTest().skip(
                result.getThrowable() != null ? result.getThrowable().getMessage() : "Test SKIPPED");
        ExtentTestManager.remove();
    }

    /** Called after each &lt;test&gt; block finishes: write the HTML file. */
    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flush();
    }

    /** Builds a report entry named after the method and grouped by test class. */
    private ExtentTest createTestEntry(ITestResult result) {
        String description = result.getMethod().getDescription();
        return ExtentManager.getInstance()
                .createTest(result.getMethod().getMethodName(), description == null ? "" : description)
                .assignCategory(result.getTestClass().getRealClass().getSimpleName());
    }
}
