package ish.oncourse.enrol.services.student;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

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
				.andExp(ExpressionFactory.matchExp(Student.CONTACT_PROPERTY
						+ "." + Contact.GIVEN_NAME_PROPERTY, firstName))
				.andExp(ExpressionFactory.matchExp(Student.CONTACT_PROPERTY
						+ "." + Contact.FAMILY_NAME_PROPERTY, lastName))
				.andExp(ExpressionFactory.matchExp(Student.CONTACT_PROPERTY
						+ "." + Contact.EMAIL_ADDRESS_PROPERTY, email));
		SelectQuery query = new SelectQuery(Student.class, qualifier);
		List<Student> results = cayenneService.sharedContext().performQuery(
				query);
		return results.isEmpty() ? null : results.get(0);
	}

	public void addStudentToShortlist(Contact student) {
		Session session = request.getSession(false);
		List<Contact> students = (List<Contact>) session
				.getAttribute("shortlistStudents");
		if (students == null) {
			students = new ArrayList<Contact>();
		}
		students.add(student);
		session.setAttribute("shortlistStudents", students);
	}

}
