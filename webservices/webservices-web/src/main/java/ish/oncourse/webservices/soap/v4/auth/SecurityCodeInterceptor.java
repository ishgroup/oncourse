package ish.oncourse.webservices.soap.v4.auth;

import ish.oncourse.services.system.ICollegeService;

import org.w3c.dom.Element;
import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.helpers.DOMUtils;
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

	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		
		Header securityCode = getHeader(message, CollegeCredentials.SECURITY_CODE);
		
		Element el = (Element) securityCode.getObject();
        Element child = DOMUtils.getFirstElement(el);
		
		/*
		if (securityCodeList != null && securityCodeList.size() > 0) {
			String securityCode = securityCodeList.get(0);
			College college = collegeService.findBySecurityCode(securityCode);
			if (college == null) {
				// College failed to authenticate itself.
			}
		} else {

		}*/
	}

	private static Header getHeader(SoapMessage message, String localName) {
		for (Header h : message.getHeaders()) {
            QName n = h.getName();
            if (n.getLocalPart().equals(localName)) {
                return h;
            }
        }
		
		return null;
	}
}
