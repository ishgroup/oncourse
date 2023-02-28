/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.util.selenium.service.extension.function;

import ish.oncourse.util.selenium.model.HttpConfiguration;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

/**
 *  Loads the current HTTP configuration from onCourse.yml for building onCourse URL in tests.
 *  Used in ConfigurationResolver.
 */
public class LoadHttpConfiguration {

    private String configFile;

    private LoadHttpConfiguration() {
    }

    public static LoadHttpConfiguration valueOf(String configFile) {
        LoadHttpConfiguration loadHttpConfiguration = new LoadHttpConfiguration();
        loadHttpConfiguration.configFile = configFile;
        return loadHttpConfiguration;
    }

    public void load() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(configFile);

        Map<String, Object> projectConfiguration = yaml.load(inputStream);
        Map<String, Object> httpConfiguration = (Map<String, Object>) projectConfiguration.get("http");

        System.setProperty(HttpConfiguration.PORT.getKey(), String.valueOf(httpConfiguration.get("port")));
        System.setProperty(HttpConfiguration.IP.getKey(), (String) httpConfiguration.get("ip"));
        System.setProperty(HttpConfiguration.PATH.getKey(), (String) httpConfiguration.get("path"));
    }
}
