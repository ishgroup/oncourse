package ish.oncourse.willow.editor.services.access

import com.google.inject.Singleton
import ish.oncourse.model.SystemUser
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Response

@Singleton
class UserService {
    
    static final ThreadLocal<String> ThreadLocalFirstName = new ThreadLocal<String>()
    static final ThreadLocal<String> ThreadLocalLastName= new ThreadLocal<String>()
    static final ThreadLocal<String> ThreadLocalEmail= new ThreadLocal<String>()
    static final ThreadLocal<SystemUser> ThreadLocalSystemUser= new ThreadLocal<SystemUser>()


    SystemUser getSystemUser() {
        ThreadLocalSystemUser.get()
    }
    
    String getUserFirstName() {
        ThreadLocalFirstName.get()
    }

    String getUserLastName() {
        ThreadLocalLastName.get()
    }

    String getUserEmail() {
        ThreadLocalEmail.get()
    }

    void setSystemUser(SystemUser systemUser) {
        ThreadLocalSystemUser.set(systemUser)
    }

    void setUserFirstName(String firstName) {
        ThreadLocalFirstName.set(firstName)
    }

    void setUserLastName(String firstName) {
        ThreadLocalLastName.set(firstName)
    }

    void setUserEmail(String firstName) {
        ThreadLocalEmail.set(firstName)
    }
}
