package ish.oncourse.webservices.soap.v4.auth;

import javax.servlet.http.HttpSession;

import ish.oncourse.model.College;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.listeners.CollegeSessions;
import ish.oncourse.webservices.util.SoapUtil;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.springframework.beans.factory.annotation.Autowired;

public class CommunicationKeyInterceptor extends AbstractSoapInterceptor {

	@Inject
	@Autowired
	private ICollegeService collegeService;
	
	@Inject
	@Autowired
	private Request request;

	public CommunicationKeyInterceptor() {
		super(Phase.PRE_INVOKE);
	}

	public void handleMessage(SoapMessage message) throws Fault {
		String securityCode = SoapUtil.getSecurityCode(message);

		if (securityCode == null) {
			throw new AuthenticationFailureException("empty.securityCode");
		}

		College college = collegeService.findBySecurityCode(securityCode);

		if (college == null) {
			throw new AuthenticationFailureException("invalid.securityCode", securityCode);
		}

		String communicationKey = SoapUtil.getCommunicationKey(message);

		if (communicationKey == null || !String.valueOf(college.getCommunicationKey()).equals(communicationKey)) {
			throw new AuthenticationFailureException("communicationKey.invalid", communicationKey);
		}
		
		HttpSession session = CollegeSessions.getCollegeSession(college.getId());
		
		if (session == null) {
			throw new AuthenticationFailureException("session.expired.for.key", communicationKey);
		}
		
		request.setAttribute(SoapUtil.REQUESTING_COLLEGE, college);
	}
}
