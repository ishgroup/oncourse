package ish.oncourse.webservices.soap.v4.auth;

import ish.oncourse.model.College;
import ish.oncourse.model.CommunicationKey;
import ish.oncourse.model.CommunicationKeyType;
import ish.oncourse.model.KeyStatus;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;

import java.util.Random;

import javax.annotation.Resource;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceContext;

import org.apache.cayenne.ObjectContext;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
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

	@Resource
	private WebServiceContext webServiceContext;

	@Inject
	@Autowired
	private Messages messages;

	/**
	 * Default constructor for DI container.
	 */
	public AuthenticationPortTypeImpl() {

	}

	public AuthenticationPortTypeImpl(ICollegeService collegeService, ICayenneService cayenneService, WebServiceContext webServiceContext,
			Messages messages) {
		super();
		this.collegeService = collegeService;
		this.cayenneService = cayenneService;
		this.webServiceContext = webServiceContext;
		this.messages = messages;
	}

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
	private long authenticate(String webServicesSecurityCode, long lastCommKey, CommunicationKeyType keyType) throws AuthFailure {

		HttpServletRequest request = (HttpServletRequest) webServiceContext.getMessageContext().get(AbstractHTTPDestination.HTTP_REQUEST);
		HttpSession session = request.getSession(false);

		LOGGER.info(String.format("Got college request with securityCode:%s, lastCommKey:%s.", webServicesSecurityCode, lastCommKey));

		if (session != null) {
			throw new AuthFailure(messages.get("invalid.session"), ErrorCode.INVALID_SESSION);
		}

		College college = collegeService.findBySecurityCode(webServicesSecurityCode);

		if (college == null) {
			LOGGER.error("No college found for 'security code': " + webServicesSecurityCode);
			throw new AuthFailure(messages.format("invalid.securityCode", webServicesSecurityCode), ErrorCode.INVALID_SECURITY_CODE);
		}

		ObjectContext objectContext = cayenneService.newContext();
		college = (College) objectContext.localObject(college.getObjectId(), college);
		
		if (college.getCommunicationKeys().isEmpty()) {
			String message = messages.format("no.keys", college.getId());
			LOGGER.error(message);
			throw new AuthFailure(message, ErrorCode.NO_KEYS);
		}

		CommunicationKey currentKey = null;
		CommunicationKey recoverFromHALT = null;

		for (CommunicationKey key : college.getCommunicationKeys()) {
			Long collegeCommunicationKey = key.getKey();
			KeyStatus keyStatus = key.getKeyStatus();
			CommunicationKeyType collegeKeyType = key.getType();

			LOGGER.info(String.format("Load from db communicationKey:%s, status:%s", collegeCommunicationKey, keyStatus));

			if (keyType == collegeKeyType) {
				if (collegeCommunicationKey == null && keyStatus == KeyStatus.HALT) {
					recoverFromHALT = key;
				} else if (collegeCommunicationKey != null && collegeCommunicationKey.equals(lastCommKey)) {
					currentKey = key;
				}
			}
		}

		if (currentKey == null) {
			// we didn't find key
			if (recoverFromHALT != null) {
				// recovering from HALT state
				generateNewKey(recoverFromHALT);
				return recoverFromHALT.getKey();
			} else {
				for (CommunicationKey key : college.getCommunicationKeys()) {
					key.setKeyStatus(KeyStatus.HALT);
				}

				objectContext.commitChanges();

				throw new AuthFailure(messages.format("communicationKey.invalid", lastCommKey), ErrorCode.INVALID_COMMUNICATION_KEY);
			}
		} else {
			if (currentKey.getKeyStatus() == KeyStatus.VALID) {
				generateNewKey(currentKey);
				return currentKey.getKey();
			} else {
				// Communication key in a HALT state. Refuse authentication
				// attempt.
				throw new AuthFailure(messages.format("communicationKey.halt", lastCommKey), ErrorCode.HALT_COMMUNICATION_KEY);
			}
		}
	}

	private void generateNewKey(CommunicationKey key) {

		Random randomGen = new Random();
		long newCommunicationKey = ((long) randomGen.nextInt(63) << 59) + System.currentTimeMillis();

		key.setKey(newCommunicationKey);
		key.setKeyStatus(KeyStatus.VALID);

		HttpServletRequest request = (HttpServletRequest) webServiceContext.getMessageContext().get(AbstractHTTPDestination.HTTP_REQUEST);
		HttpSession session = request.getSession(true);
		session.setAttribute(SessionToken.SESSION_TOKEN_KEY, new SessionToken(key.getCollege(), newCommunicationKey));

		key.getObjectContext().commitChanges();
	}

	@Override
	@WebMethod(operationName = "authenticateRefund", action = "authenticateRefund")
	public long authenticateRefund(String securityCode, long lastCommunicationKey) throws AuthFailure {
		return authenticate(securityCode, lastCommunicationKey, CommunicationKeyType.REFUND);
	}

	@Override
	@WebMethod(operationName = "authenticateReplication", action = "authenticateReplication")
	public long authenticateReplication(String securityCode, long lastCommunicationKey) throws AuthFailure {
		return authenticate(securityCode, lastCommunicationKey, CommunicationKeyType.REPLICATION);
	}

	@Override
	@WebMethod(operationName = "authenticatePayment", action = "authenticatePayment")
	public long authenticatePayment(String securityCode, long lastCommunicationKey) throws AuthFailure {
		return authenticate(securityCode, lastCommunicationKey, CommunicationKeyType.PAYMENT);
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

		HttpServletRequest request = (HttpServletRequest) webServiceContext.getMessageContext().get(AbstractHTTPDestination.HTTP_REQUEST);

		HttpSession session = request.getSession(false);

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
