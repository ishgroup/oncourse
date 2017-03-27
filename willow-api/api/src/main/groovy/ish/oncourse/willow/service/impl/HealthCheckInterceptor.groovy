package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import io.bootique.Bootique
import org.apache.cxf.interceptor.Fault
import org.apache.cxf.message.Message
import org.apache.cxf.phase.AbstractPhaseInterceptor
import org.apache.cxf.phase.Phase
import org.eclipse.jetty.server.Response


class HealthCheckInterceptor extends AbstractPhaseInterceptor<Message> {
    
    @Inject
    ShotDownService downService
    
    HealthCheckInterceptor() {
        super(Phase.USER_PROTOCOL) 
    }

    @Override
    void handleMessage(Message message) throws Fault {

        if (downService.killSignalReceived) {
            Response response = message.get('HTTP.RESPONSE') as Response
            response.setStatus(400)
            response.outputStream.close()
        }
    }
}
