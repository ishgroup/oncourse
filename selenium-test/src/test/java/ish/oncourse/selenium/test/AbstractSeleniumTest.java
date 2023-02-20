/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.selenium.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.*;

@TestInstance(TestInstance. Lifecycle.PER_CLASS)
public abstract class AbstractSeleniumTest {
    protected WebDriver driver;

    protected JavascriptExecutor js;

    @BeforeAll
    public void setUp(WebDriver webDriver) {
        driver = webDriver;
        js = (JavascriptExecutor) driver;
    }

    @AfterAll
    public void tearDown() {
        driver.quit();
    }

    public WebDriver getDriver() {
        return driver;
    }

    public JavascriptExecutor getJs() {
        return js;
    }
}
