package ish.oncourse.portal.pages.tutor;

import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.TutorRole;
import ish.oncourse.portal.annotations.UserRole;
import ish.oncourse.portal.pages.PageNotFound;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PortalUtils;
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
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.Date;

@UserRole("tutor")
public class ClassApproval {

    @Property
    private boolean approved;

    @Property
    @Persist
    private boolean messageSent;

    @Property
    @Persist
    private CourseClass courseClass;

    @Inject
    private ICourseClassService courseClassService;

    @Inject
    private IPortalService portalService;

    @Inject
    private ICayenneService cayenneService;

    @Inject
    private IMailService mailService;

    @Inject
    private PreferenceControllerFactory prefFactory;

    @Inject
    private Request request;

    @Inject
    private Messages messages;

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
            this.courseClass = portalService.getCourseClassBy(Long.parseLong(id));
            return this.courseClass == null ? pageNotFound : null;
        } else {
            return pageNotFound;
        }
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
        Contact c = portalService.getContact();

        if (whyDeclined == null || whyDeclined.length() == 0) {
            approvalForm.recordError(whyDeclinedField, messages.get("message-feedbackEmpty"));
        } else {
            if (getTutorFeedbackEmail() == null) {
                approvalForm.recordError(whyDeclinedField, messages.get("message-feedbackEmailNotSet"));
                return;
            }
            String subject = String.format(messages.get("email-subject"), c.getGivenName(), c.getFamilyName());
            String body = messages.format("email-body", c.getGivenName(),
                    c.getFamilyName(), courseClass.getCourse().getCode(), courseClass.getCode(), courseClass.getCourse().getName(), whyDeclined);

            EmailBuilder email = new EmailBuilder();
            String tutorEmail = c.getEmailAddress();
            email.setFromEmail(tutorEmail != null ? tutorEmail : PortalUtils.FROM_EMAIL);
            email.setSubject(subject);
            email.setBody(body);
            email.setToEmails(getTutorFeedbackEmail());
            mailService.sendEmail(email, true);
            messageSent = true;
        }
    }

    @AfterRender
    public void afterRender()
    {
        messageSent = false;
    }

    void accept() {
        Contact c = portalService.getContact();

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

    public boolean isClassApproved() {

        return portalService.isApproved(courseClass);
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
