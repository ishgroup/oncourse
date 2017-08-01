package ish.oncourse.webservices.soap.interceptors;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;

public class TestSecurityCodeInterceptor extends AbstractSoapInterceptor {
	@Override
	public void handleMessage(SoapMessage message) throws Fault {
	}
}
