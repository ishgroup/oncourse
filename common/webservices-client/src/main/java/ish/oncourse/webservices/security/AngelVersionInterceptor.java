package ish.oncourse.webservices.security;

import ish.oncourse.webservices.util.SoapUtil;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.binding.soap.interceptor.SoapPreProtocolOutInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;

/**
 * Cxf client side interceptor, which adds angelVersion to as soap header.
 * @author anton
 *
 */
public class AngelVersionInterceptor extends AbstractSoapInterceptor {
	
	private String angelVersion;

	public AngelVersionInterceptor(String angelVersion) {
		super(Phase.WRITE);
		addAfter(SoapPreProtocolOutInterceptor.class.getName());
		this.angelVersion = angelVersion;
	}

	public void handleMessage(SoapMessage message) throws Fault {
		if(angelVersion != null) {
			SoapUtil.addAngelVersion(message, angelVersion);
		}
	}
}
