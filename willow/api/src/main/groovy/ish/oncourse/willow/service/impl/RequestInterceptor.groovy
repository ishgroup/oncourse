package ish.oncourse.willow.service.impl

import org.apache.cxf.interceptor.Fault
import org.apache.cxf.message.Message
import org.apache.cxf.phase.AbstractPhaseInterceptor
import org.apache.cxf.phase.Phase

import javax.servlet.http.HttpServletRequest

class RequestInterceptor extends AbstractPhaseInterceptor<Message> {
    static final ThreadLocal<String> ThreadLocalXOrigin = new ThreadLocal<String>()

    RequestInterceptor() {
        super(Phase.RECEIVE)
    }

    @Override
    void handleMessage(Message message) throws Fault {
        HttpServletRequest request = message.get('HTTP.REQUEST')
        String host = request.getHeader('X-Origin')
        if (host == null)
            throw new NullPointerException("X-Origin should be not null.")
        ThreadLocalXOrigin.set(host)
    }
}
