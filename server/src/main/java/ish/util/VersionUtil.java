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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class VersionUtil {

	private static final Logger logger = LogManager.getLogger();

	public static final String VERSION_development = "development";
	public static final String VERSION_trunk_SNAPSHOT = "SNAPSHOT";

	public static final String VERSION_PREFIX_RELEASE = "release-";
	public static final String VERSION_SUFFIX_SNAPSHOT = "-SNAPSHOT";

	/**
	 *
	 * @param version1 not null
	 * @param version2  not null
	 */
	public static int compare(String version1 , String version2)
	{
		//these tests need to exclude NPE
		if (version1 == null)
			return -1;
		if (version2 == null)
			return 1;
		if (version1.equals(version2))
			return 0;

		// The code extracts version number from 'release-<version>-SNAPSHOT'
		if (version1 != null && version1.startsWith(VERSION_PREFIX_RELEASE))
		{
			version1 = version1.replace(VERSION_PREFIX_RELEASE, StringUtils.EMPTY);
			version1 = version1.replace(VERSION_SUFFIX_SNAPSHOT, StringUtils.EMPTY);
		}
		if (VERSION_development.equalsIgnoreCase(version1) ||
				VERSION_trunk_SNAPSHOT.equalsIgnoreCase(version1)) {
			logger.info("pass the gradle development and trunk-SNAPSHOT version");
			return 1;
		}

		DefaultArtifactVersion aVersion1 = new DefaultArtifactVersion(version1);
		DefaultArtifactVersion aVersion2 = new DefaultArtifactVersion(version2);
		return aVersion1.compareTo(aVersion2);
	}
}
