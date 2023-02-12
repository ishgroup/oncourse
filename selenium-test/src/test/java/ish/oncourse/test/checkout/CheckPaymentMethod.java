/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.test.checkout;

import com.google.common.collect.ImmutableMap;
import ish.oncourse.server.api.v1.model.LoginRequestDTO;
import ish.oncourse.service.AuthenticationService;
import ish.oncourse.service.EmailAuthenticationService;
import ish.oncourse.test.AbstractSeleniumTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.Command;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v109.network.Network;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CheckPaymentMethod extends AbstractSeleniumTest {

    private static final Logger logger = LogManager.getLogger(CheckPaymentMethod.class);
    private static AuthenticationService emailAuthenticationService;

    @BeforeAll
    public void setUp() throws InterruptedException {
        super.setUp();

        var dto = new LoginRequestDTO();
        dto.setLogin("admin@example.edu");
        dto.setPassword("abcd1723");

        DevTools devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        devTools.addListener(Network.responseReceived(), l -> {
            logger.error("Response URL: ");
            logger.error(l.getResponse().getUrl());
            logger.error(l.getResponse().getStatus());
        });
        devTools.addListener(Network.requestWillBeSent(), l -> {
            logger.error("Request URL: ");
            logger.error(l.getRequest().getUrl());
            logger.error(l.getRequest().getMethod());
        });

        emailAuthenticationService = new EmailAuthenticationService(driver, js);
        emailAuthenticationService.login(dto);


    }

    @AfterAll
    public void tearDown() throws NoSuchFieldException {
        emailAuthenticationService.logout();
        super.tearDown();
    }

    @Test
    void test() throws InterruptedException, IOException {

        try {

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

            logger.error("1. Open https://127.0.0.1:8182/checkout");
            driver.get("https://127.0.0.1:8182/checkout");

            logger.error("4. Create new contact");
            {

                Thread.sleep(2000);
                driver.findElement(By.name("contacts")).click();
                driver.findElement(By.name("contacts")).sendKeys("Test");

                Thread.sleep(2000);
                driver.findElement(By.cssSelector(".MuiListItemText-root")).click();

                Thread.sleep(1000);
                driver.findElement(By.name("firstName")).click();
                driver.findElement(By.name("firstName")).sendKeys("CheckPaymentMethod");
                driver.findElement(By.cssSelector(".saveButtonEditView")).click();

                Thread.sleep(1000);
                js.executeScript("window.scrollTo(0,0)");

                Thread.sleep(1000);
                {
                    WebElement element = driver.findElement(By.name("items"));
                    Actions builder = new Actions(driver);
                    builder.moveToElement(element).clickAndHold().perform();
                }
                {
                    WebElement element = driver.findElement(By.cssSelector(".MuiDialog-container"));
                    Actions builder = new Actions(driver);
                    builder.moveToElement(element).release().perform();
                }
                driver.findElement(By.cssSelector("body")).click();
                {
                    WebElement element = driver.findElement(By.cssSelector(".MuiButton-contained"));
                    Actions builder = new Actions(driver);
                    builder.moveToElement(element).perform();
                }
                driver.findElement(By.cssSelector(".MuiButton-contained")).click();
            }

            logger.error("5. Click on the `items` text field");
            driver.findElement(By.name("items")).click();
            logger.error("6.Set course code in the `items` text field");
            driver.findElement(By.name("items")).sendKeys("dcftc1");
            logger.error("7. Choose course");
            driver.findElement(By.cssSelector(".MuiListItemText-root")).click();
            Thread.sleep(1000);
            logger.error("8. Choose course");
            {
                WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".mui-1d3bbye > .MuiGrid-grid-xs-1")));
                Actions builder = new Actions(driver);
                builder.moveToElement(element).perform();
            }
            logger.error("9. Move mouse to class list");
            Thread.sleep(1000);
            {
                WebElement element = driver.findElement(By.tagName("body"));
                Actions builder = new Actions(driver);
                builder.moveToElement(element, 0, 0).perform();
            }

            logger.error("10. Choose class");
            Thread.sleep(1000);
            driver.findElement(By.cssSelector(".PrivateSwitchBase-input")).click();


            js.executeScript("document.evaluate(\"//div[text()='SUMMARY']\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.click()");
            {
                logger.error("CHROME CONSOLE");
                driver.manage().logs().get(LogType.BROWSER).getAll().forEach(it -> {
                    logger.error(it.getMessage());
                });

            }
            {
                TakesScreenshot scrShot = ((TakesScreenshot) driver);
                String data = scrShot.getScreenshotAs(OutputType.BASE64);
                logger.error("------------1 BASE 64 SCREENSHOT DATA------------");
                logger.error(data);
                logger.error("------------");
            }

            logger.error("12. Choose payment method");

            Thread.sleep(1000);
            wait.until(ExpectedConditions.attributeToBe(By.name("payment_method"), "value", ""));
            wait.until(ExpectedConditions.elementToBeClickable(By.name("payment_method"))).click();
            Thread.sleep(2000);

            logger.error("13. Select payment method");
            WebElement spanTag = driver.findElement(By.xpath("//span[text()='Cash']"));
            WebElement clickableTag = spanTag.findElement(By.xpath("./.."));
            clickableTag.click();
            String result = driver.findElement(By.name("payment_method")).getAttribute("value");
            Assertions.assertEquals("Cash", result);
        } catch (Exception e) {
            e.printStackTrace();
            TakesScreenshot scrShot = ((TakesScreenshot) driver);
            String data = scrShot.getScreenshotAs(OutputType.BASE64);
            logger.error("------------BASE 64 SCREENSHOT DATA------------");
            logger.error(data);
            logger.error("------------");
            Assertions.assertEquals("Cash", data);
        }
    }

}
