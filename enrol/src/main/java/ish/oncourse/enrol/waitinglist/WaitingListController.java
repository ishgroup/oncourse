package ish.oncourse.enrol.waitinglist;

import ish.oncourse.enrol.checkout.ContactCredentialsEncoder;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Course;
import ish.oncourse.model.CustomField;
import ish.oncourse.model.WaitingList;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import java.util.List;

public class WaitingListController extends AContactController {

	public static final String KEY_ERROR_alreadyAdded = "message-alreadyAdded";

    private Course course;
    private WaitingList waitingList;

	public void addWaitingList() {
		if (!getErrors().isEmpty())
			return;
		switch (getState())
		{
			case ADD_CONTACT:
				addContact();
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
	public void addContact() {
		ContactCredentialsEncoder contactCredentialsEncoder = new ContactCredentialsEncoder();
		contactCredentialsEncoder.setContactCredentials(getContactCredentials());
		contactCredentialsEncoder.setCollege(getCollege());
		contactCredentialsEncoder.setObjectContext(getObjectContext());
		contactCredentialsEncoder.setStudentService(getStudentService());
		contactCredentialsEncoder.encode();

        Contact contact = contactCredentialsEncoder.getContact();
        setContact(contact);
		waitingList.setStudent(contact.getStudent());


		if (getContact().getObjectId().isTemporary()) {
			setVisibleFields(getContactFieldHelper().getVisibleFields(contact, false));
			setState(State.EDIT_CONTACT);
		} else {
			if (alreadyAdded()) {
				addError(KEY_ERROR_alreadyAdded, getMessages().format(KEY_ERROR_alreadyAdded, contact.getFullName(), course.getName()));
				setState(State.ADD_CONTACT);
				return;
			}

			setFillRequiredProperties(!(getContactFieldHelper().isAllRequiredFieldFilled(contact)));
			if (isFillRequiredProperties()) {
                setVisibleFields(getContactFieldHelper().getVisibleFields(contact, true));
				setState(State.EDIT_CONTACT);
			} else {
				saveContact();
			}
		}

	}

	private boolean alreadyAdded() {
		if (getContact().getStudent().getObjectId().isTemporary())
			return false;
		SelectQuery selectQuery = new SelectQuery(WaitingList.class);

		Expression exp = ExpressionFactory.matchExp(WaitingList.STUDENT_PROPERTY, getContact().getStudent());
		exp = exp.andExp(ExpressionFactory.matchExp(WaitingList.COURSE_PROPERTY, course));
		selectQuery.setQualifier(exp);
		List<WaitingList> waitingLists = getObjectContext().performQuery(selectQuery);
		return !waitingLists.isEmpty();
	}

	public void init() {

        super.init();
        waitingList = getObjectContext().newObject(WaitingList.class);
        waitingList.setCollege(getCollege());
        waitingList.setCourse(course);
        waitingList.setPotentialStudents(1);
    }

	public WaitingList getWaitingList() {
		return waitingList;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public Course getCourse() {
		return course;
	}

	@Override
	public List<CustomField> getCustomFields() {
		return getContact().getCustomFields();
	}
}
