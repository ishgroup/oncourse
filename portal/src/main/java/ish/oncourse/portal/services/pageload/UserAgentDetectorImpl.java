package ish.oncourse.portal.services.pageload;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class UserAgentDetectorImpl implements IUserAgentDetector {

	@Inject
	private Request request;

	public UserAgent getUserAgent() {

		String userAgent = request.getHeader("User-Agent");
		return UserAgent.valueByAgentId(userAgent);
	}
}
