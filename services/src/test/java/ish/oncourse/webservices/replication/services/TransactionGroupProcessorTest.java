package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.tapestry.ServiceTest;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.replication.builders.IWillowStubBuilder;
import ish.oncourse.webservices.util.*;
import ish.oncourse.webservices.v21.stubs.replication.BinaryInfoRelationStub;
import ish.oncourse.webservices.v21.stubs.replication.CourseStub;
import ish.oncourse.webservices.v21.stubs.replication.DocumentStub;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static ish.oncourse.webservices.replication.services.TransactionGroupProcessorTestUtils.*;
import static ish.oncourse.webservices.util.SupportedVersions.V21;
import static org.junit.Assert.*;

public class TransactionGroupProcessorTest extends ServiceTest {
	WillowQueueService willowQueueService;
    IWillowStubBuilder willowStubBuilder;
    ITransactionGroupProcessor transactionGroupProcessor;
    ICayenneService cayenneService;

    @Before
    public void setup() throws Exception {
		initTest("ish.oncourse.webservices.services", "", ReplicationTestModule.class);
		new LoadDataSet().dataSetFile("ish/oncourse/webservices/replication/services/TransactionGroupProcessorTest.xml")
				.load(testContext.getDS());
        willowQueueService = new WillowQueueService(getObject(IWebSiteService.class, null), getService(ICayenneService.class));
        willowStubBuilder = getService(IWillowStubBuilder.class);
        transactionGroupProcessor = getService(ITransactionGroupProcessor.class);
        cayenneService = getService(ICayenneService.class);
    }


	@Test
	public void testDeleteV17Object() throws Exception {
		/**
		 * Transaction with one Contact delete.
		 */
		GenericTransactionGroup transactionGroup = getTransactionGroup(0, SupportedVersions.V21);
		List<GenericReplicatedRecord> replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
		assertEquals("Expecting one failed replicatedRecord, size test", 1, replicatedRecords.size());
		assertEquals("Expecting one failed replicatedRecord, status test", true, StubUtils.hasFailedStatus(replicatedRecords.get(0)));
		assertEquals("Expecting one failed replicatedRecord, message test", "Failed to process transaction group: " + String.format(TransactionGroupProcessorImpl.MESSAGE_TEMPLATE_NO_STUB,1,"Contact",1,"Invoice") +  " and collegeId: 1", replicatedRecords.get(0).getMessage());
		/**
		 * Transaction with one Student delete.
		 */
		transactionGroup = getTransactionGroup(1, SupportedVersions.V21);
		replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
		assertEquals("Expecting one failed replicatedRecord, size test", 1, replicatedRecords.size());
		assertEquals("Expecting one failed replicatedRecord, status test", true, StubUtils.hasFailedStatus(replicatedRecords.get(0)));
		assertEquals("Expecting one failed replicatedRecord, message test", "Failed to process transaction group: " + String.format(TransactionGroupProcessorImpl.MESSAGE_TEMPLATE_NO_STUB,1,"Student",1,"Enrolment") +  " and collegeId: 1", replicatedRecords.get(0).getMessage());
		/**
		 * Transaction with Merge Student 1 to 2  delete.
		 */
		transactionGroup = getTransactionGroup(2, SupportedVersions.V21);
		List<GenericReplicationStub> replicationStubs = transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		/**
		 * Adjust relationships as the angel side does it.
		 */
		for (GenericReplicationStub replicationStub : replicationStubs) {
			if (replicationStub instanceof GenericEnrolmentStub) {
				((GenericEnrolmentStub)replicationStub).setStudentId(2L);
				((ish.oncourse.webservices.v21.stubs.replication.EnrolmentStub)replicationStub).setInvoiceLineId(1L);
			}
			else if (replicationStub instanceof GenericPaymentInStub) {
				((GenericPaymentInStub) replicationStub).setContactId(2L);
			}
			else if (replicationStub instanceof GenericInvoiceStub) {
				((GenericInvoiceStub) replicationStub).setContactId(2L);
			}
		}
		replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
		assertEquals("Expecting success records, size test", 6, replicatedRecords.size());
		for (GenericReplicatedRecord replicatedRecord : replicatedRecords) {
			assertEquals("Expecting success record, status test", true, StubUtils.hasSuccessStatus(replicatedRecord));
		}
	}


	@Test
	public void testBinaryDataProcessingV17() {
		GenericTransactionGroup transactionGroup = PortHelper.createTransactionGroup(SupportedVersions.V21);
		transactionGroup.getTransactionKeys().add("2e6ebaa0c38247ea4da3ae403315c970");

		ish.oncourse.webservices.v21.stubs.replication.DocumentStub documentStub = new ish.oncourse.webservices.v21.stubs.replication.DocumentStub();
		documentStub.setDescription("Presenter's Guide");
		documentStub.setName("Presenter's Guide");
		documentStub.setFileUUID("1234567890");
		documentStub.setWebVisible(0);
		documentStub.setEntityIdentifier("Document");
		documentStub.setRemoved(false);
		documentStub.setShared(true);
		documentStub.setAngelId(422l);

		ish.oncourse.webservices.v21.stubs.replication.DocumentVersionStub documentVersionStub = new ish.oncourse.webservices.v21.stubs.replication.DocumentVersionStub();
		documentVersionStub.setEntityIdentifier("DocumentVersion");
		documentVersionStub.setAngelId(422l);
		documentVersionStub.setByteSize(464609L);
		documentVersionStub.setMimeType("application/pdf");
		documentVersionStub.setDocumentId(422l);

		ish.oncourse.webservices.v21.stubs.replication.BinaryInfoStub binaryInfoStub = new ish.oncourse.webservices.v21.stubs.replication.BinaryInfoStub();
		binaryInfoStub.setEntityIdentifier("AttachmentInfo");
		binaryInfoStub.setAngelId(422l);
		binaryInfoStub.setWebVisible(0);
		binaryInfoStub.setByteSize(464609L);
		binaryInfoStub.setMimeType("application/pdf");
		binaryInfoStub.setName("Presenter's Guide");

		ish.oncourse.webservices.v21.stubs.replication.BinaryDataStub binaryDataStub = new ish.oncourse.webservices.v21.stubs.replication.BinaryDataStub();
		binaryDataStub.setEntityIdentifier("AttachmentData");
		binaryDataStub.setAngelId(422l);
		binaryDataStub.setContent("AttachmentData".getBytes());
		binaryDataStub.setBinaryInfoId(422l);

		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(documentStub);
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(documentVersionStub);
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(binaryInfoStub);
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(binaryDataStub);
		java.util.List<GenericReplicatedRecord> records = transactionGroupProcessor.processGroup(transactionGroup);

		Long willowId = null;
		for (GenericReplicatedRecord record : records) {
			assertTrue("GenericReplicatedRecord success", StubUtils.hasSuccessStatus(record));
			if (record.getStub().getEntityIdentifier().equals("AttachmentInfo")) {
				willowId = record.getStub().getWillowId();
				assertNotNull("Willow id for AttachmentInfo", willowId);
			}
			DocumentVersion documentVersion = Cayenne.objectForPK(cayenneService.newContext(), DocumentVersion.class, 1L);
			BinaryInfo binaryInfo = Cayenne.objectForPK(cayenneService.newContext(), BinaryInfo.class, 1L);

			assertNotNull("BinaryInfo form db", binaryInfo);
			assertNotNull(documentVersion);
			assertNotNull("BinaryInfo filePath", documentVersion.getFilePath());
		}

		transactionGroup.getTransactionKeys().add("2e6ebaa0c38247ea4da3ae403315c970");

		ish.oncourse.webservices.v21.stubs.replication.DeletedStub deletedStubBI = new ish.oncourse.webservices.v21.stubs.replication.DeletedStub();
		deletedStubBI.setAngelId(422l);
		deletedStubBI.setEntityIdentifier("AttachmentInfo");

		ish.oncourse.webservices.v21.stubs.replication.DeletedStub deletedStubBD = new ish.oncourse.webservices.v21.stubs.replication.DeletedStub();
		deletedStubBD.setAngelId(422l);
		deletedStubBD.setEntityIdentifier("AttachmentData");


		transactionGroup = PortHelper.createTransactionGroup(SupportedVersions.V21);
		transactionGroup.getTransactionKeys().add("2e6ebaa0c38247ea4da3ae403315c970");
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(deletedStubBI);
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(deletedStubBD);
		records = transactionGroupProcessor.processGroup(transactionGroup);
		assertEquals("records size 2", 2, records.size());
		for (GenericReplicatedRecord record : records) {
			assertTrue("GenericReplicatedRecord success", StubUtils.hasSuccessStatus(record));
		}

		ObjectContext context = cayenneService.newContext();

		BinaryInfo binaryInfo = ObjectSelect.query(BinaryInfo.class)
				.where(BinaryInfo.ANGEL_ID.eq(422l))
				.selectOne(context);

		Document document = ObjectSelect.query(Document.class)
				.where(Document.ANGEL_ID.eq(422l))
				.selectOne(context);

		DocumentVersion documentVersion = ObjectSelect.query(DocumentVersion.class)
				.where(DocumentVersion.ANGEL_ID.eq(422l))
				.selectOne(context);

		assertNull("BinaryInfo is null", binaryInfo);
		assertNull(document);
		assertNull(documentVersion);
	}

    private GenericTransactionGroup getTransactionGroup(int fromTransaction, final SupportedVersions version) {
        List<QueuedTransaction> transactions = willowQueueService.getReplicationQueue(fromTransaction, 1);
        GenericTransactionGroup transactionGroup = PortHelper.createTransactionGroup(version);

        for (QueuedRecord record: transactions.get(0).getQueuedRecords())
        {
            GenericReplicationStub replicationStub = willowStubBuilder.convert(record, PortHelper.getVersionByTransactionGroup(transactionGroup));
            transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(replicationStub);
        }
        return transactionGroup;
    }


	@Test
	public void testMergeContactsDenyRuleV17() {
		GenericTransactionGroup transactionGroup = PortHelper.createTransactionGroup(SupportedVersions.V21);

		GenericReplicationStub deleteContactSub = PortHelper.createDeleteStub(SupportedVersions.V21);

		deleteContactSub.setWillowId(3l);
		deleteContactSub.setAngelId(3l);
		deleteContactSub.setEntityIdentifier("Contact");

		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(deleteContactSub);

		ish.oncourse.webservices.v21.stubs.replication.ContactStub updateContactSub = new ish.oncourse.webservices.v21.stubs.replication.ContactStub();
		updateContactSub.setWillowId(4l);
		updateContactSub.setAngelId(4l);
		updateContactSub.setEntityIdentifier("Contact");
		updateContactSub.setCreated(new Date());
		updateContactSub.setModified(new Date());
		updateContactSub.setFamilyName("Contact2");
		updateContactSub.setGivenName("Contact32");
		updateContactSub.setEmailAddress("1@kremlin.ru");

		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(updateContactSub);

		ish.oncourse.webservices.v21.stubs.replication.ContactRelationStub updateContactRelationSub = new ish.oncourse.webservices.v21.stubs.replication.ContactRelationStub();
		updateContactRelationSub.setWillowId(1l);
		updateContactRelationSub.setAngelId(1l);
		updateContactRelationSub.setEntityIdentifier("ContactRelation");
		updateContactRelationSub.setCreated(new Date());
		updateContactRelationSub.setModified(new Date());
		updateContactRelationSub.setFromContactId(4l);
		updateContactRelationSub.setToContactId(5l);
		updateContactRelationSub.setTypeId(1l);

		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(updateContactRelationSub);

		List<GenericReplicatedRecord> replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
		assertEquals(3, replicatedRecords.size());

		for (GenericReplicatedRecord record : replicatedRecords) {
			if (record.getStub().getEntityIdentifier().equals("ContactRelation")) {
				assertEquals((Long) 1l, record.getStub().getWillowId());
			} else if (record.getStub().getEntityIdentifier().equals("Contact")) {
				if (record.getStub().getAngelId().equals(3l)) {
					assertNull(record.getStub().getWillowId());
				} else {
					assertNotNull(record.getStub().getWillowId());
				}
			}
		}

		ObjectContext context = cayenneService.newContext();

		Contact deletedContact = ObjectSelect.query(Contact.class)
				.where(BinaryInfo.ANGEL_ID.eq(3l))
				.selectOne(context);

		assertNull(deletedContact);

		Contact remainedContact = ObjectSelect.query(Contact.class)
				.where(BinaryInfo.ANGEL_ID.eq(4l))
				.selectOne(context);

		assertNotNull(remainedContact);

		assertEquals(2, remainedContact.getToContacts().size());
	}

	@Test
	public void testCourseWithAttachmentsCreation() throws Exception {
		GenericTransactionGroup transactionGroup = PortHelper.createTransactionGroup(V21);
		CourseStub courseStub = new CourseStub();
		courseStub.setName("CourseName");
		courseStub.setAngelId(1L);
		courseStub.setEnrolmentType(1);
		courseStub.setEntityIdentifier("Course");
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(courseStub);

		DocumentStub documentStub = new DocumentStub();
		documentStub.setEntityIdentifier("Document");
		documentStub.setAngelId(1L);
		documentStub.setName("DocumentName");
		documentStub.setShared(true);
		documentStub.setWebVisible(1);
		documentStub.setRemoved(true);
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(documentStub);

		BinaryInfoRelationStub binaryInfoRelationStub = new BinaryInfoRelationStub();
		binaryInfoRelationStub.setEntityName("Course");
		binaryInfoRelationStub.setEntityIdentifier("CourseAttachmentRelation");
		binaryInfoRelationStub.setEntityAngelId(1l);
		binaryInfoRelationStub.setDocumentId(1L);
		binaryInfoRelationStub.setAngelId(1L);
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(binaryInfoRelationStub);

		transactionGroupProcessor.processGroup(transactionGroup);

		ObjectContext context = cayenneService.newContext();
		List<BinaryInfoRelation> relations = ObjectSelect.query(BinaryInfoRelation.class).select(context);
		assertEquals(1, relations.size());
		assertEquals(Long.valueOf(1), relations.get(0).getEntityAngelId());
		assertNotNull(relations.get(0).getEntityWillowId());

		Course course = ObjectSelect.query(Course.class).where(Course.ANGEL_ID.eq(1L)).selectOne(context);
		assertEquals(course.getId(), relations.get(0).getEntityWillowId());
	}

	/**
	 * Test for merging two contacts in TransactionGroupProcessor by transaction group with several
	 * ContactDuplicate stubs
	 * @throws Exception
	 */
	@Test
	public void testMergeProcessingV17() throws Exception {
    	Long updContactId = 7L;
    	Long delContactId = 6L;

		ObjectContext context = cayenneService.newContext();
		Contact delContact = ObjectSelect.query(Contact.class)
				.where(ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, delContactId)).selectOne(context);
		Contact updContact = ObjectSelect.query(Contact.class)
				.where(ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, updContactId)).selectOne(context);

		GenericTransactionGroup group = PortHelper.createTransactionGroup(V21);

		group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(
				generateContactV21Stub(updContact.getAngelId(),updContact.getId(), updContact.getGivenName(), updContact.getFamilyName()));
		group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(

				generateDeleteV21Stub("Contact", delContact.getAngelId(), delContact.getId()));
		group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(

				generateContactDuplicateV17Stub(10L,null, delContactId,14L,14L));
		group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(

				generateContactDuplicateV17Stub(11L,null, delContactId,15L,15L));
		group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(

				generateContactDuplicateV17Stub(12L,null, updContactId, delContactId, delContactId));

		group.getTransactionKeys().add("2e6ebaa0c38247ea4da3ae403315c970");
		group.getTransactionKeys().add("MERGE");

		transactionGroupProcessor.processGroup(group);

		Contact updated = ObjectSelect.query(Contact.class).where(ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, updContactId)).selectOne(context);
		Contact deleted = ObjectSelect.query(Contact.class).where(ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, delContactId)).selectOne(context);

		assertNotNull(updated);
		assertNull(deleted);
	}
}
