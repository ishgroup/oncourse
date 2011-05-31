package ish.oncourse.webservices.soap.v4.auth;

import ish.oncourse.model.College;
import ish.oncourse.model.CommunicationKey;
import ish.oncourse.model.KeyStatus;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.exception.AuthSoapFault;
import ish.oncourse.webservices.listeners.CollegeSessions;
import ish.oncourse.webservices.util.SoapUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

public class CommunicationKeyInterceptor extends AbstractSoapInterceptor {

	private static final Logger logger = Logger.getLogger(CommunicationKeyInterceptor.class);

	@Inject
	@Autowired
	private ICollegeService collegeService;

	public CommunicationKeyInterceptor() {
		super(Phase.PRE_INVOKE);
	}

	public void handleMessage(SoapMessage message) throws Fault {
		String securityCode = SoapUtil.getSecurityCode(message);

		if (securityCode == null) {
			throw new AuthSoapFault("empty.securityCode");
		}

		College college = collegeService.findBySecurityCode(securityCode);

		if (college == null) {
			throw new AuthSoapFault("invalid.securityCode", securityCode);
		}

		Long communicationKey = null;

		try {
			communicationKey = Long.parseLong(SoapUtil.getCommunicationKey(message));
		} catch (NumberFormatException ne) {
			logger.error("Failed to parse communication key.", ne);
			throw new AuthSoapFault("communicationKey.invalid", communicationKey);
		}

		boolean isValid = false;

		for (CommunicationKey key : college.getCommunicationKeys()) {
			if (communicationKey.equals(key.getKey()) && key.getKeyStatus() == KeyStatus.VALID) {
				isValid = true;
				break;
			}
		}

		if (!isValid) {
			throw new AuthSoapFault("communicationKey.invalid", communicationKey);
		}

		HttpSession session = CollegeSessions.getCollegeSession(communicationKey);

		if (session == null) {
			throw new AuthSoapFault("session.expired.for.key", communicationKey);
		}
		
		SessionToken token = (SessionToken) session.getAttribute(SessionToken.SESSION_TOKEN_KEY);
		
		if (!communicationKey.equals(token.getCommunicationKey())) {
			throw new AuthSoapFault("communicationKey.invalid", communicationKey);
		}

		HttpServletRequest req = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
		req.setAttribute(SoapUtil.REQUESTING_COLLEGE, college);
	}
}
