package ish.oncourse.enrol.services.student;

import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

public class StudentService implements IStudentService {

	private static final Logger LOGGER = Logger.getLogger(StudentService.class);

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private Request request;

	public Student getStudent(String firstName, String lastName, String email) {
		College currentCollege = webSiteService.getCurrentCollege();
		Expression qualifier = ExpressionFactory
				.matchExp(Student.COLLEGE_PROPERTY, currentCollege)
				.andExp(ExpressionFactory.matchExp(Student.CONTACT_PROPERTY + "."
						+ Contact.GIVEN_NAME_PROPERTY, firstName))
				.andExp(ExpressionFactory.matchExp(Student.CONTACT_PROPERTY + "."
						+ Contact.FAMILY_NAME_PROPERTY, lastName))
				.andExp(ExpressionFactory.matchExp(Student.CONTACT_PROPERTY + "."
						+ Contact.EMAIL_ADDRESS_PROPERTY, email));
		SelectQuery query = new SelectQuery(Student.class, qualifier);
		List<Student> results = cayenneService.sharedContext().performQuery(query);
		return results.isEmpty() ? null : results.get(0);
	}

	public void addStudentToShortlist(Contact student) {
		List<Long> studentIds = getContactsIdsFromShortList();
		studentIds.add(student.getId());
		request.getSession(false).setAttribute(SHORTLIST_STUDENTS_KEY, studentIds);
	}

	public List<Long> getContactsIdsFromShortList() {
		Session session = request.getSession(false);
		List<Long> studentIds = (List<Long>) session.getAttribute(SHORTLIST_STUDENTS_KEY);
		if (studentIds == null) {
			studentIds = new ArrayList<Long>();
		}
		return studentIds;
	}

	public List<Contact> getStudentsFromShortList() {
		List<Contact> contacts = getContactsByIds(getContactsIdsFromShortList());
		List<Ordering> orderings = new ArrayList<Ordering>();
		orderings.add(new Ordering(Contact.GIVEN_NAME_PROPERTY, SortOrder.ASCENDING));
		orderings.add(new Ordering(Contact.FAMILY_NAME_PROPERTY, SortOrder.ASCENDING));
		Ordering.orderList(contacts, orderings);
		return contacts;
	}

	public List<Contact> getContactsByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}

		SelectQuery q = new SelectQuery(Contact.class, ExpressionFactory.inDbExp(
				Contact.ID_PK_COLUMN, ids));

		return cayenneService.sharedContext().performQuery(q);
	}

	/**
	 * {@inheritDoc} <br/>
	 * Sets null to the session attribute with name
	 * {@value IStudentService#SHORTLIST_STUDENTS_KEY}.
	 * 
	 * @see ish.oncourse.enrol.services.student.IStudentService#clearStudentsShortList()
	 */
	@Override
	public void clearStudentsShortList() {
		Session session = request.getSession(false);
		if (session != null) {
			session.setAttribute(SHORTLIST_STUDENTS_KEY, null);
		}
	}

}
