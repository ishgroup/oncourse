package ish.oncourse.webservices.soap.v4.interceptors;

import ish.oncourse.model.College;
import ish.oncourse.model.KeyStatus;
import ish.oncourse.model.access.SessionToken;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.exception.AuthSoapFault;
import ish.oncourse.webservices.exception.StackTraceUtils;
import ish.oncourse.webservices.util.SoapUtil;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

public class CommunicationKeyInterceptor extends AbstractSoapInterceptor {

	private static final String LOGOUT_METHOD = "logout";
	private static final String AUTHENTICATE_METHOD = "authenticate";
	private static final Logger logger = Logger.getLogger(CommunicationKeyInterceptor.class);

	@Inject
	@Autowired
	private ICollegeService collegeService;

	public CommunicationKeyInterceptor() {
		super(Phase.PRE_INVOKE);
	}

	public void handleMessage(SoapMessage message) throws Fault {

		try {
			
			BindingOperationInfo bindingInfo = message.getExchange().get(BindingOperationInfo.class);
			
			QName op = (QName) bindingInfo.getName();

			if (op != null && (AUTHENTICATE_METHOD.equalsIgnoreCase(op.getLocalPart()) || LOGOUT_METHOD.equalsIgnoreCase(op.getLocalPart()))) {
				return;
			}

			String securityCode = SoapUtil.getSecurityCode(message);
			final HttpServletRequest httpRequest = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
			final String ip = (httpRequest != null) ? httpRequest.getRemoteAddr() : "unknown";
			final String version = SoapUtil.getAngelVersion(message);
			if (securityCode == null) {
				final String m = String.format("Empty security code in replication soap message from ip = %s with angel server version = %s .", 
					ip, version);
				logger.error(m);
				throw new AuthSoapFault(m);
			}

			College college = collegeService.findBySecurityCode(securityCode);

			if (college == null) {
				String m = String.format("College not found. Invalid security code %s from ip = %s with angel server version = %s .", securityCode, ip, 
					version);
				logger.error(m);
				throw new AuthSoapFault(m);
			}

			if (college.getCommunicationKeyStatus() == KeyStatus.HALT) {
				String m = String.format("Communication key for college:%s in a HALT state.", college.getId());
				logger.error(m);
				throw new AuthSoapFault(m);
			}

			Long communicationKey = null;

			try {
				communicationKey = Long.parseLong(SoapUtil.getCommunicationKey(message));
				//final HttpServletRequest req = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
				//each request should contain session token (need for #13890)
				final SessionToken token = new SessionToken(college.getId(), communicationKey);
				httpRequest.setAttribute(SessionToken.SESSION_TOKEN_KEY, token);
				httpRequest.setAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE, token.getCollegeId());

			} catch (NumberFormatException ne) {
				String m = String.format("Invalid communication key %s from ip = %s with angel server version = %s .", communicationKey, ip, version);
				logger.error(m, ne);
				throw new AuthSoapFault(m);
			}

		} catch (Exception e) {
			logger.error("Willow generic exception.", e);
			String m = String.format("Willow generic exception:%s.", StackTraceUtils.stackTraceAsString(e));
			throw new AuthSoapFault(m);
		}
	}
}
