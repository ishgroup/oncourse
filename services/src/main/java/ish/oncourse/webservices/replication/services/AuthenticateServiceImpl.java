package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.College;
import ish.oncourse.model.KeyStatus;
import ish.oncourse.model.access.SessionToken;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.exception.StackTraceUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import java.util.Date;
import java.util.Random;

public class AuthenticateServiceImpl implements IAuthenticateService {
	private final static Logger logger = LogManager.getLogger();
	
	private ICayenneService cayenneService;
	
	private WebServiceContext webServiceContext;
	
	private ICollegeService collegeService;
	
	public AuthenticateServiceImpl() {}
	
	public AuthenticateServiceImpl(ICayenneService cayenneService, WebServiceContext webServiceContext, ICollegeService collegeService) {
		this.cayenneService = cayenneService;
		this.webServiceContext = webServiceContext;
		this.collegeService = collegeService;
	}
	
	/**
	 * @return the cayenneService
	 */
	public ICayenneService takeCayenneService() {
		return cayenneService;
	}
	
	/**
	 * @return the webServiceContext
	 */
	public WebServiceContext takeWebServiceContext() {
		return webServiceContext;
	}
	
	/**
	 * @return the collegeService
	 */
	public ICollegeService takeCollegeService() {
		return collegeService;
	}
	
	@Override
	public long authenticate(String webServicesSecurityCode, long lastCommKey) throws InternalAuthenticationException {
		try {
			@SuppressWarnings("unused")
			HttpServletRequest request = (HttpServletRequest) takeWebServiceContext().getMessageContext().get(AbstractHTTPDestination.HTTP_REQUEST);
			logger.info("Got college request with securityCode: {}, lastCommKey: {}.", webServicesSecurityCode, lastCommKey);
			College college = takeCollegeService().findBySecurityCode(webServicesSecurityCode);
			if (college == null) {
				String message = String.format("No college found for 'security code':%s.", webServicesSecurityCode);
				InternalAuthenticationException ex = new InternalAuthenticationException(message, InternalErrorCode.INVALID_SECURITY_CODE);
				logger.error(message, ex);
				throw ex;
			}
			//we need the code to reset cached college's properties
			college.setPersistenceState(PersistenceState.HOLLOW);
			Long currentKey = college.getCommunicationKey();
			boolean recoverFromHALT = (currentKey == null) && (college.getCommunicationKeyStatus() == KeyStatus.HALT);
			if (currentKey == null) {
				// we didn't find key
				if (recoverFromHALT) {
					// recovering from HALT state
					Long newKey = generateNewKey(college);
					return newKey;
				} else {
					InternalAuthenticationException ex = new InternalAuthenticationException(String.format("Invalid communication key:%s", lastCommKey), 
							InternalErrorCode.INVALID_COMMUNICATION_KEY);
					logger.error("Communication key is null for college: {}, when received key is {}.", college.getId(), lastCommKey, ex);
					putCollegeInHaltState(college);
					throw ex;
				}
			} else {
				if (college.getCommunicationKeyStatus() != KeyStatus.VALID) {
					// Communication key in a HALT state. Refuse authentication
					// attempt.
					InternalAuthenticationException ex = new InternalAuthenticationException(String.format("Communication key:%s in a HALT state.", 
						lastCommKey), InternalErrorCode.HALT_COMMUNICATION_KEY);
					logger.debug("Communication key: {} for college: {} in a HALT state.", lastCommKey, college.getId(), ex);
					throw ex;
				}
				if (lastCommKey == currentKey) {
					Long newKey = generateNewKey(college);
					return newKey;
				} else {
					InternalAuthenticationException ex = new InternalAuthenticationException(String.format("Invalid communication key: %s.", lastCommKey), 
						InternalErrorCode.INVALID_COMMUNICATION_KEY);
					logger.warn("Invalid communication key: {}, for college: {}, expected: {}.", lastCommKey, college.getId(), currentKey, ex);
					putCollegeInHaltState(college);
					// TODO: !!!!! Here should be exception, since we're in HALT state !!!!!!
					return generateNewKey(college);// throw ex;
				}
			}
		} catch (Exception e) {
			if (e instanceof InternalAuthenticationException) {
				throw (InternalAuthenticationException) e;
			} else {
				logger.error("Unable to authenticate. Generic exception.", e);
				String message = String.format("Unable to authenticate. Willow generic exception: %s", StackTraceUtils.stackTraceAsString(e));
				throw new InternalAuthenticationException(message);
			}
		}
	}

	@Override
	public Long generateNewKey(College college) {
		ObjectContext objectContext = takeCayenneService().newNonReplicatingContext();
		College local = objectContext.localObject(college);
		Random randomGen = new Random();
		long newCommunicationKey = System.currentTimeMillis();
		local.setCommunicationKey(newCommunicationKey);
		local.setCommunicationKeyStatus(KeyStatus.VALID);

		Date date = new Date();
		if (local.getFirstRemoteAuthentication() == null) {
			local.setFirstRemoteAuthentication(date);
		} else {
			local.setLastRemoteAuthentication(date);
		}
		objectContext.commitChanges();

		HttpServletRequest request = (HttpServletRequest) takeWebServiceContext().getMessageContext().get(AbstractHTTPDestination.HTTP_REQUEST);
		request.setAttribute(SessionToken.SESSION_TOKEN_KEY, new SessionToken(college.getId(), newCommunicationKey));
		return newCommunicationKey;
	}

	@Override
	public void putCollegeInHaltState(College college) {
		logger.warn("Putting college: {} into HALT state.", college.getId());
		/*
		 * college.setCommunicationKeyStatus(KeyStatus.HALT);
		 * college.getObjectContext().commitChanges();
		 */
	}

}
