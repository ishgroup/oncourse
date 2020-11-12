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
package ish.util;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FilenameFilter;

/**
 */
public class OneFilenameFilter implements FilenameFilter {
	String extension;

	public OneFilenameFilter(String ext) {
		if (StringUtils.isEmpty(ext)) {
			throw new IllegalArgumentException("file filter cannot be instantiated with null extension");
		}
		this.extension = ext;
	}

	public boolean accept(File dir, String name) {
		return name.endsWith(this.extension);
	}
}
