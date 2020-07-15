package au.gov.usi._2018.ws.servicepolicy;

import com.sun.xml.ws.handler.SOAPMessageContextImpl;
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
        log(context);
        return true;

    }

    @Override
    public boolean handleFault(SOAPMessageContextImpl context) {
        log(context);
        return true;
    }

    private void log(SOAPMessageContextImpl context) {
        try {
            SOAPMessage msg = context.getMessage();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            msg.writeTo(out);
            String strMsg = new String(out.toByteArray());
            logger.warn(strMsg);
        } catch (Exception e) {
            logger.catching(e);
        }
    }

    @Override
    public void close(MessageContext context) {

    }
}
