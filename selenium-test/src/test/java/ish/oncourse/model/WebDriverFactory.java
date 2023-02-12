/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.model;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.managers.ChromeDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;

public class WebDriverFactory {

    private static final String IGNORE_CERTIFICATE = "--ignore-certificate-errors";

    public static WebDriver get(Browser browser) {
        switch (browser) {
            case CHROME:
                WebDriverManager.chromedriver().setup();
                return new ChromeDriver(new ChromeOptions().addArguments(
                       "--headless",
                        "--disable-gpu",
                        "--window-size=1920,1200",
                        "--ignore-certificate-errors",
//                        "--disable-extensions",
                        "--no-sandbox",
                        "--disable-dev-shm-usage"));
            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                return new FirefoxDriver(new FirefoxOptions().addArguments(IGNORE_CERTIFICATE));
            case SAFARI:
                WebDriverManager.safaridriver().setup();
                return new SafariDriver(); // metod addArgument not impleleted -_____-
        }
        return null;
    }
}
