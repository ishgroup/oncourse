package ish.oncourse.util;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class RequestUtil {
	private static final Logger logger = LogManager.getLogger();

    private static final String MSIE = "MSIE";

    private  RequestUtil() {
        
    }

    public static String getAgentAwareClass(String userAgent) {
		if (userAgent != null && userAgent.contains(MSIE)) {
			int versionPosition = userAgent.indexOf(MSIE) + MSIE.length() + 1;
			Integer versionNumber;
			try {
				versionNumber = Integer.parseInt(userAgent.substring(versionPosition, versionPosition + 1));
			} catch (Throwable t) {
				logger.info("Incorrect or corrupted userAgent received {}", userAgent, t);
				return StringUtils.EMPTY;
			}
			switch (versionNumber) {
			case 7:
				return  " ie7";
			case 8:
				return  " ie8";
			case 9:
				return  " ie9";
			default:
				if (versionNumber < 7) {
					return " ie6";
				}
				if (versionNumber > 9) {
					return StringUtils.EMPTY;
				}
			}

		}

		return StringUtils.EMPTY;
	}
}
