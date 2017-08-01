package ish.oncourse.webservices.soap.interceptors;

import ish.oncourse.model.College;
import ish.oncourse.model.access.SessionToken;
import ish.oncourse.webservices.util.SoapUtil;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertNotNull;

public class TestCommunicationKeyInterceptor extends AbstractSoapInterceptor {

	public TestCommunicationKeyInterceptor() {
		super(Phase.PRE_INVOKE);
	}
	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		Long communicationKey = Long.parseLong(SoapUtil.getCommunicationKey(message));

		final HttpServletRequest httpRequest = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
		final SessionToken token = new SessionToken(1l, null);
		httpRequest.setAttribute(SessionToken.SESSION_TOKEN_KEY, token);
		httpRequest.setAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE, token.getCollegeId());

		assertNotNull(communicationKey);

	}
}
