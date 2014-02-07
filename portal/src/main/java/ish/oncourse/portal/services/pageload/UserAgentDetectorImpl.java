package ish.oncourse.portal.services.pageload;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
@Deprecated
public class UserAgentDetectorImpl implements IUserAgentDetector {
	private static final String SYSTEM_PROPERTY_TEST_AGENT_ID = "TEST_AGENT_ID";
	private static final String HEADER_USER_AGENT = "User-Agent";



	@Inject
	private Request request;

	public UserAgent getUserAgent() {

		//todo uncomment this line when we will be ready to continue portal-mobile skin developing
		//return UserAgent.valueByAgentId(getUserAgentAsString());
		return UserAgent.DESKTOP;
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
