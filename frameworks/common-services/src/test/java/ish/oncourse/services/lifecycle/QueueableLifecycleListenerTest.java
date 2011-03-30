package ish.oncourse.services.lifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.SampleEntityBuilder;
import ish.oncourse.model.Student;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.services.AbstractDatabaseTest;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.persistence.ICayenneService;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.BeforeClass;
import org.junit.Test;

public class QueueableLifecycleListenerTest extends AbstractDatabaseTest {

	@BeforeClass
	public static void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceModule.class);

		InputStream st = QueueableLifecycleListenerTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/services/lifecycle/referenceDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

		DataSource refDataSource = getDataSource("jdbc/oncourse_reference");

		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

		st = QueueableLifecycleListenerTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/services/lifecycle/queuDataSet.xml");
		dataSet = new FlatXmlDataSetBuilder().build(st);

		DataSource onDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), dataSet);
	}

	@Test
	public void testCreateEntities() throws Exception {

		ICayenneService cayenneService = getService(ICayenneService.class);

		final ObjectContext ctx = cayenneService.newContext();

		final SampleEntityBuilder builder = SampleEntityBuilder.newBuilder(ctx);

		Course course = builder.createCourse();
		Contact contact = builder.createContact();
		Invoice invoice = builder.createInvoice(contact);
		InvoiceLine invoiceLine = builder.createInvoiceLine(invoice);
		Student student = builder.createStudent(contact);
		CourseClass courseClass = builder.createCourseClass(course);
		
		builder.createEnrolment(invoiceLine, student, courseClass);
				
		ctx.commitChanges();
		
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);

		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord"));

		assertEquals("Expecting 7 queueable records.", 7, actualData.getRowCount());

		String transactionKey = (String) actualData.getValue(0, "transactionKey");

		for (int i = 1; i < actualData.getRowCount(); i++) {
			assertTrue("Expecting the same transactionKey across records.", actualData.getValue(i, "transactionKey").equals(transactionKey));
		}

		// Check entity names
		Set<String> entityNames = new HashSet<String>();
		for (int i = 0; i < actualData.getRowCount(); i++) {
			String entityName = (String) actualData.getValue(i, "entityIdentifier");
			entityNames.add(entityName);
		}

		assertTrue("Expecting Enrolment record.", entityNames.contains("Enrolment"));
		assertTrue("Expecting CourseClass record.", entityNames.contains("CourseClass"));
		assertTrue("Expecting Course record.", entityNames.contains("Course"));
		assertTrue("Expecting InvoiceLine record.", entityNames.contains("InvoiceLine"));
		assertTrue("Expecting Invoice record.", entityNames.contains("Invoice"));
		assertTrue("Expecting Student record.", entityNames.contains("Student"));
		assertTrue("Expecting Contact record.", entityNames.contains("Contact"));
	}

	@Test
	public void testUpdateEntities() throws Exception {
		ICayenneService cayenneService = getService(ICayenneService.class);

		ObjectContext ctx = cayenneService.newContext();
		
		Course course = Cayenne.objectForPK(ctx, Course.class, 3l);
		course.setName("Test course name update");
		
		ctx.commitChanges();
		
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='Course' and entityWillowId=3 and action='Update'"));
		
		assertEquals("Expecting 1 queueable records.", 1, actualData.getRowCount());
	}

	@Test
	public void testRemoveEntities() throws Exception {
		ICayenneService cayenneService = getService(ICayenneService.class);

		ObjectContext ctx = cayenneService.newContext();
		
		Course course = Cayenne.objectForPK(ctx, Course.class, 4l);
		ctx.deleteObject(course);
		
		ctx.commitChanges();
		
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='Course' and entityWillowId=4"));
		
		assertEquals("Expecting 1 queueable records.", 1, actualData.getRowCount());
		assertNotNull("Expecting not null transaction id.", actualData.getValue(0, "transactionKey"));
		
		TutorRole tutorRole = Cayenne.objectForPK(ctx, TutorRole.class, 1l);
		CourseClass courseClass =  Cayenne.objectForPK(ctx, CourseClass.class, 10l);
		
		ctx.deleteObject(tutorRole);
		ctx.deleteObject(courseClass);
	    
		ctx.commitChanges();
		
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where (entityIdentifier='TutorRole' and entityWillowId=1) or (entityIdentifier='CourseClass' and entityWillowId=10)"));
		
		assertEquals("Expecting courseClass and TutorRole records.", 2, actualData.getRowCount());
		assertEquals("Expecting Delete action.", "Delete", actualData.getValue(0, "action"));
		assertEquals("Expecting Delete action.", "Delete", actualData.getValue(1, "action"));
		assertEquals("Expecting identical transactionKeys.", actualData.getValue(0, "transactionKey"), actualData.getValue(1, "transactionKey"));
		
		Course course2 = Cayenne.objectForPK(ctx, Course.class, 5l);
		Tutor tutor = Cayenne.objectForPK(ctx, Tutor.class, 1l);
		
		ctx.deleteObject(course2);
		ctx.deleteObject(tutor);
		
		ctx.commitChanges();
		
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where (entityIdentifier='Course' and entityWillowId=5) or (entityIdentifier='Tutor' and entityWillowId=1)"));
		assertEquals("Expecting course and tutor records.", 2, actualData.getRowCount());
		assertEquals("Expecting identical transactionKeys.", actualData.getValue(0, "transactionKey"), actualData.getValue(1, "transactionKey"));
	}
}
