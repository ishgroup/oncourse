package ish.oncourse.webservices.soap.interceptors;

import ish.oncourse.model.College;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.util.SoapUtil;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

public class CommunicationKeyInterceptor extends AbstractSoapInterceptor {
	
	private static final Logger logger = LogManager.getLogger();

	@Inject
	@Autowired
	private ICollegeService collegeService;

	public CommunicationKeyInterceptor() {
		super(Phase.PRE_INVOKE);
	}

	public void handleMessage(SoapMessage message) throws Fault {
		try {

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

			httpRequest.setAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE, college.getId());
			
		} catch (Exception e) {
			 throw  new InterceptorErrorHandle(message,logger).handle(e);
		}
	}
}
