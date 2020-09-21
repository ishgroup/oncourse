package ish.oncourse.webservices.security;

import ish.oncourse.webservices.util.SoapUtil;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.binding.soap.interceptor.SoapPreProtocolOutInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;

public class AddSecurityCodeInterceptor extends AbstractSoapInterceptor {
	
	private String securityCode;
	private Long communicationKey;

	public AddSecurityCodeInterceptor(String securityCode, Long communicationKey) {
		super(Phase.WRITE);
		addAfter(SoapPreProtocolOutInterceptor.class.getName());
		this.securityCode = securityCode;
		this.communicationKey = communicationKey;
	}

	public void handleMessage(SoapMessage message) throws Fault {
		if (securityCode != null) {
			SoapUtil.addSecurityCode(message, securityCode);
		}
		
		if (communicationKey != null) {
			SoapUtil.addCommunicationKey(message, communicationKey);
		}
	}
}
