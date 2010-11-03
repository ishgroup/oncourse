package ish.oncourse.enrol.components;

import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class EnrolmentContactEntry {
	
	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IStudentService studentService;

	@Inject
	private Request request;

	@InjectComponent
	private Zone addStudentBlock;

	@Property
	private boolean needMoreInfo;

	@Property
	private boolean hasContact;

	@Property
	private boolean hasErrors;

	@Parameter
	@Property
	private boolean showConcessionsArea;
	@Persist
	private ObjectContext context;

	@Property
	@Persist
	private Contact contact;

	@SetupRender
	void beforeRender() {
		context = cayenneService.newContext();

		contact = context.newObject(Contact.class);
		College college = webSiteService.getCurrentCollege();
		contact.setCollege((College) context.localObject(college.getObjectId(),
				college));
	}

	public String getAddStudentBlockClass() {
		List<Student> shortlistStudents = (List<Student>) request.getSession(false)
				.getAttribute("shortlistStudents");
		return (shortlistStudents == null || shortlistStudents.isEmpty() || needMoreInfo) ? "show"
				: "collapse";
	}

	public boolean isNewStudent() {
		return contact.getPersistenceState() == PersistenceState.NEW;
	}

	@OnEvent(component = "addStudentAction", value = "selected")
	void onSelectedFromAddStudentAction() {
		Student student = studentService.getStudent(contact.getGivenName(),
				contact.getFamilyName(), contact.getEmailAddress());
		if (student != null) {
			contact = student.getContact();
		}
		hasContact = true;
	}

	@OnEvent(component = "shortDetailsForm", value = "success")
	Block refreshContactEntry() {
		return addStudentBlock.getBody();
	}

}
