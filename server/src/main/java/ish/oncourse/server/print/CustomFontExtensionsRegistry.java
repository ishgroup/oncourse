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
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.fonts.FontFamily;
import net.sf.jasperreports.engine.fonts.SimpleFontFace;
import net.sf.jasperreports.engine.fonts.SimpleFontFamily;
import net.sf.jasperreports.extensions.ExtensionsRegistry;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class which registers all the fonts available on the system to use with jasper/itext.
 */
public class CustomFontExtensionsRegistry extends AbstractFontExtensionsRegistry {

	private List<FontFamily> fontFamilies = new ArrayList<>();

	/**
	 * To load font into setTtf method library tries to load jasperreports context, that caused
	 * new preinitialization of extensions and inifinite cycle. We need to avoid preinitialization
	 * of fonts during any font loading (on lower level of methods stack)
	 */
	private static boolean fontInitializationStarted = false;

	private CustomFontExtensionsRegistry() {
	}

	public static ExtensionsRegistry valueOf(ExtensionsRegistry extensionsRegistry) {
		var registry = new CustomFontExtensionsRegistry();
		registry.setExtensionsRegistry(extensionsRegistry);
		registry.registerFonts();
		return registry;
	}

	@Override
	public <T> List<T> getExtensions(Class<T> extensionType) {
		List<T> result = new ArrayList<>();

		var extensions = super.getExtensions(extensionType);
		if (extensions != null && !extensions.isEmpty()) {
			logger.info("Found {} FontFamilies.", extensions.size());
			result.addAll(extensions);
		}

		if (FontFamily.class.equals(extensionType)) {
			logger.info("Found {} FontFamilies from other folders", fontFamilies.size());

			result.addAll((List<T>)fontFamilies);
		}

		return result.isEmpty() ? null : result;
	}


	@Override
	public void registerFonts() {
		super.registerFonts();
		if(fontInitializationStarted)
			return;

		List<File> folders = new ArrayList<>(8);

		// load fonts from the 'fonts' directory where the application is running
		folders.add(new File("fonts"));

		// OSX
		folders.add(new File("/Library/Fonts"));
		folders.add(new File(System.getProperty("user.home"), "/Library/Fonts"));

		// Windows
		folders.add(new File("/Windows/Fonts"));

		// FreeBSD
		folders.add(new File("/usr/local/lib/X11/fonts/"));

		// Ubuntu
		folders.add(new File("/usr/share/fonts"));
		folders.add(new File("/usr/X11R6/lib/X11/fonts"));
		//registerFontsFromFolder(new File("/usr/share/fonts"));
		folders.add(new File(System.getProperty("user.home"), ".fonts"));

		folders.forEach(this::registerFontsFromFolder);
	}


	/**
	 * registers all the fonts in a given directory and subfolders
	 *
	 * @param folder with fonts
	 */
	private void registerFontsFromFolder(File folder) {
		if (!folder.exists() || !folder.isDirectory()) {
			logger.debug("Path to the additional fonts folder {} does not exist or is not a directory.", folder.getAbsolutePath());
			return;
		}

		// register fonts recursively with itext
		FontFactory.registerDirectory(folder.getAbsolutePath(), true);
		// register fonts with jasper
		for (final var fontFile : folder.listFiles()) {
			try {
				var family = new SimpleFontFamily();
				var normalFace = new SimpleFontFace(DefaultJasperReportsContext.getInstance());

				fontInitializationStarted = true;
				normalFace.setTtf(fontFile.getAbsolutePath());
				family.setNormalFace(normalFace);

				fontInitializationStarted = false;
				var font = Font.createFont(Font.TRUETYPE_FONT, fontFile);

				family.setPdfEmbedded(true);
				logger.warn("registering font {} as {} and name {}", fontFile, font.getFamily(), font.getName());
				fontFamilies.add(family);

			} catch (Exception e) {
				// ignore the error since we just hit a font we couldn't parse
				fontInitializationStarted = false;
				logger.warn("can't register font {}. Reason: {}", fontFile, e.getMessage());
			}

		}
		logger.warn("registered {} fonts from {}", fontFamilies.size(), folder.getAbsolutePath());
	}
}
