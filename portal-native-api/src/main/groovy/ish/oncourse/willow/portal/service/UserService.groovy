package ish.oncourse.willow.portal.service

import com.google.inject.Inject
import ish.oncourse.model.User
import ish.oncourse.model.UserAccount
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.portal.auth.SSOCredantials
import org.apache.cayenne.query.ObjectSelect

class UserService {
    
    @Inject
    private ICayenneService cayenneService


    User getUserByEmail(String email) {
        ObjectSelect.query(User).where(User.EMAIL.eq(email)).selectFirst(cayenneService.newContext())
    }

    void updateCredantials(User user, SSOCredantials ssoCredantials) {
        user = cayenneService.newContext().localObject(user)
        UserAccount account = user.getAccount(ssoCredantials.providerType)
        account.setProperty("accessToken", ssoCredantials.accessToken)
        account.setProperty("refreshToken", ssoCredantials.refreshToken)
        user.profilePicture = ssoCredantials.profilePicture
        user.objectContext.commitChanges()
    }

    User createUser(String email) {
        User user = cayenneService.newContext().newObject(User)
        user.email = email
        user.emailVerified = false
        user.objectContext.commitChanges()
        return user
    }

    def void sendVerificationEmail(String email, String sessionToken) {
        //TODO: send signed email tokenized with sessionToken
    }
}
