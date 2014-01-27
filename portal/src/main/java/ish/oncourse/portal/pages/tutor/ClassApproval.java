package ish.oncourse.portal.pages.tutor;

import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.TutorRole;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.annotations.UserRole;
import ish.oncourse.portal.pages.PageNotFound;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.mail.EmailBuilder;
import ish.oncourse.services.mail.IMailService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.preference.PreferenceControllerFactory;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.Date;
import java.util.List;

@UserRole("tutor")
public class ClassApproval {

    /**
     * Recover emails from address.
     */
    private static final String FROM_EMAIL = "support@ish.com.au";

    @Property
    private boolean approved;

    @Property
    @Persist
    private CourseClass courseClass;

    @Inject
    private ICourseClassService courseClassService;

    @Inject
    private IPortalService portalService;

    @Inject
    private IAuthenticationService authService;

    @Inject
    private ICayenneService cayenneService;

    @Inject
    private IMailService mailService;

    @Inject
    private PreferenceControllerFactory prefFactory;

    @Inject
    private Request request;

    @Property
    private String whyDeclined;

    @Component
    private Form approvalForm;

    @InjectComponent("whyDeclined")
    private TextArea whyDeclinedField;

    @InjectPage
    private PageNotFound pageNotFound;

    Object onActivate() {
        if (courseClass == null)
            return pageNotFound;
        else
            return null;
    }

    Object onActivate(String id) {
        if (id != null && id.length() > 0 && id.matches("\\d+")) {
            List<CourseClass> list = courseClassService.loadByIds(Long.parseLong(id));
            this.courseClass = (!list.isEmpty()) ? list.get(0) : null;
        } else {
            return pageNotFound;
        }
        return null;
    }

    @OnEvent(component = "approvalForm")
    Object submit() {
        String value = request.getParameter("accept");
        if (value != null) {
            accept();
        } else {
            declined();
        }
        return this;
    }


    private void declined() {
        Contact c = authService.getUser();

        if (whyDeclined == null || whyDeclined.length() == 0) {
            approvalForm.recordError(whyDeclinedField, "Please enter your feedback for the class.");
        } else {

            String subject = String.format("Class feedback from tutor %s %s", c.getGivenName(), c.getFamilyName());
            String body = String.format("Tutor %s %s has submitted the following feedback for the class %s-%s '%s'.\n%s", c.getGivenName(),
                    c.getFamilyName(), courseClass.getCourse().getCode(), courseClass.getCode(), courseClass.getCourse().getName(), whyDeclined);

            EmailBuilder email = new EmailBuilder();
            String tutorEmail = c.getEmailAddress();
            email.setFromEmail(tutorEmail != null ? tutorEmail : FROM_EMAIL);
            email.setSubject(subject);
            email.setBody(body);
            email.setToEmails(getTutorFeedbackEmail());
            mailService.sendEmail(email, true);
        }
    }

    void accept() {
        Contact c = authService.getUser();

        if (approved) {
            for (TutorRole t : courseClass.getTutorRoles()) {
                if (t.getTutor().getContact().getId().equals(c.getId())) {
                    ObjectContext newContext = cayenneService.newContext();
                    TutorRole local = newContext.localObject(t);
                    local.setIsConfirmed(approved);
                    if (approved) {
                        local.setConfirmedDate(new Date());
                    }
                    newContext.commitChanges();
                }
            }
        }
    }

    public boolean getIsClassApproved() {

        return portalService.isApproved(authService.getUser(), courseClass);
    }

    public String getDeclineLabel() {
        return getIsClassApproved() ? "Accept" : "Decline";
    }

    private String getTutorFeedbackEmail() {
        College college = courseClass.getCollege();
        PreferenceController prefController = prefFactory.getPreferenceController(college);

        if (prefController.getTutorFeedbackEmail() != null) {
            return prefController.getTutorFeedbackEmail();
        }
        return prefController.getEmailAdminAddress();
    }
}
