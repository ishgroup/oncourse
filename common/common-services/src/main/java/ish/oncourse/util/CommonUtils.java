package ish.oncourse.util;

import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class CommonUtils {

	private static final Logger LOGGER = Logger.getLogger(CommonUtils.class);

	public static final String VERSION_development = "development";
	public static final String VERSION_trunk_SNAPSHOT = "trunk-SNAPSHOT";

    public static final String VERSION_PREFIX_RELEASE = "release-";
    public static final String VERSION_SUFFIX_SNAPSHOT = "-SNAPSHOT";

    public static final String VERSION_5_0 = "5.0A0";

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
			LOGGER.info("pass the gradle development and trunk-SNAPSHOT version");
			return 1;
		}

		DefaultArtifactVersion aVersion1 = new DefaultArtifactVersion(version1);
		DefaultArtifactVersion aVersion2 = new DefaultArtifactVersion(version2);
		return aVersion1.compareTo(aVersion2);
	}

	public static String getCurrentCollegeAngelVersion(Queueable entity) {
		return entity.getCollege() != null && StringUtils.trimToNull(entity.getCollege().getAngelVersion()) != null ?
				entity.getCollege().getAngelVersion() : College.UNDEFINED_ANGEL_VERSION;
	}
}
