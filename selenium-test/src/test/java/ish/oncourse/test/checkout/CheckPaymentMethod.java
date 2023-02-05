/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.test.checkout;

import ish.oncourse.server.api.v1.model.LoginRequestDTO;
import ish.oncourse.service.AuthenticationService;
import ish.oncourse.service.EmailAuthenticationService;
import ish.oncourse.test.AbstractSeleniumTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.concurrent.TimeUnit;


@TestInstance(TestInstance. Lifecycle.PER_CLASS)
public class CheckPaymentMethod extends AbstractSeleniumTest{

    private static AuthenticationService emailAuthenticationService;

    @BeforeAll
    public void setUp() {
        super.setUp();
        emailAuthenticationService = new EmailAuthenticationService(driver, js);
        var dto = new LoginRequestDTO();
        dto.setLogin("admin@example.edu");
        dto.setPassword("11111");
        emailAuthenticationService.login(dto);
    }

    @AfterAll
    public void tearDown() {
        emailAuthenticationService.logout();
        super.tearDown();
    }

    @Test
    void test() {
        // Test name: checkout 1
        // Step # | name | target | value
        // 1 | open | https://127.0.0.1:8182/checkout |
        driver.get("https://127.0.0.1:8182/checkout");
        // 2 | click | name=contacts |
        driver.findElement(By.name("contacts")).click();
        // 3 | type | name=contacts | Kristina Ish
        driver.findElement(By.name("contacts")).sendKeys("Kristina Ish");
        // 4 | click | css=span:nth-child(1) > strong |
//        driver.findElement(By.xpath("//div[@id=\'client\']/div/div/div[2]/div/div/div[2]/div/div/div/div/div[2]/nav/div[2]/div/span/strong")).click();
//        // 5 | click | name=items |
//        driver.findElement(By.name("items")).click();
//        // 6 | type | name=items | dcftc1
//        driver.findElement(By.name("items")).sendKeys("dcftc1");
//        // 7 | click | css=strong |
//        driver.findElement(By.cssSelector("strong")).click();
//        // 8 | click | css=.PrivateSwitchBase-input |
//        driver.findElement(By.cssSelector(".PrivateSwitchBase-input")).click();
//        // 9 | click | css=.MuiPaper-root:nth-child(3) .MuiSvgIcon-root |
//        driver.findElement(By.cssSelector(".MuiPaper-root:nth-child(3) .MuiSvgIcon-root")).click();
//        // 10 | click | css=.MuiIconButton-sizeSmall > .MuiSvgIcon-root |
//        driver.findElement(By.cssSelector(".MuiIconButton-sizeSmall > .MuiSvgIcon-root")).click();
//        // 11 | click | id=:rq:-option-0 |
//        driver.findElement(By.id(":rq:-option-0")).click();
//        // 12 | mouseOver | css=.MuiStepButton-root |
//        {
//            WebElement element = driver.findElement(By.cssSelector(".MuiStepButton-root"));
//            Actions builder = new Actions(driver);
//            builder.moveToElement(element).perform();
//        }
    }

}
