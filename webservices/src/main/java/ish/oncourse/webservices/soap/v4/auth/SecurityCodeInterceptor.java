package ish.oncourse.webservices.soap.v4.auth;

import ish.oncourse.model.College;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.exception.AuthSoapFault;
import ish.oncourse.webservices.util.SoapUtil;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

public class SecurityCodeInterceptor extends AbstractSoapInterceptor {

	@Inject
	@Autowired
	private ICollegeService collegeService;


	public SecurityCodeInterceptor() {
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
	}
}
