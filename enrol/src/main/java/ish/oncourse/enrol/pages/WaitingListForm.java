package ish.oncourse.enrol.pages;

import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Course;
import ish.oncourse.model.WaitingList;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

/**
 * Java class for WaitingListForm.tml.
 * 
 * @author ksenia
 * 
 */
public class WaitingListForm {

	@Inject
	private Request request;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICourseService courseService;

	@Inject
	private IStudentService studentService;

	@Persist
	@Property
	private Course course;

	@Persist
	@Property
	private Contact contact;

	@Persist
	@Property
	private WaitingList waitingList;

	@Property
	private boolean submittedSuccessfully;

	@InjectComponent
	@Property
	private Form waitingListForm;

	@InjectComponent
	private Field waitlistFirstName;

	@InjectComponent
	private Field waitlistLastName;

	@InjectComponent
	private Field waitlistEmail;

	@InjectComponent
	private Zone waitingListFormZone;

	@SetupRender
	void beforeRender() {
		String courseId = request.getParameter("courseId");
		List<Course> result = courseService.loadByIds(courseId);
		if (!result.isEmpty()) {
			course = result.get(0);
		}

		contact = new Contact();
		waitingList = new WaitingList();
	}

	@OnEvent(component = "waitingListForm", value = "validate")
	void validate() {
		String firstNameErrorMessage = contact.validateGivenName();
		if (firstNameErrorMessage != null) {
			waitingListForm.recordError(waitlistFirstName, firstNameErrorMessage);
		}
		String lastNameErrorMessage = contact.validateFamilyName();
		if (lastNameErrorMessage != null) {
			waitingListForm.recordError(waitlistLastName, lastNameErrorMessage);
		}
		String emailErrorMessage = contact.validateEmail();
		if (emailErrorMessage != null) {
			waitingListForm.recordError(waitlistEmail, emailErrorMessage);
		}
	}

	@OnEvent(component = "waitingListForm", value = "submit")
	Object submittedSuccessfully() {
		if (!waitingListForm.getHasErrors()) {

			ObjectContext context = cayenneService.newContext();
			College college = (College) context.localObject(webSiteService.getCurrentCollege().getObjectId(), null);

			Contact studentContact = studentService.getStudentContact(contact.getGivenName(), contact.getFamilyName(),
					contact.getEmailAddress());
			if (studentContact != null) {
				studentContact = (Contact) context.localObject(studentContact.getObjectId(), null);
				if (studentContact.getStudent() == null) {
					studentContact.createNewStudent();
				}
			} else {
				context.registerNewObject(contact);
				contact.setCollege(college);
				studentContact = contact;
				studentContact.createNewStudent();
			}
			//this check added to prevent #13048.
			if (waitingList.getObjectId() == null) {
				context.registerNewObject(waitingList);
			} else {
				waitingList = (WaitingList) context.localObject(waitingList.getObjectId(), null);
			}
			waitingList.setCollege(college);
			waitingList.setStudent(studentContact.getStudent());
			waitingList.setCourse((Course) context.localObject(course.getObjectId(), null));
			context.commitChanges();
			submittedSuccessfully = true;
		}

		if (request.isXHR()) {
			return waitingListFormZone.getBody();
		}
		return this;

	}
}
