package com.perficient.restassured.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.perficient.restassured.config.ConfigManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Creates and configures the single ExtentReports object (Assignment 5).
 * The generated file is: target/extent-reports/ExtentReport.html
 * The HTML report contains the execution summary dashboard and the list of
 * passed / failed / skipped tests.
 */
public final class ExtentManager {

    private static final String REPORT_DIR = "target/extent-reports";
    private static final String REPORT_FILE = REPORT_DIR + "/ExtentReport.html";

    private static ExtentReports extent;

    private ExtentManager() {
    }

    /** Lazily creates the report the first time it is needed (singleton). */
    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            try {
                Files.createDirectories(Paths.get(REPORT_DIR));
            } catch (IOException e) {
                throw new IllegalStateException("Cannot create report folder " + REPORT_DIR, e);
            }

            ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_FILE);
            spark.config().setDocumentTitle("Rest Assured Capstone - Execution Report");
            spark.config().setReportName("GoRest API Automation");
            spark.config().setTheme(Theme.STANDARD);

            extent = new ExtentReports();
            extent.attachReporter(spark);

            // Shown in the "Environment" section of the report
            extent.setSystemInfo("Environment", ConfigManager.getEnv().toUpperCase());
            extent.setSystemInfo("Base URL", ConfigManager.getBaseUrl() + ConfigManager.getBasePath());
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("User", System.getProperty("user.name"));
        }
        return extent;
    }

    /** Writes everything collected so far into the HTML file. */
    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}
