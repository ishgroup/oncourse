package ish.oncourse.webservices.soap.v4.auth;

import ish.oncourse.services.system.ICollegeService;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.ws.security.WSPasswordCallback;
import org.springframework.beans.factory.annotation.Autowired;

public class SecurityCodeCallback implements CallbackHandler {
	
	@Inject @Autowired
	private ICollegeService collegeService;
	
	@Override
	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {
		for (int i = 0; i < callbacks.length; i++) {
			WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
		}
	}
}
