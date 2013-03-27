package ish.oncourse.services.lifecycle;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.math.Money;
import ish.oncourse.model.*;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class QueueableLifecycleListenerTest extends ServiceTest {

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceModule.class);

		InputStream st = QueueableLifecycleListenerTest.class.getClassLoader().getResourceAsStream("ish/oncourse/services/lifecycle/queuDataSet.xml");
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource onDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), dataSet);
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
		Enrolment enrolment = builder.createEnrolment(invoiceLine, student, courseClass);
		enrolment.setStatus(EnrolmentStatus.SUCCESS);

		ctx.commitChanges();

		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);

		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord"));

		assertEquals("Expecting 7 queueable records.", 7, actualData.getRowCount());

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

		actualData = dbUnitConnection.createQueryTable("QueuedTransaction", String.format("select * from QueuedTransaction"));

		assertTrue("Expecting only one transaction created.", actualData.getRowCount() == 1);
		assertNotNull("Expecting not null transaction key.", actualData.getValue(0, "transactionKey"));
	}

	@Test
	public void testUpdateEntities() throws Exception {
		ICayenneService cayenneService = getService(ICayenneService.class);

		ObjectContext ctx = cayenneService.newContext();

		Course course = Cayenne.objectForPK(ctx, Course.class, 3l);
		course.setName("Test course name update");

		ctx.commitChanges();

		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityIdentifier='Course' and entityWillowId=3 and action='Update'"));

		assertEquals("Expecting 1 queueable records.", 1, actualData.getRowCount());
	}

	@Test
	public void testRemoveEntities() throws Exception {
		ICayenneService cayenneService = getService(ICayenneService.class);

		ObjectContext ctx = cayenneService.newContext();

		Course course = Cayenne.objectForPK(ctx, Course.class, 4l);
		ctx.deleteObjects(course);

		ctx.commitChanges();

		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityIdentifier='Course' and entityWillowId=4"));

		assertEquals("Expecting 1 queueable records.", 1, actualData.getRowCount());
		assertNotNull("Expecting not null transaction id.", actualData.getValue(0, "transactionId"));

		TutorRole tutorRole = Cayenne.objectForPK(ctx, TutorRole.class, 1l);
		CourseClass courseClass = Cayenne.objectForPK(ctx, CourseClass.class, 10l);

		ctx.deleteObjects(tutorRole);
		ctx.deleteObjects(courseClass);

		ctx.commitChanges();

		actualData = dbUnitConnection
				.createQueryTable(
						"QueuedRecord",
						String.format("select * from QueuedRecord where (entityIdentifier='TutorRole' and entityWillowId=1) or (entityIdentifier='CourseClass' and entityWillowId=10)"));

		assertEquals("Expecting courseClass and TutorRole records.", 2, actualData.getRowCount());
		assertEquals("Expecting Delete action.", "Delete", actualData.getValue(0, "action"));
		assertEquals("Expecting Delete action.", "Delete", actualData.getValue(1, "action"));
		assertEquals("Expecting identical transactionIds.", actualData.getValue(0, "transactionId"),
				actualData.getValue(1, "transactionId"));

		Course course2 = Cayenne.objectForPK(ctx, Course.class, 5l);
		Tutor tutor = Cayenne.objectForPK(ctx, Tutor.class, 1l);

		ctx.deleteObjects(course2);
		ctx.deleteObjects(tutor);

		ctx.commitChanges();

		actualData = dbUnitConnection
				.createQueryTable(
						"QueuedRecord",
						String.format("select * from QueuedRecord where (entityIdentifier='Course' and entityWillowId=5 and action='Delete') or (entityIdentifier='Tutor' and entityWillowId=1 and action='Delete')"));

		assertEquals("Expecting course and tutor records.", 2, actualData.getRowCount());
		assertEquals("Expecting identical transactionIds.", actualData.getValue(0, "transactionId"),
				actualData.getValue(1, "transactionId"));
	}

	/**
	 * Tests saving global preferences, such as collegeId is null.
	 * @throws Exception
	 */
	@Test
	public void testSaveGlobalPreference() throws Exception {
		
		ICayenneService cayenneService = getService(ICayenneService.class);

		ObjectContext replicatingContext = cayenneService.newContext();
		Preference pref = replicatingContext.newObject(Preference.class);
		pref.setName("ish.test.pref");
		pref.setValueString("pref value 123");
		replicatingContext.commitChanges();
		
		assertNotNull("Expecting preference saved with not null id", pref.getId());

		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);

		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityIdentifier='Preference'"));

		assertEquals("Expecting no queued records created.", 0, actualData.getRowCount());

		ObjectContext notReplicatingContext = cayenneService.newNonReplicatingContext();
		
		pref = replicatingContext.newObject(Preference.class);
		pref.setName("ish.test.pref.2");
		pref.setValueString("pref value 2");
		replicatingContext.commitChanges();
		notReplicatingContext.commitChanges();
		
		assertNotNull("Expecting preference saved with not null id", pref.getId());
		

		actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityIdentifier='Preference'"));

		assertEquals("Expecting no queued records created.", 0, actualData.getRowCount());
	}
	
	@Test
	public void testMultyCollegesAndOneCommit() throws Exception {
		ICayenneService cayenneService = getService(ICayenneService.class);
		ObjectContext context = cayenneService.newContext();
		
		Course course1 = Cayenne.objectForPK(context, Course.class, 5l);
		Course course2 = Cayenne.objectForPK(context, Course.class, 6l);
		course1.setModified(new Date());
		course2.setModified(new Date());
		
		context.commitChanges();
		
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		
		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityIdentifier='Course'"));
		
		assertEquals("Expecting two queued records.", 2, actualData.getRowCount());
		assertNotSame("Expecting two different queued transactions.", actualData.getValue(0, "transactionId"), actualData.getValue(1, "transactionId"));
		
		actualData = dbUnitConnection.createQueryTable("QueuedTransaction", String.format("select * from QueuedTransaction"));
		assertEquals("Expecting two queued transactions.", 2, actualData.getRowCount());
	}
	
	@Test
	public void testTheSameCollegeOneCommit() throws Exception {
		ICayenneService cayenneService = getService(ICayenneService.class);
		ObjectContext context = cayenneService.newContext();
		
		Course course1 = Cayenne.objectForPK(context, Course.class, 4l);
		Course course2 = Cayenne.objectForPK(context, Course.class, 5l);
		course1.setModified(new Date());
		course2.setModified(new Date());
		
		context.commitChanges();
		
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		
		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityIdentifier='Course'"));
		
		assertEquals("Expecting two queued records.", 2, actualData.getRowCount());
		assertEquals("Expecting record assigned to the same transaction.", actualData.getValue(0, "transactionId"), actualData.getValue(1, "transactionId"));
		
		actualData = dbUnitConnection.createQueryTable("QueuedTransaction", String.format("select * from QueuedTransaction"));
		assertEquals("Expecting only one transactions.", 1, actualData.getRowCount());
	}

    @Test
    public  void  testPostAdd()
    {
        ICayenneService cayenneService = getService(ICayenneService.class);
        ObjectContext context = cayenneService.newContext();

        try {
            testQueueablePostAdd(BinaryInfo.class, context);
            testQueueablePostAdd(DiscountConcessionType.class, context);
            testQueueablePostAdd(Preference.class, context);
            testQueueablePostAdd(ContactRelationType.class, context);
            testQueueablePostAdd(Discount.class, context);
            testQueueablePostAdd(TaggableTag.class, context);
            testQueueablePostAdd(Session.class, context);
            testQueueablePostAdd(InvoiceLine.class, context);
            testQueueablePostAdd(Contact.class, context);
            testQueueablePostAdd(SessionTutor.class, context);
            testQueueablePostAdd(WaitingListSite.class, context);
            testQueueablePostAdd(BinaryInfoRelation.class, context);
            testQueueablePostAdd(Invoice.class, context);
            testQueueablePostAdd(VoucherProduct.class, context);
            testQueueablePostAdd(ProductItem.class, context);
            testQueueablePostAdd(Outcome.class, context);
            testQueueablePostAdd(MessageTemplate.class, context);
            testQueueablePostAdd(TutorRole.class, context);
            testQueueablePostAdd(Attendance.class, context);
            testQueueablePostAdd(Room.class, context);
            testQueueablePostAdd(Tag.class, context);
            testQueueablePostAdd(Message.class, context);
            testQueueablePostAdd(PaymentIn.class, context);
            testQueueablePostAdd(CourseClass.class, context);
            testQueueablePostAdd(ConcessionType.class, context);
            testQueueablePostAdd(PaymentInLine.class, context);
            testQueueablePostAdd(ContactRelation.class, context);
            testQueueablePostAdd(PaymentOut.class, context);
            testQueueablePostAdd(MessagePerson.class, context);
            testQueueablePostAdd(Membership.class, context);
            testQueueablePostAdd(Enrolment.class, context);
            testQueueablePostAdd(Certificate.class, context);
            testQueueablePostAdd(Course.class, context);
            testQueueablePostAdd(InvoiceLineDiscount.class, context);
            testQueueablePostAdd(Voucher.class, context);
            testQueueablePostAdd(CourseModule.class, context);
            testQueueablePostAdd(Tutor.class, context);
            testQueueablePostAdd(PaymentOut.class, context);
            testQueueablePostAdd(DiscountMembership.class, context);
            testQueueablePostAdd(TagGroupRequirement.class, context);
            testQueueablePostAdd(Site.class, context);
            testQueueablePostAdd(Taggable.class, context);
            testQueueablePostAdd(DiscountMembershipRelationType.class, context);
            testQueueablePostAdd(WaitingList.class, context);
            testQueueablePostAdd(MembershipProduct.class, context);
            testQueueablePostAdd(Student.class, context);
            testQueueablePostAdd(DiscountCourseClass.class, context);
            testQueueablePostAdd(Product.class, context);
            testQueueablePostAdd(CertificateOutcome.class, context);
            testQueueablePostAdd(StudentConcession.class, context);
        } finally {
            context.rollbackChanges();
        }

    }

    private void testQueueablePostAdd(Class<? extends Queueable> queueableClass, ObjectContext context) {
        Queueable queueable = context.newObject(queueableClass);
        assertTrue(String.format("created property is not null on postAdd for %s ",queueableClass.getSimpleName()), queueable.getCreated() != null);
        assertTrue(String.format("modified property is not null on postAdd for %s ",queueableClass.getSimpleName()), queueable.getCreated() != null);
    }

    @Test
    /**
     * * The test has been introduced to exclude rewrite created date by QueueableLifecycleListener when the entity came from angel
     */
    public void testQueueableWithPresetCreatedDate()
    {
        ICayenneService cayenneService = getService(ICayenneService.class);
        ObjectContext context = cayenneService.newContext();

        PaymentIn paymentIn = context.newObject(PaymentIn.class);
        Date zero = new Date(0);
        paymentIn.setCreated(zero);
        paymentIn.setAmount(new Money("0.0"));
        paymentIn.setSource(PaymentSource.SOURCE_ONCOURSE);
        paymentIn.setCollege(Cayenne.objectForPK(context,College.class,1));
        context.commitChanges();

        try {
            context = cayenneService.newContext();
            paymentIn = Cayenne.objectForPK(context, PaymentIn.class, paymentIn.getId());

            assertEquals("PaymentIn date after saved should be the same", zero, paymentIn.getCreated());
        } finally {
            context.deleteObjects(paymentIn);
        }

    }

    @Test
    public void testQueuedRecordForSurvey() throws Exception {
        ICayenneService cayenneService = getService(ICayenneService.class);
        ObjectContext nrContext = cayenneService.newNonReplicatingContext();

		final SampleEntityBuilder builder = SampleEntityBuilder.newBuilder(nrContext);

		Contact contact = builder.createContact();
		Student student = builder.createStudent(contact);
		Invoice invoice = builder.createInvoice(contact);
		InvoiceLine invoiceLine =  builder.createInvoiceLine(invoice);
		Enrolment enrolment = builder.createEnrolment(invoiceLine,student,Cayenne.objectForPK(nrContext, CourseClass.class, 10));
		nrContext.commitChanges();

		ObjectContext context = cayenneService.newContext();
		Survey survey = context.newObject(Survey.class);
        survey.setCourseScore(1);
        survey.setVenueScore(2);
        survey.setTutorScore(3);
        survey.setCollege(Cayenne.objectForPK(context, College.class, 1));
        survey.setComment("comment");
        survey.setUniqueCode("12345678");
        survey.setEnrolment(Cayenne.objectForPK(context, Enrolment.class, enrolment.getId()));
        context.commitChanges();

        DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);

        ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord",
                String.format("select * from QueuedRecord where entityIdentifier='Survey'"));

        assertEquals("Expecting one queued records.", 1, actualData.getRowCount());
        assertEquals("Test entityWillowId", survey.getId().longValue(), ((BigInteger) actualData.getValue(0, "entityWillowId")).longValue());

        context.deleteObjects(survey);
        context.commitChanges();
    }

}
