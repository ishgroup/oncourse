/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.service;

import ish.oncourse.server.api.v1.model.LoginRequestDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class EmailAuthenticationService implements AuthenticationService {

    private static final Logger logger = LogManager.getLogger(EmailAuthenticationService.class);

    private final WebDriver driver;

    private final JavascriptExecutor js;

    public EmailAuthenticationService(WebDriver driver, JavascriptExecutor js) {
        this.driver = driver;
        this.js = js;
    }

    @Override
    public void login(LoginRequestDTO requestDTO) {

        logger.error("Test name: Login");
        logger.error("1 | open | /login |");
        driver.get("https://127.0.0.1:8182/login");
        logger.error(" 2 | click | name=user |");
        {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("user")));
        }
        driver.findElement(By.name("user")).click();

        logger.error("3 | type | name=user | admin@example.edu");
        driver.findElement(By.name("user")).sendKeys("admin@example.edu");
        logger.error("4 | click | name=password |");
        driver.findElement(By.name("password")).click();
        logger.error("5 | type | name=password | 11111");
        driver.findElement(By.name("password")).sendKeys("11111");
        logger.error("6 | click | css=.flex-fill:nth-child(3) |");
        driver.findElement(By.cssSelector(".flex-fill:nth-child(3)")).click();
        logger.error("7 | mouseOver | css=.jss18 |");
        {
            WebElement element = driver.findElement(By.cssSelector(".jss18"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
        }
        logger.error("8 | click | css=.jss18 |");
        driver.findElement(By.cssSelector(".jss18")).click();
        logger.error("9 | mouseOut | css=.jss18 |");
        {
            WebElement element = driver.findElement(By.tagName("body"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element, 0, 0).perform();
        }
        logger.error("10 | click | css=.MuiGrid-spacing-xs-3 |");
        driver.findElement(By.cssSelector(".MuiGrid-spacing-xs-3")).click();
        logger.error("11 | mouseOver | css=.jss13 |");
        {
            WebElement element = driver.findElement(By.cssSelector(".jss13"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
        }
        logger.error("12 | click | css=.jss13 |");
        // 12 | click | css=.jss13 |
        driver.findElement(By.cssSelector(".jss13")).click();
        logger.error("13 | runScript | window.scrollTo(0,0) |");
        // 13 | runScript | window.scrollTo(0,0) |
        js.executeScript("window.scrollTo(0,0)");
    }

    @Override
    public void logout() {
        driver.get("https://127.0.0.1:8182/");
        js.executeScript("window.scrollTo(0,0)");
        driver.findElement(By.cssSelector(".jss37")).click();
        {
            WebElement element = driver.findElement(By.cssSelector(".MuiIconButton-sizeMedium:nth-child(2)"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
        }
        {
            WebElement element = driver.findElement(By.tagName("body"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element, 0, 0).perform();
        }
        driver.findElement(By.cssSelector(".MuiIconButton-sizeMedium:nth-child(2) > .MuiSvgIcon-root")).click();
        driver.findElement(By.cssSelector(".MuiButton-contained")).click();
    }
}
