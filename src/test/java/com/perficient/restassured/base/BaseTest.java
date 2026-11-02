package com.perficient.restassured.base;

import com.perficient.restassured.config.ConfigManager;
import org.testng.annotations.BeforeSuite;

/**
 * Parent class of every test class.
 * Holds set-up that must happen once per run. Test classes extend it with
 * "extends BaseTest".
 */
public class BaseTest {

    /** Runs once before the whole suite: prints the active configuration. */
    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        System.out.println("=====================================================");
        System.out.println(" Environment : " + ConfigManager.getEnv().toUpperCase());
        System.out.println(" Base URL    : " + ConfigManager.getBaseUrl() + ConfigManager.getBasePath());
        System.out.println("=====================================================");

        if (ConfigManager.getToken().isEmpty()) {
            System.out.println("WARNING: no auth token configured. GET tests will work, but "
                    + "POST/PUT/DELETE will fail with 401.\n"
                    + "         Run with: mvn test -Denv=qa -Dtoken=<your_gorest_token>");
        }
    }
}
