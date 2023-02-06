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

    private static AuthenticationService emailAuthenticationService;

    @BeforeAll
    public void setUp() {
        super.setUp();
        emailAuthenticationService = new EmailAuthenticationService(driver, js);
        var dto = new LoginRequestDTO();
        dto.setLogin("admin@example.edu");
        dto.setPassword("abcd1723");
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        emailAuthenticationService.login(dto);
    }

    @AfterAll
    public void tearDown() {
        emailAuthenticationService.logout();
        super.tearDown();
    }

    @Test
    void test() {
        // 1 | open | https://127.0.0.1:8182/checkout |
        driver.get("https://127.0.0.1:8182/checkout");
        // 3 | click | name=contacts |
        driver.findElement(By.name("contacts")).click();
        // 4 | type | name=contacts | Kristina Ish
        driver.findElement(By.name("contacts")).sendKeys("Kristina Ish");
        // 5 | click | css=span:nth-child(3) > strong |
        {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span:nth-child(3) > strong")));
        }
        driver.findElement(By.cssSelector("span:nth-child(3) > strong")).click();
        // 6 | click | name=items |
        driver.findElement(By.name("items")).click();
        // 7 | type | name=items | dcftc1
        driver.findElement(By.name("items")).sendKeys("dcftc1");
        // 8 | click | css=.MuiListItemText-root |
        driver.findElement(By.cssSelector(".MuiListItemText-root")).click();
        // 9 | mouseOver | css=.mui-1d3bbye > .MuiGrid-grid-xs-1 |
        {

            {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".mui-1d3bbye > .MuiGrid-grid-xs-1")));
            }
            WebElement element = driver.findElement(By.cssSelector(".mui-1d3bbye > .MuiGrid-grid-xs-1"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
        }
        // 10 | mouseOut | css=.mui-1d3bbye > .MuiGrid-grid-xs-1 |
        {
            WebElement element = driver.findElement(By.tagName("body"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element, 0, 0).perform();
        }
        // 11 | click | css=.PrivateSwitchBase-input |
        driver.findElement(By.cssSelector(".PrivateSwitchBase-input")).click();
        // 12 | click | css=.MuiPaper-root:nth-child(3) .MuiSvgIcon-root |
        driver.findElement(By.cssSelector(".MuiPaper-root:nth-child(3) .MuiSvgIcon-root")).click();
        // 13 | waitForElementVisible | name=payment_method | 30000
        {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("payment_method")));
        }
        // 14 | click | name=payment_method |
        driver.findElement(By.name("payment_method")).click();
        // 15 | click | id=:rq:-option-0 |
        {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":rq:-option-0")));
        }
        driver.findElement(By.id(":rq:-option-0")).click();
        // 16 | click | css=.centeredFlex:nth-child(2) .jss311 |
        driver.findElement(By.cssSelector(".centeredFlex:nth-child(2) .jss311")).click();
    }

}
