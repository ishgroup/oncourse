package ish.oncourse.webservices.usi.interceptors;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.soap.SOAPMessage;

public class UnexpectedResponseNodeInterceptor extends AbstractPhaseInterceptor<SoapMessage> {

    private static final Logger logger = LogManager.getLogger();

    public UnexpectedResponseNodeInterceptor() {
        super(Phase.USER_PROTOCOL);
    }

    @Override
    public void handleMessage(SoapMessage message) throws Fault {

        try {
            SOAPMessage msg = message.getContent(SOAPMessage.class);
            NodeList allBodyNodes = msg.getSOAPBody().getElementsByTagName("MemberNames");

            while (allBodyNodes.getLength() > 0) {
                Node nodeToRemove = allBodyNodes.item(0);
                nodeToRemove.getParentNode().removeChild(nodeToRemove);
            }
        } catch (Exception ex) {
            logger.error("Can not remove excess node <MemberNames> in inbound SOAP message, unexpected element error will be thrown by CXF client.");
        }

    }
}
