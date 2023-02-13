/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.service.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogType;

public class NoSuchElementExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger logger = LogManager.getLogger(NoSuchElementExceptionHandler.class);

    private WebDriver webDriver;

    public NoSuchElementExceptionHandler(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (e instanceof  NoSuchElementException) {
            TakesScreenshot scrShot = ((TakesScreenshot) webDriver);
            String data = scrShot.getScreenshotAs(OutputType.BASE64);

            logger.error("---------" + "console" + "---------");
            webDriver.manage().logs().get(LogType.BROWSER).getAll().forEach(it -> {
                logger.error(it.getMessage());
            });
            logger.error("---------end---------");

            logger.error("---------" + webDriver.getTitle() + " page screenshot in base64" + "---------");
            logger.error(data);
            logger.error("---------end---------");

//            Assertions.fail();
        }
        e.printStackTrace();
    }
}
