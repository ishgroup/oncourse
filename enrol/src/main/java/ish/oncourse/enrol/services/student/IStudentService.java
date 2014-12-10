package ish.oncourse.enrol.services.student;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;

import java.util.List;

public interface IStudentService {

	String SHORTLIST_STUDENTS_KEY = "shortlistStudents";
	
	Contact getContact(String firstName, String lastName, String email, boolean isCompany);
	
	Contact getStudentContact(String firstName, String lastName, String email);

	void addStudentToShortlist(Contact student);

	List<Contact> getStudentsFromShortList();

	/**
	 * Removes all the shortlisted students from the session.
	 */
	void clearStudentsShortList();

	List<Long> getContactsIdsFromShortList();

	List<Contact> getContactsByIds(List<Long> ids);
	
}
