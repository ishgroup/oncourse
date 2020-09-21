package ish.oncourse.webservices.util;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class Sent2WillowInterceptor extends AbstractPhaseInterceptor<Message> {
    private boolean sent;

    public Sent2WillowInterceptor() {
        super(Phase.SEND_ENDING);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        if (!sent) {
            sent = true;
        }
    }

    public boolean isSent() {
        return sent;
    }
}
