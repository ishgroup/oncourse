package ish.oncourse.willow.portal.service

import com.google.inject.Inject
import ish.oncourse.api.request.RequestService
import ish.oncourse.model.User
import ish.oncourse.model.UserAccount
import ish.oncourse.services.mail.EmailBuilder
import ish.oncourse.services.mail.SendEmail
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.portal.auth.SSOCredantials
import ish.oncourse.willow.portal.secur.SecurityUtil
import org.apache.cayenne.query.ObjectSelect

import java.time.LocalDateTime

class UserService {
    
    
    public static final String VERIFiCATION_EMAIL_CONTENT = "" +
            "The link vavid for 2 hours:\n" +
            "%s\n" +
            ""
    public static final String VERIFiCATION_EMAIL_SUBJECT = "skillsOnCourse verification email"

    public static final String EMAIL_FROM = "noreply@skillsoncourse.com.au"

    public static final String EMAIL_FROM_NAME = "skillsOnCourse"


    @Inject
    private ICayenneService cayenneService

    @Inject
    private RequestService requestService
    
    
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

    void sendVerificationEmail(String email, String sessionToken, String path = '') {
        String validTo = LocalDateTime.now().plusHours(2).toString()
        String verificationUrl = "$requestService.requestUrl/login$path?email=$email&valid=$validTo"
        verificationUrl = SecurityUtil.enrciptUrl(verificationUrl, sessionToken)
        sendEmail(String.format(VERIFiCATION_EMAIL_CONTENT, verificationUrl), email)
    }
    
    private static sendEmail(String content, String toEmail) {
        EmailBuilder email = new EmailBuilder()
        email.setFromEmail(EMAIL_FROM)
        email.setFromName(EMAIL_FROM_NAME)
        email.setSubject(VERIFiCATION_EMAIL_SUBJECT)
        email.setBody(content)
        email.setToEmails(toEmail)
        SendEmail.valueOf(email, true).send()
    }
}
