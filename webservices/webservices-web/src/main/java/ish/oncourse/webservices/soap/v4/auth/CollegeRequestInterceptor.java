package ish.oncourse.webservices.soap.v4.auth;

import ish.oncourse.model.College;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.util.SoapUtil;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

public class CollegeRequestInterceptor extends AbstractSoapInterceptor {

	private static final Logger LOGGER = Logger.getLogger(CollegeRequestInterceptor.class);
	
	@Inject
	@Autowired
	private ICollegeService collegeService;
	
	public CollegeRequestInterceptor() {
		super(Phase.PRE_INVOKE);
	}

	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		HttpServletRequest req = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
		
		String ip = (req != null) ? req.getRemoteAddr() : "unknown";
		Date time = new Date();
		
		BindingOperationInfo boi = message.getExchange().get(BindingOperationInfo.class);
		
		String securityCode = SoapUtil.getHeader(message, SoapUtil.SECURITY_CODE_HEADER);
		
		Long version = null;
		if (securityCode != null) {
			College college = collegeService.findBySecurityCode(securityCode);
			if (college != null) {
				//assing ish version here
			}
		}

		LOGGER.info(String.format("Invoke %s from %s with version %s at %s.", boi.getName(),  ip, version, time));
	}
}
