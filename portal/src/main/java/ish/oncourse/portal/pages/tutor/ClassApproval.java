package ish.oncourse.portal.pages.tutor;

import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.TutorRole;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.annotations.UserRole;
import ish.oncourse.portal.services.mail.EmailBuilder;
import ish.oncourse.portal.services.mail.IMailService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceControllerFactory;
import ish.persistence.CommonPreferenceController;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

@UserRole("tutor")
public class ClassApproval {

	/**
	 * Recover emails from address.
	 */
	private static final String FROM_EMAIL = "support@ish.com.au";

	@Property
	private boolean approved;

	@Property
	private boolean declined;

	@Property
	@Persist
	private CourseClass courseClass;

	@Inject
	private ICourseClassService courseClassService;

	@Inject
	private IAuthenticationService authService;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IMailService mailService;

	@Inject
	private PreferenceControllerFactory prefFactory;

	@Property
	private String whyDeclined;

	void onActivate(long id) {
		List<CourseClass> list = courseClassService.loadByIds(id);
		this.courseClass = (!list.isEmpty()) ? list.get(0) : null;
	}

	@OnEvent(component = "approvalForm", value = "selected")
	void submitDeclined() {
		this.declined = true;
	}

	@OnEvent(component = "approvalForm", value = "success")
	Object submitted() {
		Contact c = authService.getUser();

		if (approved) {
			for (TutorRole t : courseClass.getTutorRoles()) {
				if (t.getTutor().getContact().getId().equals(c.getId())) {
					ObjectContext newContext = cayenneService.newContext();
					TutorRole local = (TutorRole) newContext.localObject(t.getObjectId(), null);
					local.setIsConfirmed(approved);
					if (approved) {
						local.setConfirmedDate(new Date());
					}
					newContext.commitChanges();
				}
			}
		} else {
			String subject = String.format("Class feedback from tutor %s %s", c.getGivenName(), c.getFamilyName());
			String body = String.format("Tutor %s %s has indicated there is a problem with Class %s '%s'.\n%s", c.getGivenName(),
					c.getFamilyName(), courseClass.getCode(), courseClass.getCourse().getName(), whyDeclined);

			College college = courseClass.getCollege();

			CommonPreferenceController prefController = prefFactory.getPreferenceController(college);

			EmailBuilder email = new EmailBuilder();
			email.setFromEmail(FROM_EMAIL);
			email.setSubject(subject);
			email.setBody(body);
			email.setToEmails(prefController.getEmailAdminAddress());

			mailService.sendEmail(email, true);
		}

		return this;
	}

	public boolean getIsClassApproved() {
		Contact c = authService.getUser();

		for (TutorRole t : courseClass.getTutorRoles()) {
			if (t.getTutor().getContact().getId().equals(c.getId())) {
				return t.getIsConfirmed();
			}
		}

		return false;
	}

	public String getDeclineLabel() {
		return getIsClassApproved() ? "Complain" : "Decline";
	}
}
