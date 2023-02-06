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

        // 1 | open | https://127.0.0.1:8182/login |
        logger.error("1 | open | https://127.0.0.1:8182/login");
        driver.get("https://127.0.0.1:8182/login");

        // 2 | setWindowSize | 1335x806 |
        logger.error("2 | setWindowSize | 1335x806");
        driver.manage().window().setSize(new Dimension(1335, 806));

        // 3 | click | id=:r1: |
        logger.error("3 | click | id=:r1:");
        driver.findElement(By.id(":r1:")).click();

        // 4 | type | id=:r1: | admin@example.edu
        logger.error("4 | type | id=:r1: | " + requestDTO.getLogin());
        driver.findElement(By.id(":r1:")).sendKeys(requestDTO.getLogin());

        // 5 | click | id=:r2: |
        logger.error("5 | click | id=:r2:");
        driver.findElement(By.id(":r2:")).click();

        // 6 | mouseOver | css=.jss18 |
        logger.error("6 | mouseOver | css=.jss18");
        {
            WebElement element = driver.findElement(By.cssSelector(".jss18"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
        }
        // 7 | type | id=:r2: | abcd1723
        logger.error(" 7 | type | id=:r2: | " +requestDTO.getPassword());
        driver.findElement(By.id(":r2:")).sendKeys(requestDTO.getPassword());

        // 8 | click | css=.jss18 |
        logger.error("8 | click | css=.jss18");
        driver.findElement(By.cssSelector(".jss18")).click();

        // 9 | mouseOut | css=.jss18 |
        logger.error("9 | mouseOut | css=.jss18");
        {
            WebElement element = driver.findElement(By.tagName("body"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element, 0, 0).perform();
        }

        // 10 | mouseOver | css=.jss13 |
        logger.error("10 | mouseOver | css=.jss13");
        {
            WebElement element = driver.findElement(By.cssSelector(".jss13"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
        }
        // 11 | click | css=.jss13 |
        logger.error("11 | click | css=.jss13");
        driver.findElement(By.cssSelector(".jss13")).click();

        // 12 | runScript | window.scrollTo(0,0) |
        logger.error("12 | runScript | window.scrollTo(0,0)");
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
