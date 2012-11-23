package ish.oncourse.enrol.waitinglist;

import ish.oncourse.enrol.checkout.ContactCredentialsEncoder;
import ish.oncourse.enrol.checkout.contact.AddContactDelegate;
import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.enrol.checkout.contact.ContactEditorDelegate;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Course;
import ish.oncourse.model.WaitingList;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceController;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.Messages;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaitingListController implements AddContactDelegate, ContactEditorDelegate {

	public static final String KEY_ERROR_alreadyAdded = "error-alreadyAdded";

	private IStudentService studentService;
	private ObjectContext objectContext;
	private PreferenceController preferenceController;
	private ContactFieldHelper contactFieldHelper;

	private College college;
	private Course course;


	private ContactCredentials contactCredentials = new ContactCredentials();
	private Contact contact;
	private WaitingList waitingList;

	private Map<String, String> errors = new HashMap<String, String>();
	private List<String> visibleFields;

	private boolean isFillRequiredProperties = false;
	private State state = State.INIT;

	private Messages messages;

	//AddContactDelegate implementation
	@Override
	public void cancelEditing() {

	}

	public void addWaitingList() {
		if (!getErrors().isEmpty())
			return;
		switch (state)
		{
			case ADD_CONTACT:
				saveEditing();
				break;
			case EDIT_CONTACT:
				saveContact();
				break;
			case FINISHED:
				break;
			default:
				throw new IllegalArgumentException();
		}
	}


	@Override
	public void saveEditing() {
		ContactCredentialsEncoder contactCredentialsEncoder = new ContactCredentialsEncoder();
		contactCredentialsEncoder.setContactCredentials(contactCredentials);
		contactCredentialsEncoder.setCollege(college);
		contactCredentialsEncoder.setObjectContext(objectContext);
		contactCredentialsEncoder.setStudentService(studentService);
		contactCredentialsEncoder.encode();
		contact = contactCredentialsEncoder.getContact();

		waitingList.setStudent(contact.getStudent());


		if (contact.getObjectId().isTemporary()) {
			visibleFields = contactFieldHelper.getVisibleFields(contact, false);
			state = State.EDIT_CONTACT;
		} else {
			if (alreadyAdded()) {
				errors.put(KEY_ERROR_alreadyAdded, messages.format(KEY_ERROR_alreadyAdded, contact.getFullName(), course.getName()));
				state = State.ADD_CONTACT;
				return;
			}

			isFillRequiredProperties = !(contactFieldHelper.isAllRequiredFieldFilled(contact));
			if (isFillRequiredProperties) {
				visibleFields = contactFieldHelper.getVisibleFields(contact, false);
				state = State.EDIT_CONTACT;
			} else {
				saveContact();
			}
		}

	}

	private boolean alreadyAdded() {
		if (contact.getStudent().getObjectId().isTemporary())
			return false;
		SelectQuery selectQuery = new SelectQuery(WaitingList.class);

		Expression exp = ExpressionFactory.matchExp(WaitingList.STUDENT_PROPERTY, contact.getStudent());
		exp = exp.andExp(ExpressionFactory.matchExp(WaitingList.COURSE_PROPERTY, course));
		selectQuery.setQualifier(exp);
		List<WaitingList> waitingLists = objectContext.performQuery(selectQuery);
		return !waitingLists.isEmpty();
	}

	public void init() {
		contactFieldHelper = new ContactFieldHelper(preferenceController);

		waitingList = objectContext.newObject(WaitingList.class);
		waitingList.setCollege(college);
		waitingList.setCourse(course);
		state = State.ADD_CONTACT;
	}

	@Override
	public ContactCredentials getContactCredentials() {
		return contactCredentials;
	}

	//ContactEditorDelegate implementation
	@Override
	public Contact getContact() {
		return contact;
	}

	@Override
	public boolean isFillRequiredProperties() {
		return isFillRequiredProperties;
	}

	@Override
	public void saveContact() {
		objectContext.commitChanges();
		state = State.FINISHED;
	}

	@Override
	public List<String> getVisibleFields() {
		return visibleFields;
	}

	@Override
	public void cancelContact() {
		objectContext.rollbackChanges();
	}

	public void addError(String key, String error)
	{
		this.errors.put(key, error);
	}

	@Override
	public void setErrors(Map<String, String> errors) {
		this.errors.clear();
		this.errors.putAll(errors);
	}

	@Override
	public Map<String, String> getErrors() {
		return Collections.unmodifiableMap(errors);
	}

	public void setPreferenceController(PreferenceController preferenceController) {
		this.preferenceController = preferenceController;
	}

	public WaitingList getWaitingList() {
		return waitingList;
	}

	public void setObjectContext(ObjectContext objectContext) {
		this.objectContext = objectContext;
	}

	public void setCollege(College college) {
		this.college = college;
	}

	public void setStudentService(IStudentService studentService) {
		this.studentService = studentService;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public Course getCourse() {
		return course;
	}

	public boolean isFinished() {
		return state == State.FINISHED;
	}

	public boolean isAddContact() {
		return state == State.ADD_CONTACT;
	}

	public boolean isEditContact() {
		return state == State.EDIT_CONTACT;
	}


	public void setMessages(Messages messages) {
		this.messages = messages;
	}

	public ContactFieldHelper getContactFieldHelper()
	{
		return this.contactFieldHelper;
	}

	public ObjectContext getObjectContext() {
		return objectContext;
	}


	public static enum State {
		INIT,
		ADD_CONTACT,
		EDIT_CONTACT,
		FINISHED
	}
}
