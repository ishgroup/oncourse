package ish.oncourse.webservices.soap.v4.auth;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import org.apache.ws.security.WSPasswordCallback;
import org.springframework.beans.factory.annotation.Autowired;

public class CommunicationKeyCallback implements CallbackHandler {

	@Inject
	@Autowired
	private Request request;

	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		for (int i = 0; i < callbacks.length; i++) {
			WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
			
			Session session = request.getSession(false);
			
			if (session != null) {
				SessionToken token = (SessionToken) session.getAttribute(SessionToken.SESSION_TOKEN_KEY);
				if (token != null && pc.getIdentifier().equals(token.getSecurityCode())) {
					pc.setPassword(String.valueOf(token.getCommunicationKey()));
				}
			}
		}
	}
}
