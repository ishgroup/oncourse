package ish.oncourse.webservices.soap.auth;

import java.io.IOException;

import javax.annotation.Resource;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceContext;

import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.ws.security.WSPasswordCallback;

public class CommunicationKeyCallback implements CallbackHandler {

	@Resource
	private WebServiceContext webServiceContext;

	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		for (int i = 0; i < callbacks.length; i++) {
			WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
			
			HttpSession session = getHttpSession();
			
			if (session != null) {
				SessionToken token = (SessionToken) session.getAttribute(SessionToken.SESSION_TOKEN_KEY);
				if (token != null && pc.getIdentifier().equals(token.getSecurityCode())) {
					pc.setPassword(String.valueOf(token.getCommunicationKey()));
				}
			}
		}
	}

	private HttpSession getHttpSession() {
		HttpServletRequest req = (HttpServletRequest) webServiceContext.getMessageContext().get(
				AbstractHTTPDestination.HTTP_REQUEST);
		return req.getSession(false);
	}
}
