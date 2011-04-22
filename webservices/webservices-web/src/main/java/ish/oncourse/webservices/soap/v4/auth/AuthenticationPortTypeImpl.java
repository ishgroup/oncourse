package ish.oncourse.webservices.soap.v4.auth;

import ish.oncourse.model.College;
import ish.oncourse.model.KeyStatus;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.services.ICollegeRequestService;

import java.util.Date;
import java.util.Random;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.cayenne.ObjectContext;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Session;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Web service which initiates/finishes conversations for Angel-Willow
 * replication.
 * 
 * 
 * @author Marek Wawrzyczny
 */

@WebService(targetNamespace = "http://auth.v4.soap.webservices.oncourse.ish/", endpointInterface = "ish.oncourse.webservices.soap.v4.auth.AuthenticationPortType", serviceName = "AuthenticationService", portName = "AuthenticationPort")
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
	private ICollegeRequestService collegeRequestService;

	@Inject
	@Autowired
	private Messages messages;

	/**
	 * Authenticates user, stores details in HTTP Session.
	 * 
	 * @param securityCode
	 *            code generated/stored within Angel database
	 * @param lastCommunicationKey
	 *            communication key used in the last communication session
	 * 
	 * @return next communication key to track current conversation.
	 */
	@WebMethod(operationName = "authenticate", action = "authenticate")
	public long authenticate(String webServicesSecurityCode, long lastCommKey) throws AuthFailure {

		if (collegeRequestService.getCollegeSession(false) != null) {
			throw new AuthFailure(messages.get("invalid.session"), ErrorCode.INVALID_SESSION);
		}

		College college = collegeService.findBySecurityCode(webServicesSecurityCode);

		if (college == null) {
			LOGGER.error("No college found for 'security code': " + webServicesSecurityCode);
			throw new AuthFailure(messages.format("invalid.securityCode", webServicesSecurityCode), ErrorCode.INVALID_SECURITY_CODE);
		}

		ObjectContext ctx = cayenneService.newContext();

		Date today = new Date();

		if (college.getCommunicationKey() == null && college.getCommunicationKeyStatus() == KeyStatus.VALID) {
			// Null key set as valid.
			throw new AuthFailure(messages.get("null.communicationKey"), ErrorCode.EMPTY_COMMUNICATION_KEY);
		}

		if (college.getCommunicationKey() != null && college.getCommunicationKeyStatus() == KeyStatus.HALT) {
			// Communication key in a HALT state. Refuse authentication attempt.
			throw new AuthFailure(messages.get("communicationKey.halt"), ErrorCode.HALT_COMMUNICATION_KEY);
		}

		boolean invalidKey = college.getCommunicationKey() != null && college.getCommunicationKeyStatus() == KeyStatus.VALID
				&& !college.getCommunicationKey().equals(lastCommKey);

		if (invalidKey) {
			// Invalid communication key put college in a HALT state.
			College local = (College) ctx.localObject(college.getObjectId(), null);
			local.setCommunicationKeyStatus(KeyStatus.HALT);
			ctx.commitChanges();

			throw new AuthFailure(messages.format("communicationKey.invalid", lastCommKey), ErrorCode.INVALID_COMMUNICATION_KEY);
		}

		// Normal flow or recovering from HALT state. Generate and store new
		// communication key.

		College local = (College) ctx.localObject(college.getObjectId(), null);

		Random randomGen = new Random();
		long newCommunicationKey = ((long) randomGen.nextInt(63) << 59) + System.currentTimeMillis();

		Session session = collegeRequestService.getCollegeSession(true);
		session.setAttribute(SessionToken.SESSION_TOKEN_KEY, new SessionToken(local, newCommunicationKey));

		local.setCommunicationKey(newCommunicationKey);
		local.setCommunicationKeyStatus(KeyStatus.VALID);

		if (local.getFirstRemoteAuthentication() == null) {
			local.setFirstRemoteAuthentication(today);
		}

		local.setLastRemoteAuthentication(today);

		ctx.commitChanges();

		return newCommunicationKey;
	}

	/**
	 * End the session on Willow - this will discard the HTTP Session.
	 * 
	 * @param communicationKey
	 *            the communication key returned for this communication session
	 * 
	 * @return logout status
	 */
	@WebMethod(operationName = "logout", action = "logout")
	@Oneway
	public void logout(long newCommKey) {
		Session session = collegeRequestService.getCollegeSession(false);

		if (session != null) {
			SessionToken token = (SessionToken) session.getAttribute(SessionToken.SESSION_TOKEN_KEY);
			if (token.getCommunicationKey().equals(newCommKey)) {
				session.invalidate();
			} else {
				LOGGER.error(messages.format("communicationKey.invalid", newCommKey));
			}
		}
	}
}
