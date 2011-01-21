package ish.oncourse.webservices.soap.v4.auth;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import org.springframework.beans.factory.annotation.Autowired;

public class CommunicationKeyInterceptor extends AbstractSoapInterceptor {

	@Inject
	@Autowired
	private Request request;
	
	public CommunicationKeyInterceptor() {
		super(Phase.PRE_INVOKE);
	}

	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		Session session = request.getSession(false);
		
		if (session != null) {
			SessionToken token = (SessionToken) session.getAttribute(SessionToken.SESSION_TOKEN_KEY);
			/*
			if (token != null && pc.getIdentifier().equals(token.getSecurityCode())) {
				pc.setPassword(String.valueOf(token.getCommunicationKey()));
			}*/
		}
	}
}
