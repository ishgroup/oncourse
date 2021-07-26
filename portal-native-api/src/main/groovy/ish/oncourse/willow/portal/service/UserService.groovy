package ish.oncourse.willow.portal.service

import com.google.inject.Inject
import ish.oncourse.model.User
import ish.oncourse.services.persistence.ICayenneService
import org.apache.cayenne.query.ObjectSelect

class UserService {
    
    @Inject
    private ICayenneService cayenneService


    User getUserByEmail(String email) {
        ObjectSelect.query(User).where(User.EMAIL.eq(email)).selectFirst(cayenneService.newContext())
    } 
}
