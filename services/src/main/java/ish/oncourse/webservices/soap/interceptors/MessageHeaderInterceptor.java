package ish.oncourse.webservices.soap.interceptors;

import ish.oncourse.webservices.util.SoapUtil;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.springframework.beans.factory.annotation.Autowired;

public class MessageHeaderInterceptor extends AbstractSoapInterceptor {

    @Inject
    @Autowired
    private Request request;

    public MessageHeaderInterceptor() {
        super(Phase.POST_PROTOCOL);
    }

    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        unescapeSecurityCode(message);
    }

    private void unescapeSecurityCode(SoapMessage message) {
        String securityCode = SoapUtil.getSecurityCode(message);
        if (securityCode.contains("&")) {
            String newSecurityCode = StringEscapeUtils.unescapeXml(securityCode);
            SoapUtil.changeSecurityCode(message, newSecurityCode);
        }
    }

    protected void changeSecurityCode(SoapMessage message, String newSecurityCode) {
        SoapUtil.changeSecurityCode(message, newSecurityCode);
    }
}
