/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.selenium.service.extension;

import ish.oncourse.selenium.model.HttpConfiguration;
import ish.oncourse.selenium.service.extension.service.WebDriverResolver;
import ish.oncourse.selenium.service.shell.command.DecodeReactErrorCommand;
import ish.oncourse.selenium.service.shell.function.LogMessageDecoder;
import ish.oncourse.selenium.service.shell.function.ShellExecutor;
import ish.oncourse.test.AbstractSeleniumTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogType;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

@ExtendWith({WebDriverResolver.class})
public class PrintBrowserConsole implements TestWatcher, BeforeAllCallback {

    private static final Logger logger = LogManager.getLogger(PrintBrowserConsole.class);

    @Override
    public void beforeAll(ExtensionContext context) {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("onCourse.yml");

        Map<String, Object> projectConfiguration = yaml.load(inputStream);
        Map<String, Object> httpConfiguration = (Map<String, Object>) projectConfiguration.get("http");

        System.setProperty(HttpConfiguration.PORT.getKey(), String.valueOf(httpConfiguration.get("port")));
        System.setProperty(HttpConfiguration.IP.getKey(), (String) httpConfiguration.get("ip"));
        System.setProperty(HttpConfiguration.PATH.getKey(), (String) httpConfiguration.get("path"));
        logger.error("SET UP PROPERTY");
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {

        if (context.getTestInstances().isPresent()) {
            WebDriver driver = ((AbstractSeleniumTest) context.getTestInstance().get()).getDriver();

            logger.error("-------Сonsole-------");
            driver.manage().logs().get(LogType.BROWSER).getAll().forEach(logEntry -> {
                LogMessageDecoder decoder = LogMessageDecoder.valueOf(logEntry.getMessage());
                logger.error(logEntry.getMessage());
                logger.error("IS REACT LOG = " + decoder.isReactLog());
                if (decoder.isReactLog()) {
                    decoder.decode();
                    logger.error("---Decoding---" +
                            ShellExecutor.valueOf()
                                    .executeCommand(
                                            new DecodeReactErrorCommand(
                                                    decoder.getBundleName(),
                                                    decoder.getSourceMapName(),
                                                    decoder.getErrorLocation()).getCommand()
                                    )
                                    .getMessage()
                            + "--------------"
                    );
                }
            });
            logger.error("-----Сonsole end-----");
        }
        TestWatcher.super.testFailed(context, cause);
    }
}
