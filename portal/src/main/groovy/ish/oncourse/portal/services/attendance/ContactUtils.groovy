package ish.oncourse.portal.services.attendance

import ish.oncourse.model.Contact
import ish.oncourse.model.Session
import ish.oncourse.model.Student

class ContactUtils {

	static List<Student> getStudentsBy(List<Contact> contacts) {
		contacts.findAll { it.student != null }.collect { it.student }
	}


	static List<Contact> getChildrenForSession(List<Contact> children, Session session) {
		return children.intersect(session.attendances*.student*.contact)
	}
	
}
