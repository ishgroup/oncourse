/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.test.checkout;

import ish.oncourse.server.api.v1.model.LoginRequestDTO;
import ish.oncourse.service.AuthenticationService;
import ish.oncourse.service.EmailAuthenticationService;
import ish.oncourse.test.AbstractSeleniumTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.dbunit.Assertion.assertEquals;


@TestInstance(TestInstance. Lifecycle.PER_CLASS)
public class CheckPaymentMethod extends AbstractSeleniumTest{

    private static final Logger logger = LogManager.getLogger(CheckPaymentMethod.class);
    private static AuthenticationService emailAuthenticationService;

    @BeforeAll
    public void setUp() {
        super.setUp();

        var dto = new LoginRequestDTO();
        dto.setLogin("admin@example.edu");
        dto.setPassword("abcd1723");

        emailAuthenticationService = new EmailAuthenticationService(driver, js);
        emailAuthenticationService.login(dto);
    }

    @AfterAll
    public void tearDown() throws NoSuchFieldException {
        emailAuthenticationService.logout();
        super.tearDown();
    }

    @Test
    void test() throws InterruptedException {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        logger.error("1. Open https://127.0.0.1:8182/checkout");
        driver.get("https://127.0.0.1:8182/checkout");

        logger.error("2. Click on the `contacts` text field");
        driver.findElement(By.id(":r2:")).click();

        logger.error("3. Set value in the `contacts` text field");
        driver.findElement(By.id(":r2:")).sendKeys("Kristina Ish");

        logger.error("4. Choose contact");
        {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span:nth-child(1) > strong")));
        }
        driver.findElement(By.cssSelector("span:nth-child(1) > strong")).click();

        logger.error("5. Click on the `items` text field");
        driver.findElement(By.name("items")).click();

        logger.error("6.Set course code in the `items` text field");
        driver.findElement(By.name("items")).sendKeys("dcftc1");

        logger.error("7. Choose course");
        driver.findElement(By.cssSelector(".MuiListItemText-root")).click();

        logger.error("8. Choose course");
        {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".mui-1d3bbye > .MuiGrid-grid-xs-1")));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
        }

        logger.error("9. Move mouse to class list");
        {
            WebElement element = driver.findElement(By.tagName("body"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element, 0, 0).perform();
        }

        logger.error("10. Choose class");
        driver.findElement(By.cssSelector(".PrivateSwitchBase-input")).click();

        logger.error("11. Open Payment  section");
        driver.findElement(By.cssSelector(".MuiPaper-root:nth-child(3) .MuiSvgIcon-root")).click();

        logger.error("12. Choose payment method");
        wait.until(ExpectedConditions.attributeToBe(By.name("payment_method"), "value", ""));
        wait.until(ExpectedConditions.elementToBeClickable(By.name("payment_method"))).click();

        Thread.sleep(2000);

        logger.error("13. Select payment method");
        WebElement spanTag = driver.findElement(By.xpath("//span[text()='Cash']"));
        WebElement clickableTag = spanTag.findElement(By.xpath("./.."));
        clickableTag.click();

        String result = driver.findElement(By.name("payment_method")).getAttribute("value");
        Assertions.assertEquals("Cash",result);
    }

}
