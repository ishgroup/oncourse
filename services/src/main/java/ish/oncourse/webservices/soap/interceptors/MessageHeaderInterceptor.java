package ish.oncourse.webservices.soap.interceptors;

import ish.oncourse.webservices.util.SoapUtil;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;

public class MessageHeaderInterceptor extends AbstractSoapInterceptor {

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
}
