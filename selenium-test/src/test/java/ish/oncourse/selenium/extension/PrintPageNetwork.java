/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.selenium.extension;


import ish.oncourse.selenium.test.AbstractSeleniumTest;
import ish.oncourse.util.selenium.service.extension.function.GoogleDevTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;

import java.util.Optional;
import java.util.logging.Level;

public class PrintPageNetwork implements BeforeEachCallback, TestWatcher {

    private static final Logger logger = LogManager.getLogger(PrintPageScreenshot.class);

    private final ThreadLocal<Optional<GoogleDevTools>> devTools = new ThreadLocal<>();

    @Override
    public void beforeEach(ExtensionContext context) {
        if (context.getTestInstances().isPresent()) {
            WebDriver driver = ((AbstractSeleniumTest) context.getTestInstance().get()).getDriver();
            System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");

            var logger = java.util.logging.Logger.getLogger(org.openqa.selenium.devtools.Connection.class.getName());
            logger.setLevel(Level.OFF);

            devTools.set(Optional.ofNullable(GoogleDevTools.valueOf(driver)
                    .createRequestListener()
                    .createResponseListener()));
        }
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        if (devTools.get().isPresent()) {

            logger.error("-----Requests-----");
            devTools.get().get().getRequestList().forEach(logger::error);
            logger.error("---Requests end---");

            logger.error("-----Response-----");
            devTools.get().get().getResponseList().forEach(logger::error);
            logger.error("---Response end---");

            TestWatcher.super.testFailed(context, cause);
        }
    }
}
