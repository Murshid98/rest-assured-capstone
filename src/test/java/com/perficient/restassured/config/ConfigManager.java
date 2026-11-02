package com.perficient.restassured.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Property Reader utility (Assignment 2).
 *
 * <p>Loads the property file of the selected environment from
 * {@code src/test/resources/config/<env>.properties} and exposes the values
 * through simple getters.</p>
 *
 * <p>The environment is selected with the JVM/Maven argument {@code -Denv=<name>},
 * for example {@code mvn test -Denv=uat}. When nothing is passed, {@code qa} is used.
 * Because the environment comes from the command line, you can switch
 * environments WITHOUT changing any code.</p>
 */
public final class ConfigManager {

    /** Environment used when -Denv is not supplied. */
    private static final String DEFAULT_ENV = "qa";

    /** Name of the active environment (qa / uat). */
    private static final String ENV =
            System.getProperty("env", DEFAULT_ENV).trim().toLowerCase();

    /** Holds every key/value pair read from the property file. */
    private static final Properties PROPS = new Properties();

    // Static block: runs once when the class is first used and loads the file.
    static {
        String fileName = "/config/" + ENV + ".properties";
        try (InputStream in = ConfigManager.class.getResourceAsStream(fileName)) {
            if (in == null) {
                throw new IllegalStateException(
                        "Property file not found on classpath: " + fileName
                                + ". Use -Denv=qa or -Denv=uat.");
            }
            PROPS.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read property file: " + fileName, e);
        }
    }

    // Utility class - no objects needed.
    private ConfigManager() {
    }

    /** @return the active environment name, e.g. "qa". */
    public static String getEnv() {
        return ENV;
    }

    /**
     * @return the base URL WITHOUT a trailing slash. Removing the slash avoids a
     * double slash (//) when Rest Assured joins baseUri and basePath.
     */
    public static String getBaseUrl() {
        String url = get("baseUrl").trim();
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    /** @return the API version path, e.g. /public/v2. */
    public static String getBasePath() {
        return get("basePath").trim();
    }

    /** @return the maximum accepted response time in milliseconds. */
    public static long getMaxResponseTimeMs() {
        return Long.parseLong(get("maxResponseTimeMs").trim());
    }

    /**
     * Authentication token lookup (highest priority first):
     * <ol>
     *   <li>JVM argument      : -Dtoken=xxxx</li>
     *   <li>Environment var   : GOREST_TOKEN</li>
     *   <li>Property file     : token=xxxx</li>
     * </ol>
     * This keeps the secret out of source control.
     *
     * @return the token, or an empty string if none is configured.
     */
    public static String getToken() {
        String fromJvm = System.getProperty("token");
        if (fromJvm != null && !fromJvm.trim().isEmpty()) {
            return fromJvm.trim();
        }
        String fromEnv = System.getenv("GOREST_TOKEN");
        if (fromEnv != null && !fromEnv.trim().isEmpty()) {
            return fromEnv.trim();
        }
        return PROPS.getProperty("token", "").trim();
    }

    /** Generic getter that fails fast with a clear message if a key is missing. */
    public static String get(String key) {
        String value = PROPS.getProperty(key);
        if (value == null) {
            throw new IllegalStateException(
                    "Key '" + key + "' is missing in " + ENV + ".properties");
        }
        return value;
    }
}
