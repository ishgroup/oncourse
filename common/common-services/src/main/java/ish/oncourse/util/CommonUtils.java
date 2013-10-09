package ish.oncourse.util;

import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class CommonUtils {

	private static final Logger LOGGER = Logger.getLogger(CommonUtils.class);

	public static final String VERSION_development = "development";

	public static final String VERSION_5_0 = "5.0";

	/**
	 *
	 * @param version1 not null
	 * @param version2  not null
	 */
	public static int compare(String version1 , String version2)
	{

		if (VERSION_development.equalsIgnoreCase(version1)) {
			LOGGER.info("pass the gradle development version");
			return 0;
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
