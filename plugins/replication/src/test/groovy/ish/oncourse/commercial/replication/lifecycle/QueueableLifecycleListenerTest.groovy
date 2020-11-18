/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.lifecycle

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.common.types.MessageType
import ish.oncourse.commercial.replication.cayenne.QueuedRecord
import ish.oncourse.commercial.replication.cayenne.QueuedRecordAction
import ish.oncourse.commercial.replication.cayenne.QueuedTransaction
import ish.oncourse.commercial.replication.handler.OutboundReplicationHandlerTest
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.lifecycle.SampleEntityBuilder
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.EJBQLQuery
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.*

/**
 */
@CompileStatic
class QueueableLifecycleListenerTest extends CayenneIshTestCase {

	@Before
    void setup() throws Exception {
		wipeTables()
        InputStream st = OutboundReplicationHandlerTest.class.getClassLoader().getResourceAsStream("ish/oncourse/commercial/replication/lifecycle/queuDataSet.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)
        executeDatabaseOperation(dataSet)
        PreferenceController pref = injector.getInstance(PreferenceController.class)
        pref.setReplicationEnabled(true)
        super.setup()
    }

	@Test
    void testCreateEmailAndSMSMessagesForReplication() throws Exception {
		ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
        ObjectContext deleteContext = cayenneService.getNewNonReplicatingContext()
        deleteContext.performQuery(new EJBQLQuery("delete from QueuedRecord"))
        deleteContext.performQuery(new EJBQLQuery("delete from QueuedTransaction"))
        final ObjectContext ctx = cayenneService.getNewContext()
        final SampleEntityBuilder builder = SampleEntityBuilder.newBuilder(ctx)
        final Contact contact = builder.createContact()
        final Contact contact2 = builder.createContact()
        final Message message = builder.createEmailMessage()
        final Message message2 = builder.createSMSMessage()
        final Message message3 = builder.createEmailMessage()
        builder.createMessagePerson(contact, message2, MessageType.SMS)
        builder.createMessagePerson(contact, message, MessageType.EMAIL)
        builder.createMessagePerson(contact2, message2, MessageType.SMS)
        builder.createMessagePerson(contact2, message, MessageType.EMAIL)
        builder.createMessagePerson(contact2, message3, MessageType.SMS)
        ctx.commitChanges()

        List<QueuedRecord> queuedRecordList = ObjectSelect.query(QueuedRecord).select(ctx)
        assertEquals("Expecting 10 queueable records.", 10, queuedRecordList.size())
        // Check entity names
		final Map<String, Integer> entities = new HashMap<>()
        for (int i = 0; i < queuedRecordList.size(); i++) {
			String entityName = queuedRecordList.get(i).getTableName()
            System.out.println(String.format("EntityName:%s, id:%s", entityName,
                    queuedRecordList.get(i).getForeignRecordId()))
            if (entities.containsKey(entityName)) {
				entities.put(entityName, entities.get(entityName) + 1)
            } else {
				entities.put(entityName, 1)
            }
		}
		assertTrue("Expecting Contact record.", entities.containsKey(Contact.class.getSimpleName()))
        assertTrue("Expecting Message record.", entities.containsKey(Message.class.getSimpleName()))
        assertTrue("Expecting MessagePerson record.", entities.containsKey(MessagePerson.class.getSimpleName()))
        assertEquals("Expected 2 Contacts ", 2, (int) entities.get(Contact.class.getSimpleName()))
        assertEquals("Expected 3 Messages ", 3, (int) entities.get(Message.class.getSimpleName()))
        assertEquals("Expected 5 MessagePersons ", 5, (int) entities.get(MessagePerson.class.getSimpleName()))
    }

	@Test
    void testCreateSMSMessagesForReplication() throws Exception {
		ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
        ObjectContext deleteContext = cayenneService.getNewNonReplicatingContext()
        deleteContext.performQuery(new EJBQLQuery("delete from QueuedRecord"))
        deleteContext.performQuery(new EJBQLQuery("delete from QueuedTransaction"))
        final ObjectContext ctx = cayenneService.getNewContext()
        final SampleEntityBuilder builder = SampleEntityBuilder.newBuilder(ctx)
        final Contact contact = builder.createContact()
        final Message message = builder.createSMSMessage()
        final Contact contact2 = builder.createContact()
        final Message message2 = builder.createSMSMessage()
        builder.createMessagePerson(contact, message, MessageType.SMS)
        builder.createMessagePerson(contact2, message2, MessageType.SMS)
        ctx.commitChanges()

        List<QueuedRecord> queuedRecordList = ObjectSelect.query(QueuedRecord)
                .select(ctx)
        assertEquals("Expecting 6 queueable records.", 6, queuedRecordList.size())

        // Check entity names
		final Map<String, Integer> entities = new HashMap<>()
        for (int i = 0; i < queuedRecordList.size(); i++) {
			String entityName = queuedRecordList.get(i).getTableName()
            System.out.println(String.format("EntityName:%s, id:%s", entityName,
                    queuedRecordList.get(i).getForeignRecordId()))
            if (entities.containsKey(entityName)) {
				entities.put(entityName, entities.get(entityName) + 1)
            } else {
				entities.put(entityName, 1)
            }
		}

		assertTrue("Expecting Contact record.", entities.containsKey(Contact.class.getSimpleName()))
        assertTrue("Expecting Message record.", entities.containsKey(Message.class.getSimpleName()))
        assertTrue("Expecting MessagePerson record.", entities.containsKey(MessagePerson.class.getSimpleName()))
        assertEquals("Expected 2 Contacts ", 2, (int) entities.get(Contact.class.getSimpleName()))
        assertEquals("Expected 2 Messages ", 2, (int) entities.get(Message.class.getSimpleName()))
        assertEquals("Expected 2 MessagePersons ", 2, (int) entities.get(MessagePerson.class.getSimpleName()))
    }

	@Test
    void testCreateSMSMessagesForReplicationWithoutAndWithinLicenseSmsPreference() throws Exception {
		ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
        ObjectContext deleteContext = cayenneService.getNewNonReplicatingContext()
        deleteContext.performQuery(new EJBQLQuery("delete from QueuedRecord"))
        deleteContext.performQuery(new EJBQLQuery("delete from QueuedTransaction"))
        deleteContext.performQuery(new EJBQLQuery("update Preference p set p.valueString='false'"))
        final ObjectContext ctx = cayenneService.getNewContext()
        final SampleEntityBuilder builder = SampleEntityBuilder.newBuilder(ctx)
        final Contact contact = builder.createContact()
        final Message message = builder.createSMSMessage()
        builder.createMessagePerson(contact, message, MessageType.SMS)
        ctx.commitChanges()
        final Contact contact2 = builder.createContact()
        final Message message2 = builder.createSMSMessage()
        builder.createMessagePerson(contact2, message2, MessageType.SMS)
        deleteContext.performQuery(new EJBQLQuery("update Preference p set p.valueString='true'"))

        List<QueuedRecord> queuedRecordList = ObjectSelect.query(QueuedRecord).select(ctx)
        assertEquals("Expecting 0 queueable records.", 0, queuedRecordList.size())

        // Check entity names
		Map<String, Integer> entities = new HashMap<>()
        for (int i = 0; i < queuedRecordList.size(); i++) {
			String entityName = queuedRecordList.get(i).getTableName()
            System.out.println(String.format("EntityName:%s, id:%s", entityName,
                    queuedRecordList.get(i).getForeignRecordId()))
            if (entities.containsKey(entityName)) {
				entities.put(entityName, entities.get(entityName) + 1)
            } else {
				entities.put(entityName, 1)
            }
		}

		assertFalse("Expecting Contact record.", entities.containsKey(Contact.class.getSimpleName()))
        assertFalse("Expecting Message record.", entities.containsKey(Message.class.getSimpleName()))
        assertFalse("Expecting MessagePerson record.", entities.containsKey(MessagePerson.class.getSimpleName()))
        assertNull("Expected 0 Contact ", entities.get(Contact.class.getSimpleName()))
        assertNull("Expected 0 Message ", entities.get(Message.class.getSimpleName()))
        assertNull("Expected 0 MessagePerson ", entities.get(MessagePerson.class.getSimpleName()))
        ctx.commitChanges()
        queuedRecordList = ObjectSelect.query(QueuedRecord).select(ctx)
        assertEquals("Expecting 3 queueable records.", 3, queuedRecordList.size())

        // Check entity names
		entities = new HashMap<>()
        for (int i = 0; i < queuedRecordList.size(); i++) {
			String entityName = queuedRecordList.get(i).getTableName()
            System.out.println(String.format("EntityName:%s, id:%s", entityName,
                    queuedRecordList.get(i).getForeignRecordId()))
            if (entities.containsKey(entityName)) {
				entities.put(entityName, entities.get(entityName) + 1)
            } else {
				entities.put(entityName, 1)
            }
		}

		assertTrue("Expecting Contact record.", entities.containsKey(Contact.class.getSimpleName()))
        assertTrue("Expecting Message record.", entities.containsKey(Message.class.getSimpleName()))
        assertTrue("Expecting MessagePerson record.", entities.containsKey(MessagePerson.class.getSimpleName()))
        assertEquals("Expected 1 Contact ", 1, (int) entities.get(Contact.class.getSimpleName()))
        assertEquals("Expected 1 Message ", 1, (int) entities.get(Message.class.getSimpleName()))
        assertEquals("Expected 1 MessagePerson ", 1, (int) entities.get(MessagePerson.class.getSimpleName()))
    }

	@Test
    void testCreateEntities() throws Exception {
		ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
        ObjectContext deleteContext = cayenneService.getNewNonReplicatingContext()
        deleteContext.performQuery(new EJBQLQuery("delete from QueuedRecord"))
        deleteContext.performQuery(new EJBQLQuery("delete from QueuedTransaction"))

        final ObjectContext ctx = cayenneService.getNewContext()

        final SampleEntityBuilder builder = SampleEntityBuilder.newBuilder(ctx)

        Course course = builder.createCourse()
        Contact contact = builder.createContact()
        Invoice invoice = builder.createInvoice(contact)
        InvoiceLine invoiceLine = builder.createInvoiceLine(invoice)
        Student student = builder.createStudent(contact)
        CourseClass courseClass = builder.createCourseClass(course)
        builder.createEnrolment(invoiceLine, student, courseClass)

        ctx.commitChanges()

        List<QueuedRecord> queuedRecordList = ObjectSelect.query(QueuedRecord).select(ctx)

        // Check entity names
		Map<String, Integer> entityNames = new HashMap<>()
        for (int i = 0; i < queuedRecordList.size(); i++) {
			String entityName = queuedRecordList.get(i).getTableName()
            if (entityNames.containsKey(entityName)) {
				Integer count = entityNames.get(entityName)
                entityNames.remove(entityName)
                entityNames.put(entityName, count + 1)
            } else {
				entityNames.put(entityName, 1)
            }
		}

		/**
		 * expecting following QueuedRecords entities:
		 * <ul>
		 * <li>Course - CREATE</li>
		 * <li>CourseClass - CREATE</li>
		 * <li>Contact - CREATE</li>
		 * <li>Student - CREATE</li>
		 * <li>Invoice - CREATE</li>
		 * <li>InvoiceLine - CREATE</li>
		 * <li>Enrolment - CREATE</li>
		 * <li>Enrolment - UPDATE</li>
		 * <li>Outcome - CREATE</li>
		 * <li>4 x Preference - CREATE</li>
		 * </ul>
		 * Total of 13 records in the queue
		 */
		assertTrue("Expecting Enrolment record.", entityNames.containsKey("Enrolment"))
        assertEquals("Expecting 1 Enrolment record.", Integer.valueOf(2), entityNames.get("Enrolment"))
        assertTrue("Expecting CourseClass record.", entityNames.containsKey("CourseClass"))
        assertEquals("Expecting 1 CourseClass record.", Integer.valueOf(1), entityNames.get("CourseClass"))
        assertTrue("Expecting Course record.", entityNames.containsKey("Course"))
        assertEquals("Expecting 1 Course record.", Integer.valueOf(1), entityNames.get("Course"))
        assertTrue("Expecting InvoiceLine record.", entityNames.containsKey("InvoiceLine"))
        assertEquals("Expecting 1 InvoiceLine record.", Integer.valueOf(1), entityNames.get("InvoiceLine"))
        assertTrue("Expecting Invoice record.", entityNames.containsKey("Invoice"))
        assertEquals("Expecting 1 Invoice record.", Integer.valueOf(1), entityNames.get("Invoice"))
        assertTrue("Expecting Student record.", entityNames.containsKey("Student"))
        assertEquals("Expecting 1 Student record.", Integer.valueOf(1), entityNames.get("Student"))
        assertTrue("Expecting Contact record.", entityNames.containsKey("Contact"))
        assertEquals("Expecting 1 Contact record.", Integer.valueOf(1), entityNames.get("Contact"))
        assertTrue("Expecting Outcome record.", entityNames.containsKey("Outcome"))
        assertEquals("Expecting 1 Outcome record.", Integer.valueOf(1), entityNames.get("Outcome"))
        assertTrue("Expecting Tax record.", entityNames.containsKey("Outcome"))
        assertEquals("Expecting 3 Tax records.", Integer.valueOf(1), entityNames.get("Outcome"))
        assertFalse(entityNames.containsKey("Preference"))
        assertTrue("Expecting FieldConfigurationScheme record.", entityNames.containsKey("FieldConfigurationScheme"))
        assertEquals("Expecting 1 FieldConfigurationScheme record.", Integer.valueOf(1), entityNames.get("FieldConfigurationScheme"))
        assertTrue("Expecting FieldConfigurationLink record.", entityNames.containsKey("FieldConfigurationLink"))
        assertEquals("Expecting 1 FieldConfigurationLink record.", Integer.valueOf(3), entityNames.get("FieldConfigurationLink"))
        assertTrue("Expecting ApplicationFieldConfiguration record.", entityNames.containsKey("ApplicationFieldConfiguration"))
        assertEquals("Expecting 1 FieldConfigurationLink record.", Integer.valueOf(1), entityNames.get("ApplicationFieldConfiguration"))
        assertTrue("Expecting WaitingListFieldConfiguration record.", entityNames.containsKey("WaitingListFieldConfiguration"))
        assertEquals("Expecting 1 FieldConfigurationLink record.", Integer.valueOf(1), entityNames.get("WaitingListFieldConfiguration"))
        assertTrue("Expecting EnrolmentFieldConfiguration record.", entityNames.containsKey("EnrolmentFieldConfiguration"))
        assertEquals("Expecting 1 FieldConfiguration record.", Integer.valueOf(1), entityNames.get("EnrolmentFieldConfiguration"))
        assertEquals("Expecting 14 queueable records.", 19, queuedRecordList.size())

        List<QueuedTransaction> queuedTransactionList = ObjectSelect.query(QueuedTransaction).select(ctx)

        // 2, one for the actual replication, one for the Outcome created in trigger
		assertEquals("Expecting only one transaction created.", 2, queuedTransactionList.size())
        assertNotNull("Expecting not null transaction key.", queuedTransactionList.get(0).getTransactionKey())
        assertNotNull("Expecting not null transaction key.", queuedTransactionList.get(1).getTransactionKey())
    }

	@Test
    void testUpdateEntities() throws Exception {

		ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
        ObjectContext deleteContext = cayenneService.getNewNonReplicatingContext()
        deleteContext.performQuery(new EJBQLQuery("delete from QueuedRecord"))
        deleteContext.performQuery(new EJBQLQuery("delete from QueuedTransaction"))
        ObjectContext ctx = cayenneService.getNewContext()

        Course course = SelectById.query(Course.class, 3).selectOne(ctx)
        course.setName("Test course name update")

        ctx.commitChanges()

        List<QueuedRecord> queuedRecordList = ObjectSelect.query(QueuedRecord)
                .where(QueuedRecord.TABLE_NAME.eq("Course"))
                .and(QueuedRecord.FOREIGN_RECORD_ID.eq(3L))
                .select(ctx)

        assertEquals("Expecting 1 queueable records.", 1, queuedRecordList.size())
        assertTrue("Expecting action 'Update'",
                "update".equalsIgnoreCase(queuedRecordList.get(0).getAction().name()))
    }

	@Test
    void testRemoveEntities() throws Exception {

		ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
        ObjectContext deleteContext = cayenneService.getNewNonReplicatingContext()
        deleteContext.performQuery(new EJBQLQuery("delete from QueuedRecord"))
        deleteContext.performQuery(new EJBQLQuery("delete from QueuedTransaction"))
        ObjectContext ctx = cayenneService.getNewContext()

        Course course = SelectById.query(Course.class, 4).selectOne(ctx)
        ctx.deleteObjects(course)

        ctx.commitChanges()

        List<QueuedRecord> queuedRecordList = ObjectSelect.query(QueuedRecord)
                .where(QueuedRecord.TABLE_NAME.eq("Course"))
                .and(QueuedRecord.FOREIGN_RECORD_ID.eq(4L))
                .select(ctx)
        assertEquals("Expecting 1 queueable records.", 1, queuedRecordList.size())
        assertNotNull("Expecting not null transaction id.", queuedRecordList.get(0).getTransactionId())

        CourseClassTutor tutorRole = SelectById.query(CourseClassTutor.class, 1).selectOne(ctx)
        CourseClass courseClass = SelectById.query(CourseClass.class, 10).selectOne(ctx)

        ctx.deleteObjects(tutorRole, courseClass)

        ctx.commitChanges()

        queuedRecordList = ObjectSelect.query(QueuedRecord)
                .where(QueuedRecord.TABLE_NAME.eq("CourseClassTutor")
                        .andExp(QueuedRecord.FOREIGN_RECORD_ID.eq(1L)))
                .or(QueuedRecord.TABLE_NAME.eq("CourseClass")
                        .andExp(QueuedRecord.FOREIGN_RECORD_ID.eq(10L)))
                .select(ctx)

        assertEquals("Expecting courseClass and TutorRole records.", 2, queuedRecordList.size())
        assertTrue("Expecting Delete action.", "Delete".equalsIgnoreCase(queuedRecordList.get(0).getAction().name()))
        assertTrue("Expecting Delete action.", "Delete".equalsIgnoreCase(queuedRecordList.get(1).getAction().name()))
        assertEquals("Expecting identical transactionIds.", queuedRecordList.get(0).getTransactionId(),
                queuedRecordList.get(1).getTransactionId())

        Course course2 = SelectById.query(Course.class, 5).selectOne(ctx)
        Tutor tutor = SelectById.query(Tutor.class, 1).selectOne(ctx)

        ctx.deleteObjects(course2, tutor)

        ctx.commitChanges()

        queuedRecordList = ObjectSelect.query(QueuedRecord)
                .where(QueuedRecord.FOREIGN_RECORD_ID.eq(5L)
                        .andExp(QueuedRecord.TABLE_NAME.eq("Course"))
                        .andExp(QueuedRecord.ACTION.eq(QueuedRecordAction.DELETE)))
                .or(QueuedRecord.FOREIGN_RECORD_ID.eq(1L)
                        .andExp(QueuedRecord.TABLE_NAME.eq("Tutor"))
                        .andExp(QueuedRecord.ACTION.eq(QueuedRecordAction.DELETE)))
        .select(ctx)
        assertEquals("Expecting course and tutor records.", 2, queuedRecordList.size())
        assertEquals("Expecting identical transactionIds.", queuedRecordList.get(0).getTransactionId(),
                queuedRecordList.get(1).getTransactionId())

    }
}
