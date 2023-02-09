/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.test;

import ish.oncourse.model.Browser;
import ish.oncourse.model.WebDriverFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.*;

import java.util.concurrent.TimeUnit;


@TestInstance(TestInstance. Lifecycle.PER_CLASS)
public abstract class AbstractSeleniumTest {
    protected WebDriver driver;
    protected JavascriptExecutor js;

    @BeforeAll
    public void setUp() throws InterruptedException {
        driver = WebDriverFactory.get(Browser.CHROME);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        js = (JavascriptExecutor) driver;
    }

    @AfterAll
    public void tearDown() throws NoSuchFieldException {
        driver.quit();
    }

}
