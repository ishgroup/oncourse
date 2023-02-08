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
    public void tearDown() {
        emailAuthenticationService.logout();
        super.tearDown();
    }

    @Test
    void test() {

        logger.info("1. Open https://127.0.0.1:8182/checkout");
        driver.get("https://127.0.0.1:8182/checkout");

        logger.info("2. Click on the `contacts` text field");
        driver.findElement(By.id(":r2:")).click();

        logger.info("3. Set value in the `contacts` text field");
        driver.findElement(By.id(":r2:")).sendKeys("Kristina Ish");

        logger.info("4. Choose contact");
        driver.findElement(By.cssSelector("span:nth-child(3) > strong")).click();

        logger.info("5. Click on the `items` text field");
        driver.findElement(By.name("items")).click();

        logger.info("6.Set course code in the `items` text field");
        driver.findElement(By.name("items")).sendKeys("dcftc1");

        logger.info("7. Choose course");
        driver.findElement(By.cssSelector(".MuiListItemText-root")).click();

        logger.info("8. Choose course");
        {
            {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".mui-1d3bbye > .MuiGrid-grid-xs-1")));
            }
            WebElement element = driver.findElement(By.cssSelector(".mui-1d3bbye > .MuiGrid-grid-xs-1"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
        }

        logger.info("9. Move mouse to class list");
        {
            WebElement element = driver.findElement(By.tagName("body"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element, 0, 0).perform();
        }

        logger.info("10. Choose class");
        driver.findElement(By.cssSelector(".PrivateSwitchBase-input")).click();

        logger.info("11. Open Payment  section");
        driver.findElement(By.cssSelector(".MuiPaper-root:nth-child(3) .MuiSvgIcon-root")).click();

        logger.info("12. Choose payment method");
        {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("payment_method")));
        }
        driver.findElement(By.name("payment_method")).click();

        logger.info("13. Select payment method");
        {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":rq:-option-0")));
        }
        driver.findElement(By.id(":rq:-option-0")).click();

        logger.info("Test completed successfully");
    }

}
