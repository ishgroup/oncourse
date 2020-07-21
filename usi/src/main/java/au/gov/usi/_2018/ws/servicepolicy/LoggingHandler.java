package au.gov.usi._2018.ws.servicepolicy;

import com.sun.xml.ws.handler.SOAPMessageContextImpl;
import ish.oncourse.usi.RequestThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import java.io.ByteArrayOutputStream;
import java.util.Set;

public class LoggingHandler implements SOAPHandler<SOAPMessageContextImpl> {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    @Override
    public boolean handleMessage(SOAPMessageContextImpl context) {
        if (context.get("javax.xml.ws.handler.message.outbound") != null && (Boolean) context.get("javax.xml.ws.handler.message.outbound")) {
            RequestThreadLocal.requestEnvelop.set(getEnvelom(context));
        } else {
            RequestThreadLocal.responseEnvelop.set(getEnvelom(context));
        }
        return true;

    }

    @Override
    public boolean handleFault(SOAPMessageContextImpl context) {
        RequestThreadLocal.responseEnvelop.set(getEnvelom(context));
        return true;
    }

    private String  getEnvelom(SOAPMessageContextImpl context) {
        try {
            SOAPMessage msg = context.getMessage();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            msg.writeTo(out);
            return new String(out.toByteArray());
        } catch (Exception e) {
            logger.catching(e);
            return null;
        }
    }

    @Override
    public void close(MessageContext context) {

    }
}
