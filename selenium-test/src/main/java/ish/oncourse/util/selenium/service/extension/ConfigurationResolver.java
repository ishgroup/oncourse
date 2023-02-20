/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.util.selenium.service.extension;

import ish.oncourse.util.selenium.model.Browser;
import ish.oncourse.util.selenium.model.WebDriverFactory;
import ish.oncourse.util.selenium.service.extension.function.LoadHttpConfiguration;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.WebDriver;

public class ConfigurationResolver implements ParameterResolver, BeforeAllCallback {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(WebDriver.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return WebDriverFactory.get(Browser.CHROME);
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        LoadHttpConfiguration.valueOf("onCourse.yml").load();
    }
}
