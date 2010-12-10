package ish.oncourse.util;


public class RequestUtil {

    private static final String MSIE = "MSIE";

    private  RequestUtil() {
        
    }

    public static String getAgentAwareClass(String userAgent) {
		if (userAgent.indexOf(MSIE) > -1) {
			int versionPosition = userAgent.indexOf(MSIE) + MSIE.length() + 1;
			Integer versionNumber = Integer.parseInt(userAgent.substring(versionPosition, versionPosition + 1));
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
					return "";
				}
			}

		}

		return "";
	}
}
