/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.selenium.test.checkout;

import ish.oncourse.selenium.test.AbstractSeleniumTest;
import ish.oncourse.selenium.extension.PrintPageScreenshot;
import ish.oncourse.server.api.v1.model.LoginRequestDTO;
import ish.oncourse.util.selenium.model.HttpConfiguration;
import ish.oncourse.util.selenium.service.authentication.AuthenticationService;
import ish.oncourse.util.selenium.service.authentication.EmailAuthenticationService;
import ish.oncourse.util.selenium.service.extension.ConfigurationResolver;
import ish.oncourse.selenium.extension.PrintBrowserConsole;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static ish.oncourse.util.selenium.util.SeleniumUtil.threadWait;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith({ConfigurationResolver.class, PrintBrowserConsole.class, PrintPageScreenshot.class})
public class CheckPaymentMethodTest extends AbstractSeleniumTest {

    private static final Logger logger = LogManager.getLogger(CheckPaymentMethodTest.class);
    private static AuthenticationService emailAuthenticationService;

    @BeforeAll
    public void setUp(WebDriver webDriver) {
        super.setUp(webDriver);
        var dto = new LoginRequestDTO();
        dto.setLogin("admin@example.edu");
        dto.setPassword("abcd1723");

        emailAuthenticationService = new EmailAuthenticationService(this.driver, js);
        emailAuthenticationService.login(dto);
    }

    @AfterAll
    public void tearDown() {
        emailAuthenticationService.logout();
        super.tearDown();
    }

    @Test
    void test() {

        String port = System.getProperty(HttpConfiguration.PORT.getKey());
        String ip = System.getProperty(HttpConfiguration.IP.getKey());

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        logger.error("1. Open " + "https://" + ip + ":" + port + "/checkout");
        driver.get("https://" + ip + ":" + port + "/checkout");

        logger.error("4. Create new contact.");
        {

            threadWait(Duration.ofSeconds(2));
            driver.findElement(By.name("contacts")).click();
            driver.findElement(By.name("contacts")).sendKeys("Test");

            threadWait(Duration.ofSeconds(2));
            driver.findElement(By.cssSelector(".MuiListItemText-root")).click();

            threadWait(Duration.ofSeconds(1));
            driver.findElement(By.name("firstName")).click();
            driver.findElement(By.name("firstName")).sendKeys("CheckPaymentMethod");
            driver.findElement(By.cssSelector(".saveButtonEditView")).click();

            threadWait(Duration.ofSeconds(1));
            js.executeScript("window.scrollTo(0,0)");

            threadWait(Duration.ofSeconds(1));
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

        logger.error("5. Click on the `items` text field.");
        driver.findElement(By.name("items")).click();

        logger.error("6.Set course code in the `items` text field.");
        driver.findElement(By.name("items")).sendKeys("dcftc1");

        logger.error("7. Choose course.");
        driver.findElement(By.cssSelector(".MuiListItemText-root")).click();

        threadWait(Duration.ofSeconds(1));
        logger.error("8. Choose course.");
        {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".mui-1d3bbye > .MuiGrid-grid-xs-1")));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
        }

        logger.error("9. Move mouse to class list.");
        threadWait(Duration.ofSeconds(1));
        {
            WebElement element = driver.findElement(By.tagName("body"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element, 0, 0).perform();
        }

        logger.error("10. Choose class.");
        threadWait(Duration.ofSeconds(1));
        driver.findElement(By.cssSelector(".PrivateSwitchBase-input")).click();

        logger.error("11. Choose Summary.");
        threadWait(Duration.ofSeconds(1));
        js.executeScript("document.evaluate(\"//div[text()='Summary']\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.click()");
        logger.error("12. Choose Payment.");
        threadWait(Duration.ofSeconds(1));
        js.executeScript("document.evaluate(\"//div[text()='Payment']\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.click()");

        logger.error("13. Choose payment method.");

        threadWait(Duration.ofSeconds(1));
        wait.until(ExpectedConditions.attributeToBe(By.name("payment_method"), "value", ""));
        wait.until(ExpectedConditions.elementToBeClickable(By.name("payment_method"))).click();
        threadWait(Duration.ofSeconds(2));

        logger.error("14. Select payment method.");
        WebElement spanTag = driver.findElement(By.xpath("//span[text()='Cash']"));
        WebElement clickableTag = spanTag.findElement(By.xpath("./.."));
        clickableTag.click();
        String result = driver.findElement(By.name("payment_method")).getAttribute("value");

        Assertions.assertEquals("Cash", result);

        logger.error("15. Test completed successfully.");
    }

}
