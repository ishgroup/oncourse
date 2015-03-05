package ish.oncourse.webservices.soap.v4.interceptors;

import ish.oncourse.webservices.util.SoapUtil;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class TestServerInterceptor extends AbstractPhaseInterceptor<Message> {

    public TestServerInterceptor() {
        super(Phase.READ);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        boolean isRm = SoapUtil.isRMProtocolMessage(message, false);
        System.out.println(isRm);
    }

}
