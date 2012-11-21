package ish.oncourse.portal.services.pageload;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class UserAgentDetectorImpl implements IUserAgentDetector {
	private static final String SYSTEM_PROPERTY_TEST_AGENT_ID = "TEST_AGENT_ID";
	private static final String HEADER_USER_AGENT = "User-Agent";



	@Inject
	private Request request;

	public UserAgent getUserAgent() {

		return UserAgent.valueByAgentId(getUserAgentAsString());
	}

	/**
	 * The method allows to predefine userAgent by system property <code>TEST_AGENT_ID</code>
	 */
	private String getUserAgentAsString()
	{
		String testUserAgent = System.getProperty(SYSTEM_PROPERTY_TEST_AGENT_ID);
		String userAgent = request.getHeader(HEADER_USER_AGENT);
		return (testUserAgent != null) ? testUserAgent:userAgent;
	}
}
