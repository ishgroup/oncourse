/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.util.selenium.service.authentication;

import ish.oncourse.util.selenium.model.HttpConfiguration;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import ish.oncourse.server.api.v1.model.LoginRequestDTO;

import java.time.Duration;
import java.util.List;

import static ish.oncourse.util.selenium.util.SeleniumUtil.threadWait;

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

        String port = System.getProperty(HttpConfiguration.PORT.getKey());
        String ip = System.getProperty(HttpConfiguration.IP.getKey());
        String basePath = System.getProperty(HttpConfiguration.PATH.getKey());

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));

        logger.error("1. Open " + "https://" + ip + ":" + port + basePath);
        driver.get("https://" + ip + ":" + port + basePath);
        threadWait(Duration.ofSeconds(3));

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

        threadWait(Duration.ofSeconds(3));
        logger.error("7. Click on the `Maybe later` button.");
        wait.until(ExpectedConditions.textToBe(By.cssSelector(".jss13"), "MAYBE LATER"));
        driver.findElement(By.cssSelector(".jss13")).click();

        threadWait(Duration.ofSeconds(1));
        logger.error("8. Сheck if authorization was successful.");
        threadWait(Duration.ofSeconds(1));

        Assertions.assertEquals("Dashboard", driver.getTitle());
        logger.error("9. You log in.");
    }

    @Override
    public void logout() {

        String port = System.getProperty(HttpConfiguration.PORT.getKey());
        String ip = System.getProperty(HttpConfiguration.IP.getKey());
        String basePath = System.getProperty(HttpConfiguration.PATH.getKey());

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        logger.error("1. Open " + "https://" + ip + ":" + port + basePath);
        driver.get("https://" + ip + ":" + port + basePath);

        logger.error("2. Click on the `Logout` button.");
        List<WebElement> buttons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".jss34")));
        if (buttons.size() < 2) {
            Assertions.fail("Button `Logout` doesn't exist");
        } else {
            buttons.get(1).click();
        }

        logger.error("3. Move to the confirm dialog.");
        {
            WebElement element = driver.findElement(By.tagName("body"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element, 0, 0).perform();
        }

        logger.error("4. Click on `Yes` in the confirm dialog.");
        driver.findElement(By.cssSelector(".MuiButton-contained")).click();

        logger.error("5.You log out.");
    }
}
