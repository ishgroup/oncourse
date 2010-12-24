package ish.oncourse.webservices.soap.auth;

import ish.oncourse.model.College;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.soap.Status;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceContext;

import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Web service which initiates/finishes conversations for Angel-Willow replication. 
 *  
 * 
 * @author Marek Wawrzyczny
 */

@WebService(endpointInterface = "ish.oncourse.webservices.soap.auth.AuthenticationPortType",
		serviceName = "AuthenticationService",
		portName = "AuthenticationPort")

public class AuthenticationPortTypeImpl implements AuthenticationPortType {

	private final static Logger LOGGER = Logger.getLogger(AuthenticationPortTypeImpl.class);

	@Inject
	private ICollegeService collegeService;

	@Resource
	private WebServiceContext webServiceContext;

	@Override
	public Long authenticate(String webServicesSecurityCode, Long lastCommKey) {

		if (getHttpServletRequest().isRequestedSessionIdValid()) {
			throw new AuthenticationException("Authentication failure, existing session must be terminated first.");
		}

		College college = collegeService.findBySecurityCode(webServicesSecurityCode);

		if (college == null) {
			LOGGER.error("No college found for 'security code': " + webServicesSecurityCode);
			throw new AuthenticationException("Invalid security code");
		}

		// Now check if communication key is valid

		if (!lastCommKey.equals(college.getCommunicationKey())) {
			throw new AuthenticationException(String.format("Invalid communication key: %s.", lastCommKey));
		}

		// Generate and store new communication key.
		long newCommunicationKey = System.nanoTime();

		HttpSession session = getHttpSession(true);

		session.setAttribute(SessionToken.SESSION_TOKEN_KEY, new SessionToken(webServicesSecurityCode, newCommunicationKey));

		return newCommunicationKey;
	}

	@Override
	public Status logout(Long newCommKey) {
		Status status = new Status();

		// clean up current session
		getHttpSession(false).invalidate();

		return status;
	}

	/**
	 * Returns the HTTP Session associated with the request.
	 * 
	 * @param doCreate
	 *            create a session if one does not exist if true
	 * @return
	 */
	private HttpSession getHttpSession(boolean doCreate) {
		return getHttpServletRequest().getSession(doCreate);
	}

	/**
	 * Convenience method to extract http request from injected cxf objects.
	 * 
	 * @return http request
	 */
	private HttpServletRequest getHttpServletRequest() {
		return (HttpServletRequest) webServiceContext.getMessageContext().get(AbstractHTTPDestination.HTTP_REQUEST);
	}
}
