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

import com.lowagie.text.FontFactory;
import net.sf.jasperreports.extensions.ExtensionsRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public abstract class AbstractFontExtensionsRegistry implements ExtensionsRegistry {

    protected static final Logger logger = LogManager.getLogger();

    private static final String FONT_RESOURCES_PATH = "resources/fonts";
    private static final Pattern FONT_PATTERN = Pattern.compile(".*\\.ttf");

    private ExtensionsRegistry extensionsRegistry;

    public void setExtensionsRegistry(ExtensionsRegistry extensionsRegistry) {
        this.extensionsRegistry = extensionsRegistry;
    }

    @Override
    public <T> List<T> getExtensions(Class<T> extensionType) {
        return extensionsRegistry.getExtensions(extensionType);
    }

    public void registerFonts() {
        registerFontsForITextFromResources();
    }

    private void registerFontsForITextFromResources() {
        var reflections = new Reflections(FONT_RESOURCES_PATH, new ResourcesScanner());
        var fonts = reflections.getResources(FONT_PATTERN);
        fonts.forEach(FontFactory::register);
    }

}
