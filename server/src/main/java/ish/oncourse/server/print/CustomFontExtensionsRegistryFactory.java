/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.oncourse.server.print;

import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.fonts.SimpleFontExtensionsRegistryFactory;
import net.sf.jasperreports.extensions.ExtensionsRegistry;

/**
 */
public class CustomFontExtensionsRegistryFactory extends SimpleFontExtensionsRegistryFactory {

	/**
	 * @see net.sf.jasperreports.extensions.ExtensionsRegistryFactory#createRegistry(String, JRPropertiesMap)
	 */
	public ExtensionsRegistry createRegistry(String registryId, JRPropertiesMap properties) {
		var registry = super.createRegistry(registryId, properties);

		return CustomFontExtensionsRegistry.valueOf(registry);
	}

}
