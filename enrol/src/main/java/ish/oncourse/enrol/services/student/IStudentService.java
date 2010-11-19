package ish.oncourse.enrol.services.student;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;

public interface IStudentService {

	Student getStudent(String firstName, String lastName, String email);
	void addStudentToShortlist(Contact student);
}
