package com.yourcompany.Tests;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Method;


public class SimpleTest extends TestBase {

    @Test(dataProvider = "hardCodedBrowsers")
    public void test(String browser, String version, String os, Method method) throws Exception {
        this.createDriver(browser, version, os, method.getName());
        WebDriver driver = this.getWebDriver();

        driver.get("https://testingbot.com");
        Assert.assertEquals("TestingBot: Cross Browser Testing and Mobile App Testing", driver.getTitle());
    }
}