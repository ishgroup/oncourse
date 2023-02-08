/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.service;

import ish.oncourse.server.api.v1.model.LoginRequestDTO;
import ish.oncourse.util.SeleniumExecutor;
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

        logger.error("1. Open https://127.0.0.1:8182/");
        driver.get("https://127.0.0.1:8182/");

        logger.error("2. Click on the `user` text field");
        driver.findElement(By.id(":r1:")).click();

        logger.error("3. Set email in the `user` text field");
        driver.findElement(By.id(":r1:")).sendKeys(requestDTO.getLogin());

        logger.error("4. Click on the `password` text field");
        driver.findElement(By.id(":r2:")).click();

        logger.error("5. Set password in the `password` text field");
        driver.findElement(By.id(":r2:")).sendKeys(requestDTO.getPassword());

        logger.error("6. Click on the `Login` button");
        driver.findElement(By.cssSelector(".jss18")).click();

        logger.error("7. Move out `Login` button");
        {
            WebElement element = driver.findElement(By.tagName("body"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element, 0, 0).perform();
        }

        logger.error("8. Mouse over `Maybe later` button");
        {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".jss13")));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
        }

        logger.error("9. Click on the `Maybe latter` button");
        driver.findElement(By.cssSelector(".jss13")).click();

        logger.error("10. Run script `window.scrollTo(0,0)`");
        js.executeScript("window.scrollTo(0,0)");
    }

    @Override
    public void logout() {
        logger.error("1. Open https://127.0.0.1:8182/");
        driver.get("https://127.0.0.1:8182/");

        logger.error("2. Run script `window.scrollTo(0,0)`");
        js.executeScript("window.scrollTo(0,0)");

        logger.error("3. Move to `Logout` button");
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

        logger.error("4. Click on `Logout` button");
        driver.findElement(By.cssSelector(".MuiIconButton-sizeMedium:nth-child(2) > .MuiSvgIcon-root")).click();

        logger.error("5. Confirm logout at dialog. Click on `Yes` button");
        driver.findElement(By.cssSelector(".MuiButton-contained")).click();
    }
}
