package ish.oncourse.webservices.soap.auth;

import ish.oncourse.model.College;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.soap.Status;

import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Inject @Autowired
	private ICollegeService collegeService;
	
	@Inject
	@Autowired
	private Request request;

	@Override
	public Long authenticate(String webServicesSecurityCode, Long lastCommKey) {

		if (request.getSession(false) != null) {
			throw new AuthenticationException("Authentication failure, existing session must be terminated first.");
		}

		College college = collegeService.findBySecurityCode(webServicesSecurityCode);

		if (college == null) {
			LOGGER.error("No college found for 'security code': " + webServicesSecurityCode);
			throw new AuthenticationException("Invalid security code");
		}

		// Now check if communication key is valid (note that a null college
		// comm key means one has not been used yet or has been reset on Willow
		// i.e. is communication valid
		if ((college.getCommunicationKey() != null) && ! lastCommKey.equals(college.getCommunicationKey())) {
			throw new AuthenticationException(String.format("Invalid communication key: %s.", lastCommKey));
		}

		// Generate and store new communication key.
		long newCommunicationKey = System.nanoTime();

		Session session = request.getSession(true);

		session.setAttribute(SessionToken.SESSION_TOKEN_KEY, new SessionToken(webServicesSecurityCode, newCommunicationKey));

		return newCommunicationKey;
	}

	@Override
	public Status logout(Long newCommKey) {
		Status status = new Status();

		// clean up current session
		request.getSession(false).invalidate();

		return status;
	}
}
