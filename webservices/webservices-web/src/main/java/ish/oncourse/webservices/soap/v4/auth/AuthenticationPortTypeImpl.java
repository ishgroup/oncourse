package ish.oncourse.webservices.soap.v4.auth;

import ish.oncourse.model.College;
import ish.oncourse.model.KeyStatus;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.soap.v4.Status;

import java.util.Random;

import javax.jws.WebService;

import org.apache.cayenne.ObjectContext;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Web service which initiates/finishes conversations for Angel-Willow
 * replication.
 * 
 * 
 * @author Marek Wawrzyczny
 */

@WebService(
	endpointInterface = "ish.oncourse.webservices.soap.v4.auth.AuthenticationPortType",
	serviceName = "AuthenticationService",
	portName = "AuthenticationPort")
public class AuthenticationPortTypeImpl implements AuthenticationPortType {

	private final static Logger LOGGER = Logger.getLogger(AuthenticationPortTypeImpl.class);

	@Inject
	@Autowired
	private ICollegeService collegeService;

	@Inject
	@Autowired
	private ICayenneService cayenneService;

	@Inject
	@Autowired
	private Request request;

	@Override
	public Long authenticate(String webServicesSecurityCode, Long lastCommKey) throws AuthenticationFailureException {

		if (request.getSession(false) != null) {
			throw new AuthenticationFailureException("Authentication failure, existing session must be terminated first.",
					AuthenticationFailureException.INVALID_SESSION);
		}

		College college = collegeService.findBySecurityCode(webServicesSecurityCode);

		if (college == null) {
			LOGGER.error("No college found for 'security code': " + webServicesSecurityCode);
			throw new AuthenticationFailureException("Invalid security code", AuthenticationFailureException.INVALID_SECURITY_CODE);
		}

		ObjectContext ctx = cayenneService.newContext();

		if (college.getCommunicationKey() == null && college.getCommunicationKeyStatus() == KeyStatus.VALID) {
			// Null key set as valid.
			throw new AuthenticationFailureException("Can not have NULL as valid key.", AuthenticationFailureException.NULL_AS_VALID_KEY);
		}

		if (college.getCommunicationKey() != null && college.getCommunicationKeyStatus() == KeyStatus.HALT) {
			// Communication key in a HALT state. Refuse authentication attempt.
			throw new AuthenticationFailureException("Communication key in a HALT state.",
					AuthenticationFailureException.COMMUNICATION_KEY_HALTED);
		}
		
		boolean invalidKey = college.getCommunicationKey() != null && college.getCommunicationKeyStatus() == KeyStatus.VALID && !lastCommKey.equals(college.getCommunicationKey());

		if (invalidKey) {
			// Invalid communication key put college in a HALT state.
			College local = (College) ctx.localObject(college.getObjectId(), null);
			local.setCommunicationKeyStatus(KeyStatus.HALT);
			ctx.commitChanges();

			throw new AuthenticationFailureException(String.format("Invalid communication key: %s.", lastCommKey),
					AuthenticationFailureException.INVALID_COMMUNICATION_KEY);
		}

		// Normal flow or recovering from HALT state. Generate and store new
		// communication key.

		Random randomGen = new Random();
		long newCommunicationKey = ((long) randomGen.nextInt(63) << 59) + System.currentTimeMillis();

		Session session = request.getSession(true);
		session.setAttribute(SessionToken.SESSION_TOKEN_KEY, new SessionToken(webServicesSecurityCode, newCommunicationKey));

		College local = (College) ctx.localObject(college.getObjectId(), null);
		local.setCommunicationKey(newCommunicationKey);
		local.setCommunicationKeyStatus(KeyStatus.VALID);

		ctx.commitChanges();

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
