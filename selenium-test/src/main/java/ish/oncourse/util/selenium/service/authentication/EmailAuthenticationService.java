/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.util.selenium.service.authentication;

import ish.oncourse.server.api.v1.model.LoginRequestDTO;
import ish.oncourse.util.selenium.model.HttpConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class EmailAuthenticationService implements AuthenticationService {

    private static final Logger logger = LogManager.getLogger(EmailAuthenticationService.class);

    private static final By emailTextField = By.xpath("//input[@id=':r1:' or @placeholder='Email']");
    private static final By passwordTextField = By.xpath("//input[@id=':r2:' or @placeholder='Password']");

    private static final By loginButton = By.xpath("//button[contains(@class, 't-loginButton') and text()='Login']");
    private static final By maybeLatterButton = By.xpath("//button[contains(@class, 't-declineButton') and text()='Maybe Later']");
    private static final By kickoutButton = By.xpath("//button[contains(@class, 't-loginButton') and text()='Kick out']");
    private static final By logoutButton = By.xpath("//button[@aria-label='Logout']");
    private static final By confirmLogoutButton = By.xpath("//button[@type='button' and text()='Yes']");

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

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        logger.error("1. Open " + "https://" + ip + ":" + port + basePath);
        driver.get("https://" + ip + ":" + port + basePath);

        logger.error("2. Click on the `user` text field.");
        var email = wait.until(ExpectedConditions.visibilityOfElementLocated(emailTextField));
        email.click();

        logger.error("3. Set email in the `user` text field.");
        email.sendKeys(requestDTO.getLogin());

        logger.error("4. Click on the `password` text field.");
        var password = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordTextField));
        password.click();

        logger.error("5. Set password in the `password` text field.");
        password.sendKeys(requestDTO.getPassword());

        logger.error("6. Click on the `Login` button.");
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginButton)).click();

        try {
            WebElement kickout = wait.until(ExpectedConditions.visibilityOfElementLocated(kickoutButton));
            if (kickout.getText().equalsIgnoreCase("Kick out")) {
                logger.error("6.5. Click on the `Kick out` button.");
                wait.until(ExpectedConditions.elementToBeClickable(kickout));
                kickout.click();
            }
        } catch (TimeoutException e) {}

        logger.error("7. Click on the `Maybe later` button.");
        var maybeLatter = wait.until(ExpectedConditions.visibilityOfElementLocated(maybeLatterButton));
        wait.until(ExpectedConditions.elementToBeClickable(maybeLatter));
        maybeLatter.click();

        logger.error("8. Сheck if authorization was successful.");
        wait.until(ExpectedConditions.titleContains("Dashboard"));

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
        var logout = wait.until(ExpectedConditions.visibilityOfElementLocated(logoutButton));
        wait.until(ExpectedConditions.elementToBeClickable(logout));
        logout.click();

        logger.error("3. Move to the confirm dialog.");
        {
            WebElement element = driver.findElement(By.tagName("body"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element, 0, 0).perform();
        }

        logger.error("4. Click on `Yes` in the confirm dialog.");
        var confirmLogout = wait.until(ExpectedConditions.visibilityOfElementLocated(confirmLogoutButton));
        wait.until(ExpectedConditions.elementToBeClickable(confirmLogout));
        confirmLogout.click();

        logger.error("5.You log out.");
    }
}
