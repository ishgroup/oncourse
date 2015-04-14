package ish.oncourse.webservices.soap.v4.interceptors;

import ish.oncourse.model.College;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.exception.AuthSoapFault;
import ish.oncourse.webservices.util.SoapUtil;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

public class SecurityCodeInterceptor extends AbstractSoapInterceptor {

	private static final Logger logger = LogManager.getLogger();

	static final String ERROR_TEMPLATE_emptySecurityCode = "empty securityCode from ip = %s with angel server version = %s .";
	static final String ERROR_TEMPLATE_invalidSecurityCode = "Invalid security code: %s from ip = %s with angel server version = %s .";

	@Inject
	@Autowired
	private ICollegeService collegeService;

	@Inject
	@Autowired
	private Request request;

	public SecurityCodeInterceptor() {
		super(Phase.PRE_INVOKE);
	}

	public void handleMessage(SoapMessage message) throws Fault {
		AuthSoapFault fault = null;
		try {
			String securityCode = SoapUtil.getSecurityCode(message);
			final HttpServletRequest httpRequest = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
			final String ip = (httpRequest != null) ? httpRequest.getRemoteAddr() : "unknown";
			final String version = SoapUtil.getAngelVersion(message);
			if (securityCode == null) {
				String m = String.format(ERROR_TEMPLATE_emptySecurityCode, ip, version);
				fault =  new InterceptorErrorHandle(message,logger).handle(m);
				throw fault;
			}
			College college = collegeService.findBySecurityCode(securityCode);
			if (college == null)
			{
				String m = String.format(ERROR_TEMPLATE_invalidSecurityCode, securityCode, ip, version);
				fault =  new InterceptorErrorHandle(message,logger, Level.WARN).handle(m);
				throw fault;
			}
			request.setAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE, college.getId());
		} catch (Exception e) {
			fault = new InterceptorErrorHandle(message,logger).handle(e);
			throw fault;
		}
	}
}
