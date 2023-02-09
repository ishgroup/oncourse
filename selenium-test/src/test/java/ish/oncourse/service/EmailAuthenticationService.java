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
import java.util.List;

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

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        logger.error("1. Open https://127.0.0.1:8182/");
        driver.get("https://127.0.0.1:8182/");

        logger.error("2. Click on the `user` text field.");
        driver.findElement(By.id(":r1:")).click();

        logger.error("3. Set email in the `user` text field.");
        driver.findElement(By.id(":r1:")).sendKeys(requestDTO.getLogin());

        logger.error("4. Click on the `password` text field.");
        driver.findElement(By.id(":r2:")).click();

        logger.error("5. Set password in the `password` text field.");
        driver.findElement(By.id(":r2:")).sendKeys(requestDTO.getPassword());

        logger.error("6. Click on the `Login` button.");
        driver.findElement(By.cssSelector(".jss18")).click();

        try {
            Boolean anotherSessionExist = wait.until(ExpectedConditions.textToBe(By.cssSelector(".jss18"), "KICK OUT"));
            if (anotherSessionExist) {
                logger.error("6.5. Click on the `Kick out` button.");
                driver.findElement(By.cssSelector(".jss18")).click();
            }
        } catch (TimeoutException e) {
            //ignored
        }


        boolean result = wait.until(ExpectedConditions.textToBe(By.cssSelector(".jss13"), "MAYBE LATER")).booleanValue();
        logger.error("9. Click on the `Maybe later` button. Result = " + result);
        driver.findElement(By.xpath("//button[normalize-space()='Maybe Later']")).click();


        logger.error("10. Run script `window.scrollTo(0,0)`.");
        js.executeScript("window.scrollTo(0,0)");
    }

    @Override
    public void logout() throws NoSuchFieldException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        logger.error("1. Open https://127.0.0.1:8182/");
        driver.get("https://127.0.0.1:8182/");

        logger.error("2. Run script `window.scrollTo(0,0)`.");
        js.executeScript("window.scrollTo(0,0)");

        logger.error("4. Click on the `Logout` button.");
        List<WebElement> buttons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".jss34")));
        if (buttons.size() < 2) {
            throw new NoSuchFieldException("Button `Logout` doesn't exist");
        } else {
            buttons.get(1).click();
        }

        logger.error("5. Move to the confirm dialog.");
        {
            WebElement element = driver.findElement(By.tagName("body"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element, 0, 0).perform();
        }

        logger.error("6. Click on `Yes` in the confirm dialog.");
        driver.findElement(By.cssSelector(".MuiButton-contained")).click();
    }
}
