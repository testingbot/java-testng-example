package com.yourcompany.Tests;

import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.rmi.UnexpectedException;


public class SimpleTest extends TestBase {

    @Test(dataProvider = "hardCodedBrowsers")
    public void test(String browser, String version, String os, Method method) throws Exception {
        this.createDriver(browser, version, os, method.getName());
        WebDriver driver = this.getWebDriver();

        driver.get("https://www.google.com/ncr");
        WebElement element = driver.findElement(By.name("q"));
        element.sendKeys("TestingBot");
        element.submit();
        Thread.sleep(5000);

        Assert.assertEquals("TestingBot - Google Search", driver.getTitle());
    }
}