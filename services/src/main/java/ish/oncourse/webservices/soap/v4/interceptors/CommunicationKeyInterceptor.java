package ish.oncourse.webservices.soap.v4.interceptors;

import ish.oncourse.model.College;
import ish.oncourse.model.KeyStatus;
import ish.oncourse.model.access.SessionToken;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.util.SoapUtil;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

public class CommunicationKeyInterceptor extends AbstractSoapInterceptor {

	private static final String METHOD_logout = "logout";
	private static final String METHOD_confirmExecution = "confirmExecution";
	private static final String METHOD_authenticate = "authenticate";
	private static final Logger logger = LogManager.getLogger();

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

			if (op != null && (METHOD_authenticate.equalsIgnoreCase(op.getLocalPart()) ||
					METHOD_logout.equalsIgnoreCase(op.getLocalPart()) ||
					/**
					 * the interceptor should not be could for confirmExecution action because the following reasons:
					 * 1. cxf >= 2.6.1 executes the interceptor in another thread than main request. TODO we cannot use  httpRequest to setAttribute because these operations are not thread safe
					 * 2. confirmExecution action does not need information about college or communication key.
					 */
					METHOD_confirmExecution.equalsIgnoreCase(op.getLocalPart())

			)) {
				return;
			}

			String securityCode = SoapUtil.getSecurityCode(message);
			final HttpServletRequest httpRequest = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
			final String ip = (httpRequest != null) ? httpRequest.getRemoteAddr() : "unknown";
			final String version = SoapUtil.getAngelVersion(message);
			if (securityCode == null) {
				String m = String.format("Empty security code in replication soap message from ip = %s with angel server version = %s .",
					ip, version);
				throw  new InterceptorErrorHandle(message, logger).handle(m);
			}

			College college = collegeService.findBySecurityCode(securityCode);

			if (college == null) {
				String m = String.format("College not found. Invalid security code %s from ip = %s with angel server version = %s .", securityCode, ip, 
					version);
				throw  new InterceptorErrorHandle(message,logger).handle(m);
			}

			if (college.getCommunicationKeyStatus() == KeyStatus.HALT) {
				String m = String.format("Communication key for college:%s in a HALT state.", college.getId());
				throw  new InterceptorErrorHandle(message,logger).handle(m);
			}

			Long communicationKey = null;

			try {
				communicationKey = Long.parseLong(SoapUtil.getCommunicationKey(message));
				//each request should contain session token (need for #13890)
				final SessionToken token = new SessionToken(college.getId(), communicationKey);
				httpRequest.setAttribute(SessionToken.SESSION_TOKEN_KEY, token);
				httpRequest.setAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE, token.getCollegeId());
			} catch (NumberFormatException ne) {
				String m = String.format("Invalid communication key %s from ip = %s with angel server version = %s .", communicationKey, ip, version);
				throw new InterceptorErrorHandle(message,logger).handle(m);

			}

		} catch (Exception e) {
			 throw  new InterceptorErrorHandle(message,logger).handle(e);
		}
	}
}
