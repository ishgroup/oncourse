package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.College;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.exception.StackTraceUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import java.util.Date;

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
	public void authenticate(String webServicesSecurityCode) throws InternalAuthenticationException {
		try {
			logger.info("Got college request with securityCode: {}", webServicesSecurityCode);
			College college = takeCollegeService().findBySecurityCode(webServicesSecurityCode);
			if (college == null) {
				String message = String.format("No VALID college found for 'security code':%s.", webServicesSecurityCode);
				InternalAuthenticationException ex = new InternalAuthenticationException(message, InternalErrorCode.INVALID_SECURITY_CODE);
				logger.error(message, ex);
				throw ex;
			}
			recordCollegeAttribute(college);
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
	public void recordCollegeAttribute(College college) {
		ObjectContext objectContext = takeCayenneService().newNonReplicatingContext();
		College local = objectContext.localObject(college);

		Date date = new Date();

		local.setLastRemoteAuthentication(date);

		objectContext.commitChanges();

		HttpServletRequest request = (HttpServletRequest) takeWebServiceContext().getMessageContext().get(AbstractHTTPDestination.HTTP_REQUEST);
		request.setAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE, college.getId());
	}

}
