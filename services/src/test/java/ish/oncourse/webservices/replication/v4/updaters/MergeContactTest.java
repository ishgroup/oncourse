package ish.oncourse.webservices.replication.v4.updaters;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;

public class MergeContactTest extends ServiceTest {
	
	@Before
    public void setupDataSet() throws Exception {
        initTest("ish.oncourse.webservices.services", StringUtils.EMPTY, ReplicationTestModule.class);
    }
	
	@Test
	public void testContactStudentMergeFail() {
		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		List<Contact> data = createTwoContacts(objectContext);
		Contact contact1 = data.get(0);
		Contact contact2 = data.get(1);
		Student student1 = createFirstStudent(objectContext);
		contact1.setStudent(student1);
		assertNotNull("contact 1 linked with the student 1", contact1.getStudent());
		assertNotNull("student 1 linked with the contact 1", student1.getContact());
		assertNull("contact 2 have no link to students", contact2.getStudent());
		//contact2.setStudent(student1);
		//use the orthodox setter logic
		contact2.setToOneTarget(Contact.STUDENT_PROPERTY, student1, true);
		assertNotNull("contact 2 linked with the student 1", contact2.getStudent());
		assertTrue("Contact 2 have link to student 1", contact2.getStudent().equals(student1));
		assertNotNull("student 1 linked with the contact 2", student1.getContact());
		assertTrue("Student 1 have link with the contact 2", student1.getContact().equals(contact2));
		//Next 2 asserts should fail because ToOneTarget should handle both sides relationship
		assertNotNull("contact 1 have no link to students", contact1.getStudent());
		assertTrue("Student 1 have link with the contact 2", contact1.getStudent().getContact().equals(contact2));
		
		objectContext.rollbackChanges();
	}
	
	@Test
	public void testContactStudentMergeWithCheckRelationShip() {
		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		List<Contact> data = createTwoContacts(objectContext);
		Contact contact1 = data.get(0);
		Contact contact2 = data.get(1);
		Student student1 = createFirstStudent(objectContext);
		contact1.setStudent(student1);
		assertNotNull("contact 1 linked with the student 1", contact1.getStudent());
		assertNotNull("student 1 linked with the contact 1", student1.getContact());
		assertNull("contact 2 have no link to students", contact2.getStudent());
		//contact2.setStudent(student1);
		//use the correct setter logic
		Contact.setObjectToOneTargetWithCheck(Contact.STUDENT_PROPERTY, student1, true, contact2);
		assertNotNull("contact 2 linked with the student 1", contact2.getStudent());
		assertTrue("Contact 2 have link to student 1", contact2.getStudent().equals(student1));
		assertNotNull("student 1 linked with the contact 2", student1.getContact());
		assertTrue("Student 1 have link with the contact 2", student1.getContact().equals(contact2));
		assertNull("contact 1 have no link to students", contact1.getStudent());
		
		objectContext.rollbackChanges();
	}
	
	@Test
	public void testStudentContactMerge() {
		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		List<Contact> data = createTwoContacts(objectContext);
		Contact contact1 = data.get(0);
		Contact contact2 = data.get(1);
		Student student1 = createFirstStudent(objectContext);
		contact1.setStudent(student1);
		assertNotNull("contact 1 linked with the student 1", contact1.getStudent());
		assertNotNull("student 1 linked with the contact 1", student1.getContact());
		assertNull("contact 2 have no link to students", contact2.getStudent());
		//use the orthodox setter logic
		student1.setContact(contact2);
		assertNotNull("contact 2 linked with the student 1", contact2.getStudent());
		assertTrue("Contact 2 have link to student 1", contact2.getStudent().equals(student1));
		assertNotNull("student 1 linked with the contact 2", student1.getContact());
		assertTrue("Student 1 have link with the contact 2", student1.getContact().equals(contact2));
		assertNull("contact 1 have no link to students", contact1.getStudent());
		
		objectContext.rollbackChanges();
	}
	
	private List<Contact> createTwoContacts(final ObjectContext objectContext) {
		Contact contact1 = objectContext.newObject(Contact.class);
		contact1.setAngelId(1L);
		Contact contact2 = objectContext.newObject(Contact.class);
		contact1.setAngelId(2L);
		assertNull("contact 1 have no link to student", contact1.getStudent());
		assertNull("contact 1 have no link to tutor", contact1.getTutor());
		assertNull("contact 2 have no link to student", contact2.getStudent());
		assertNull("contact 2 have no link to tutor", contact2.getTutor());
		List<Contact> result = new ArrayList<Contact>(2);
		result.add(contact1);
		result.add(contact2);
		return result;
	}
	
	private Student createFirstStudent(final ObjectContext objectContext) {
		Student student = objectContext.newObject(Student.class);
		student.setAngelId(1L);
		assertNull("student1 have no link to contact", student.getContact());
		return student;
	}

}
