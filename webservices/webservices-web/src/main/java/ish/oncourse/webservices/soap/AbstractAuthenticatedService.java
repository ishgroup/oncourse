package ish.oncourse.webservices.soap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceContext;

import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.model.College;
import ish.oncourse.services.preference.IPreferenceService;
import ish.oncourse.services.system.ICollegeService;
import javax.jws.WebService;

/**
 * 
 *
 * @author Marek Wawrzyczny
 */
public abstract class AbstractAuthenticatedService implements AuthenticatedService {

	@Inject
	private ICollegeService collegeService;
	@Inject
	private IPreferenceService preferenceService;

	@Resource
	private WebServiceContext webServiceContext;

	private final static String SESSION_TOKEN_KEY = "ish_session_token";
	private final static String COLLEGE_KEY = "ish_college";

	private final static Logger LOGGER = Logger.getLogger(
			AbstractAuthenticatedService.class);


	@Override
	public void authenticate(String securityCode)
			throws AuthenticationException {

		if (getHttpServletRequest().isRequestedSessionIdValid()) {
			throw new AuthenticationException(
					"Authentication failure, existing session must be terminated first.");
		}

		College college = collegeService.findBySecurityCode(securityCode);

		if (college == null) {
			LOGGER.error("No college found for 'security code': " + securityCode);
			throw new AuthenticationException("Invalid security code");
		}

		HttpSession session = getHttpSession(true);
		session.setAttribute(COLLEGE_KEY, college);
	};

	@Override
	public void logout() {
		throw new UnsupportedOperationException("Not supported yet.");
	};

	public boolean hasHttpSession() {
		return (getHttpServletRequest().isRequestedSessionIdFromCookie() &&
				getHttpSession() != null);
	}

	public HttpSession getHttpSession() {
		return getHttpSession(false);
	}

	protected College getCollege() {
		return (getHttpSession() == null) ? null
				: (College) getHttpSession().getAttribute(COLLEGE_KEY);
	}

	private HttpServletRequest getHttpServletRequest() {
		return (HttpServletRequest) webServiceContext.getMessageContext().get(
				AbstractHTTPDestination.HTTP_REQUEST);
	}

	/**
	 * Returns the HTTP Session associated with the request.
	 *
	 * @param doCreate create a session if one does not exist if true
	 * @return
	 */
	private HttpSession getHttpSession(boolean doCreate) {
		return getHttpServletRequest().getSession(doCreate);
	}


}
