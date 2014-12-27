package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ContextUtils;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.replication.builders.WillowStubBuilderTest;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang.StringUtils;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class MergeContactTest extends ServiceTest {
	
	@Before
    public void setupDataSet() throws Exception {
		final Map<String, Boolean> params = new HashMap<>(3);
		params.put(ContextUtils.SHOULD_CREATE_TABLES, true);
		params.put(ContextUtils.SHOULD_CREATE_PK_SUPPORT, true);
		params.put(ContextUtils.SHOULD_CREATE_FK_CONSTRAINTS, false);
		initTestWithParams(params, "ish.oncourse.webservices.services", StringUtils.EMPTY, ReplicationTestModule.class);
        
        InputStream st = WillowStubBuilderTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/replication/v4/updaters/MergeContactTest.xml");
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

        DataSource onDataSource = getDataSource("jdbc/oncourse");
        DatabaseConnection dbConnection = new DatabaseConnection(onDataSource.getConnection(), null);
        dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

        DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
    }
	
	@Test
	public void testContactStudentMergeFail() {
		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		List<Contact> data = getFirstTwoContacts(objectContext);
		Contact contact1 = data.get(0);
		Contact contact2 = data.get(1);
		Student student1 = contact1.getStudent();
		objectContext.commitChanges();
		assertNotNull("contact 1 linked with the student 1", contact1.getStudent());
		assertNotNull("student 1 linked with the contact 1", student1.getContact());
		assertNull("contact 2 have no link to students", contact2.getStudent());
		try {
			contact2.setStudent(student1);
			boolean commitFailed = false;
			try {
				objectContext.commitChanges();
			} catch (Exception e) {
				commitFailed = true;
			}
			assertFalse(commitFailed);
			
			assertNotNull("contact 2 linked with the student 1", contact2.getStudent());
			assertTrue("Contact 2 have link to student 1", contact2.getStudent().equals(student1));
			assertNotNull("student 1 linked with the contact 2", student1.getContact());
			assertTrue("Student 1 have link with the contact 2", student1.getContact().equals(contact2));
			contact1 = (Contact) objectContext.performQuery(
				new SelectQuery(Contact.class, ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 2L))).get(0);
			assertNull("contact 1 have no link to students", contact1.getStudent());
		} finally {
			objectContext.rollbackChanges();
		}
	}
	
	@Test
	public void testContactsStudentsMergeFail() {
		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		List<Contact> data = getSecondTwoContacts(objectContext);
		Contact contact1 = data.get(0);
		Contact contact2 = data.get(1);
		Student student1 = contact1.getStudent();
		Student student2 = contact2.getStudent();
		objectContext.commitChanges();
		assertNotNull("contact 1 linked with the student 1", contact1.getStudent());
		assertNotNull("student 1 linked with the contact 1", student1.getContact());
		assertNotNull("contact 2 linked with the student 2", contact2.getStudent());
		assertNotNull("student 2 linked with the contact 2", student2.getContact());
		
		try {
			contact2.setStudent(student1);
			boolean commitFailed = false;
			contact1.setStudent(student2);
			try {
				objectContext.commitChanges();
			} catch (Exception e) {
				commitFailed = true;
			}
			assertFalse(commitFailed);
			//contact 2 part check
			assertNotNull("contact 2 linked with the student 1", contact2.getStudent());
			assertTrue("Contact 2 have link to student 1", contact2.getStudent().equals(student1));
			assertNotNull("student 1 linked with the contact 2", student1.getContact());
			assertTrue("Student 1 have link with the contact 2", student1.getContact().equals(contact2));
			contact1 = (Contact) objectContext.performQuery(
					new SelectQuery(Contact.class, ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 3L))).get(0);
			assertNotNull("contact 1 have no link to students", contact1.getStudent());
			assertTrue("Student 1 have link with the contact 2 only", contact1.getStudent().getContact().equals(contact1));
			assertTrue("Student 1 have link with the contact 2", contact1.getStudent().equals(student2));
		} finally {
			objectContext.rollbackChanges();
		}
	}
	
	@Test
	public void testContactsStudentsMergeWithCheckRelationShip() {
		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		List<Contact> data = getSecondTwoContacts(objectContext);
		Contact contact1 = data.get(0);
		Contact contact2 = data.get(1);
		Student student1 = contact1.getStudent();
		Student student2 = contact2.getStudent();
		objectContext.commitChanges();
		assertNotNull("contact 1 linked with the student 1", contact1.getStudent());
		assertNotNull("student 1 linked with the contact 1", student1.getContact());
		assertNotNull("contact 2 linked with the student 2", contact2.getStudent());
		assertNotNull("student 2 linked with the contact 2", student2.getContact());

		try {
			//contact2.setStudent(student1);
			Contact.setObjectToOneTargetWithCheck(Contact.STUDENT_PROPERTY, student1, true, contact2);
			assertNotNull("contact 2 linked with the student 1", contact2.getStudent());
			assertTrue("Contact 2 have link to student 1", contact2.getStudent().equals(student1));
			assertNotNull("student 1 linked with the contact 2", student1.getContact());
			assertTrue("Student 1 have link with the contact 2", student1.getContact().equals(contact2));
			assertNull("contact 1 have no more link to students", contact1.getStudent());
		
			//contact1.setStudent(student2);
			Contact.setObjectToOneTargetWithCheck(Contact.STUDENT_PROPERTY, student2, true, contact1);
			objectContext.commitChanges();
			assertNotNull("contact 2 linked with the student 1", contact2.getStudent());
			assertTrue("Contact 2 have link to student 1", contact2.getStudent().equals(student1));
			assertNotNull("student 1 linked with the contact 2", student1.getContact());
			assertTrue("Student 1 have link with the contact 2", student1.getContact().equals(contact2));
			
			assertNotNull("contact 1 have no link to students", contact1.getStudent());
			assertTrue("Student 1 have link with the contact 2", contact1.getStudent().getContact().equals(contact1));
			assertTrue("Student 1 have link with the contact 2", contact1.getStudent().equals(student2));
		} finally {
			objectContext.rollbackChanges();
		}
	}
	
	@Test
	public void testContactStudentMergeWithCheckRelationShip() {
		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		List<Contact> data = getFirstTwoContacts(objectContext);
		Contact contact1 = data.get(0);
		Contact contact2 = data.get(1);
		Student student1 = contact1.getStudent();
		objectContext.commitChanges();
		assertNotNull("contact 1 linked with the student 1", contact1.getStudent());
		assertNotNull("student 1 linked with the contact 1", student1.getContact());
		assertNull("contact 2 have no link to students", contact2.getStudent());

		try {
			//contact2.setStudent(student1);
			Contact.setObjectToOneTargetWithCheck(Contact.STUDENT_PROPERTY, student1, true, contact2);
			assertNotNull("contact 2 linked with the student 1", contact2.getStudent());
			assertTrue("Contact 2 have link to student 1", contact2.getStudent().equals(student1));
			assertNotNull("student 1 linked with the contact 2", student1.getContact());
			assertTrue("Student 1 have link with the contact 2", student1.getContact().equals(contact2));
			assertNull("contact 1 have no link to students", contact1.getStudent());
			contact1 = (Contact) objectContext.performQuery(
				new SelectQuery(Contact.class, ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 2L))).get(0);
			assertNull("contact 1 have no link to students", contact1.getStudent());
		} finally {
			objectContext.rollbackChanges();
		}
	}
	
	@Test
	public void testStudentContactMerge() {
		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		List<Contact> data = getFirstTwoContacts(objectContext);
		Contact contact1 = data.get(0);
		Contact contact2 = data.get(1);
		Student student1 = contact1.getStudent();
		objectContext.commitChanges();
		assertNotNull("contact 1 linked with the student 1", contact1.getStudent());
		assertNotNull("student 1 linked with the contact 1", student1.getContact());
		assertNull("contact 2 have no link to students", contact2.getStudent());

		try {
			student1.setContact(contact2);
			//use the orthodox setter logic in this case will be correct 
			objectContext.commitChanges();
			assertNotNull("contact 2 linked with the student 1", contact2.getStudent());
			assertTrue("Contact 2 have link to student 1", contact2.getStudent().equals(student1));
			assertNotNull("student 1 linked with the contact 2", student1.getContact());
			assertTrue("Student 1 have link with the contact 2", student1.getContact().equals(contact2));
			assertNull("contact 1 have no link to students", contact1.getStudent());
		} finally {
			objectContext.rollbackChanges();
		}
	}
	
	@Test
	public void testStudentContactMergeWithCheckRelationShip() {
		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		List<Contact> data = getSecondTwoContacts(objectContext);
		Contact contact1 = data.get(0);
		Contact contact2 = data.get(1);
		Student student1 = contact1.getStudent();
		Student student2 = contact2.getStudent();
		objectContext.commitChanges();
		assertNotNull("contact 1 linked with the student 1", contact1.getStudent());
		assertNotNull("student 1 linked with the contact 1", student1.getContact());
		assertNotNull("contact 2 linked with the student 2", contact2.getStudent());
		assertNotNull("student 2 linked with the contact 2", student2.getContact());

		try { 
			//student1.setContact(contact2);
			Contact.setObjectToOneTargetWithCheck(Student.CONTACT_PROPERTY, contact2, true, student1);
			objectContext.commitChanges();
			assertNotNull("contact 2 linked with the student 1", contact2.getStudent());
			assertTrue("Contact 2 have link to student 1", contact2.getStudent().equals(student1));
			assertNotNull("student 1 linked with the contact 2", student1.getContact());
			assertTrue("Student 1 have link with the contact 2", student1.getContact().equals(contact2));
			assertNull("contact 1 have no link to students", contact1.getStudent());

			assertNull("student 2 should have no link to contacts", student2.getContact());
		} finally {
			objectContext.rollbackChanges();
		}
	}
	
	@Test
	public void testStudentsContactsMergeFail() {
		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		List<Contact> data = getSecondTwoContacts(objectContext);
		Contact contact1 = data.get(0);
		Contact contact2 = data.get(1);
		Student student1 = contact1.getStudent();
		Student student2 = contact2.getStudent();
		objectContext.commitChanges();
		assertNotNull("contact 1 linked with the student 1", contact1.getStudent());
		assertNotNull("student 1 linked with the contact 1", student1.getContact());
		assertNotNull("contact 2 linked with the student 2", contact2.getStudent());
		assertNotNull("student 2 linked with the contact 2", student2.getContact());

		try { 
			student1.setContact(contact2);
			objectContext.commitChanges();
			assertNotNull("contact 2 linked with the student 1", contact2.getStudent());
			assertTrue("Contact 2 have link to student 1", contact2.getStudent().equals(student1));
			assertNotNull("student 1 linked with the contact 2", student1.getContact());
			assertTrue("Student 1 have link with the contact 2", student1.getContact().equals(contact2));
			assertNull("contact 1 have no link to students", contact1.getStudent());
			//without the reloading student2 will have the link to contact2
			student2 = (Student) objectContext.performQuery(
				new SelectQuery(Student.class, ExpressionFactory.matchDbExp(Student.ID_PK_COLUMN, 4L))).get(0);
			assertNull("student 2 should have no link to contacts", student2.getContact());
		} finally {
			objectContext.rollbackChanges();
		}
	}
	
	@Test
	public void testNullSetter() {
		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		List<Contact> data = getSecondTwoContacts(objectContext);
		Contact contact1 = data.get(0);
		Contact contact2 = data.get(1);
		Student student1 = contact1.getStudent();
		Student student2 = contact2.getStudent();
		objectContext.commitChanges();
		assertNotNull("contact 1 linked with the student 1", contact1.getStudent());
		assertNotNull("student 1 linked with the contact 1", student1.getContact());
		assertNotNull("contact 2 linked with the student 2", contact2.getStudent());
		assertNotNull("student 2 linked with the contact 2", student2.getContact());
		
		boolean commitFailed = false;
		try {
			//contact1.setToOneTarget(Contact.STUDENT_PROPERTY, null, true);
			contact1.setStudent(null);
		} catch (Throwable t) {
			commitFailed = true;
		}
		assertFalse(commitFailed);
		try {
			objectContext.commitChanges();
		} catch (Throwable t) {
			commitFailed = true;
		} finally {
			objectContext.rollbackChanges();
		}
		assertFalse(commitFailed);
	}
	
	private List<Contact> getFirstTwoContacts(final ObjectContext objectContext) {
		Contact contact1 = (Contact) objectContext.performQuery(
			new SelectQuery(Contact.class, ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 2L))).get(0);		
		Contact contact2 = (Contact) objectContext.performQuery(
			new SelectQuery(Contact.class, ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 1L))).get(0);
		assertNotNull("contact 1 have no link to student", contact1.getStudent());
		assertNull("contact 1 have no link to tutor", contact1.getTutor());
		assertNull("contact 2 have no link to student", contact2.getStudent());
		assertNull("contact 2 have no link to tutor", contact2.getTutor());
		List<Contact> result = new ArrayList<>(2);
		result.add(contact1);
		result.add(contact2);
		return result;
	}
	
	private List<Contact> getSecondTwoContacts(final ObjectContext objectContext) {
		Contact contact1 = (Contact) objectContext.performQuery(
			new SelectQuery(Contact.class, ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 3L))).get(0);		
		Contact contact2 = (Contact) objectContext.performQuery(
			new SelectQuery(Contact.class, ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 4L))).get(0);
		assertNotNull("contact 1 have no link to student", contact1.getStudent());
		assertNull("contact 1 have no link to tutor", contact1.getTutor());
		assertNotNull("contact 2 have no link to student", contact2.getStudent());
		assertNull("contact 2 have no link to tutor", contact2.getTutor());
		List<Contact> result = new ArrayList<>(2);
		result.add(contact1);
		result.add(contact2);
		return result;
	}

}
