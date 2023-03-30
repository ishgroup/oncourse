/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.selenium.extension;

import ish.oncourse.selenium.test.AbstractSeleniumTest;
import ish.oncourse.util.selenium.service.extension.ConfigurationResolver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

@ExtendWith(ConfigurationResolver.class)
public class PrintPageScreenshot implements TestWatcher {

    private static final Logger logger = LogManager.getLogger(PrintPageScreenshot.class);

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        if (context.getTestInstances().isPresent()) {
            WebDriver driver = ((AbstractSeleniumTest) context.getTestInstance().get()).getDriver();

            TakesScreenshot scrShot = ((TakesScreenshot) driver);
            String data = scrShot.getScreenshotAs(OutputType.BASE64);

            logger.error("---" + driver.getTitle() + " page screenshot in base64" + "---");
            logger.error(data);
            logger.error("-----end-----");
        }
        TestWatcher.super.testFailed(context, cause);
    }
}
