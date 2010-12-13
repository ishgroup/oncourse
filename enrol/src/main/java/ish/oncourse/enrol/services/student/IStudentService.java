package ish.oncourse.enrol.services.student;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;

import java.util.List;

public interface IStudentService {

    String SHORTLIST_STUDENTS_KEY = "shortlistStudents";

	Student getStudent(String firstName, String lastName, String email);
	void addStudentToShortlist(Contact student);
    List<Contact>getStudentsFromShortList();
    List<Long> getContactsIdsFromShortList();
    List<Contact>getContactsByIds(List<Long>ids);

}
