package ish.oncourse.portal.services.pageload;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class UserAgentDetectorImpl implements IUserAgentDetector {

	@Inject
	private Request request;

	public UserAgent getUserAgent() {

		String userAgent = request.getHeader("User-Agent");

		/*
		 * TODO: uncomment this code when we finish implement mobile schema 
		 */
/*		if (userAgent != null) {
			userAgent = userAgent.toLowerCase();
			if (userAgent.contains("iphone")) {
				return UserAgent.IPHONE;
			} else if (userAgent.contains("ipod")) {
				return UserAgent.IPOD;
			} else if (userAgent.contains("ipad")) {
				return UserAgent.IPAD;
			} else if (userAgent.contains("android")) {
				return UserAgent.ANDROID;
			} else if (userAgent.contains("blackBerry")) {
				return UserAgent.BLACKBERRY;
			}
		}
*/

		return UserAgent.DESKTOP;
	}
}
