/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.integration

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.util.jar.Manifest

class PluginsPrefsService {
    private static final Logger logger = LogManager.getLogger();

    private Map<String, String> plugins

    Object getProperty(String key) {
        if (plugins == null)
            loadPluginsInfo()

        switch (key) {
            case "plugins.names":
                return plugins.entrySet().collect { it -> it.key + "|" + it.value }.join(",")
            default:
                return null
        }
    }

    /**
     * This method uses awful method of info loading due to we don't store info about plugin in manifests in old versions.
     * After each college starts using plugins of later versions (>=72) we will replace it with loading of properties
     * from manifests files of plugins, without dependencies on jar names or paths
     */
    private void loadPluginsInfo() {
        plugins = new HashMap<>()
        Enumeration<URL> resources = getClass().getClassLoader()
                .getResources("META-INF/MANIFEST.MF");
        while (resources.hasMoreElements()) {
            try {
                def url = resources.nextElement()
                Manifest manifest = new Manifest(url.openStream())
                def attributes = manifest.getMainAttributes()
                def specification = attributes.getValue("Specification-Title") as String
                if(specification && specification.equals("OnCourse plugins")){
                    def pluginName = attributes.getValue("Implementation-Title")
                    def pluginVersion = attributes.getValue("Implementation-Version")
                    if(pluginName)
                        plugins.put(pluginName, pluginVersion)
                }
            } catch (IOException e) {
                logger.error("Error with plugins info loading: " + e.getMessage())
            }
        }
    }
}
