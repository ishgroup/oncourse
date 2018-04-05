package ish.oncourse.willow.editor.services.access

import org.apache.cxf.interceptor.Fault
import org.apache.cxf.message.Message
import org.apache.cxf.phase.AbstractPhaseInterceptor
import org.apache.cxf.phase.Phase

import static ish.oncourse.willow.editor.services.access.UserService.*

class PostProcessInterceptor extends AbstractPhaseInterceptor<Message> {


    PostProcessInterceptor() {
        super(Phase.POST_INVOKE)
    }

    @Override
    void handleMessage(Message message) throws Fault {
        ThreadLocalEmail.remove()
        ThreadLocalFirstName.remove()
        ThreadLocalLastName.remove()
        ThreadLocalSystemUser.remove()
    }    
}
