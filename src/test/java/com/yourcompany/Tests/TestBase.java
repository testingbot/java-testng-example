package com.yourcompany.Tests;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;import java.util.HashMap;
import java.rmi.UnexpectedException;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple TestNG test which demonstrates being instantiated via a DataProvider in order to supply multiple browser combinations.
 *
 * @author Neil Manvar
 */
public class TestBase  {

    public String buildTag = System.getenv("TB_BUILD_NAME");

    public String key = System.getenv("TESTINGBOT_KEY");

    public String secret = System.getenv("TESTINGBOT_SECRET");

    /**
     * ThreadLocal variable which contains the  {@link WebDriver} instance which is used to perform browser interactions with.
     */
    private ThreadLocal<WebDriver> webDriver = new ThreadLocal<WebDriver>();

    /**
     * ThreadLocal variable which contains the WebDriver SessionId.
     */
    private ThreadLocal<String> sessionId = new ThreadLocal<String>();

    /**
     * DataProvider that explicitly sets the browser combinations to be used.
     *
     * @param testMethod
     * @return Two dimensional array of objects with browser, version, and platform information
     */
    @DataProvider(name = "hardCodedBrowsers", parallel = true)
    public static Object[][] testingbotBrowserDataProvider(Method testMethod) {
        return new Object[][]{
                new Object[]{"MicrosoftEdge", "latest", "Windows 10"},
                new Object[]{"chrome", "latest", "VENTURA"},
                new Object[]{"firefox", "latest-1", "Windows 11"},
        };
    }

    /**
     * @return the {@link WebDriver} for the current thread
     */
    public WebDriver getWebDriver() {
        return webDriver.get();
    }

    /**
     *
     * @return the SessionId for the current thread
     */
    public String getSessionId() {
        return sessionId.get();
    }

    /**
     * Constructs a new {@link RemoteWebDriver} instance which is configured to use the capabilities defined by the browser,
     * version and os parameters, and which is configured to run against hub.testingbot.com, using
     * the key and secret populated by the {@link #authentication} instance.
     *
     * @param browser Represents the browser to be used as part of the test run.
     * @param version Represents the version of the browser to be used as part of the test run.
     * @param os Represents the operating system to be used as part of the test run.
     * @param testName Represents the name of the test case that will be used to identify the test on TestingBot
     * @return
     * @throws MalformedURLException if an error occurs parsing the url
     */
    protected void createDriver(String browser, String version, String os, String testName)
            throws MalformedURLException, UnexpectedException {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability(CapabilityType.BROWSER_NAME, browser);
        capabilities.setCapability(CapabilityType.BROWSER_VERSION, version);
        capabilities.setCapability(CapabilityType.PLATFORM_NAME, os);

        Map<String, Object> testingBotOptions = new HashMap<>();
        testingBotOptions.put("name", testName);
        if (buildTag != null) {
            testingBotOptions.put("build", buildTag);
        }
        capabilities.setCapability("tb:options", testingBotOptions);

        webDriver.set(new RemoteWebDriver(
                new URL("https://" + key + ":" + secret + "@hub.testingbot.com/wd/hub"),
                capabilities));

        // set current sessionId
        String id = ((RemoteWebDriver) getWebDriver()).getSessionId().toString();
        sessionId.set(id);
    }

    /**
     * Method that gets invoked after test.
     * Dumps browser log and closes the browser
     */
    @AfterMethod
    public void tearDown(ITestResult result) throws Exception {
        if (webDriver.get() != null) {
            ((JavascriptExecutor) webDriver.get()).executeScript("tb:test-result=" + (result.isSuccess() ? "passed" : "failed"));
            webDriver.get().quit();
        }
    }

    protected void annotate(String text) {
        if (webDriver.get() != null) {
            ((JavascriptExecutor) webDriver.get()).executeScript("tb:test-context=" + text);
        }
    }
}
