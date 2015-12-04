package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.replication.builders.IWillowStubBuilder;
import ish.oncourse.webservices.replication.builders.WillowStubBuilderTest;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;
import ish.oncourse.webservices.util.*;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class TransactionGroupProcessorTest extends ServiceTest {
	WillowQueueService willowQueueService;
    IWillowStubBuilder willowStubBuilder;
    ITransactionGroupProcessor transactionGroupProcessor;
    ICayenneService cayenneService;

    @Before
    public void setup() throws Exception {
		initTest("ish.oncourse.webservices.services", "", ReplicationTestModule.class);

		InputStream st = WillowStubBuilderTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/replication/services/TransactionGroupProcessorTest.xml");
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

        DataSource onDataSource = getDataSource("jdbc/oncourse");
        DatabaseConnection dbConnection = new DatabaseConnection(onDataSource.getConnection(), null);
        dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

        DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
        willowQueueService = new WillowQueueService(getObject(IWebSiteService.class, null), getService(ICayenneService.class));
        willowStubBuilder = getService(IWillowStubBuilder.class);
        transactionGroupProcessor = getService(ITransactionGroupProcessor.class);
        cayenneService = getService(ICayenneService.class);

    }
    
    @Test
    public void testDeleteV7Object() throws Exception {
        /**
         * Transaction with one Contact delete.
         */
    	GenericTransactionGroup transactionGroup = getTransactionGroup(0, SupportedVersions.V7);
        List<GenericReplicatedRecord> replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
        assertEquals("Expecting one failed replicatedRecord, size test", 1, replicatedRecords.size());
        assertEquals("Expecting one failed replicatedRecord, status test", true, StubUtils.hasFailedStatus(replicatedRecords.get(0)));
        assertEquals("Expecting one failed replicatedRecord, message test", "Failed to process transaction group: " + String.format(TransactionGroupProcessorImpl.MESSAGE_TEMPLATE_NO_STUB, 1, "Contact", 1, "Invoice") + " and collegeId: 1", replicatedRecords.get(0).getMessage());
        /**
         * Transaction with one Student delete.
         */
        transactionGroup = getTransactionGroup(1, SupportedVersions.V7);
        replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
        assertEquals("Expecting one failed replicatedRecord, size test", 1, replicatedRecords.size());
        assertEquals("Expecting one failed replicatedRecord, status test", true, StubUtils.hasFailedStatus(replicatedRecords.get(0)));
        assertEquals("Expecting one failed replicatedRecord, message test", "Failed to process transaction group: " + String.format(TransactionGroupProcessorImpl.MESSAGE_TEMPLATE_NO_STUB,1,"Student",1,"Enrolment") +  " and collegeId: 1", replicatedRecords.get(0).getMessage());
        /**
         * Transaction with Merge Student 1 to 2  delete.
         */
        transactionGroup = getTransactionGroup(2, SupportedVersions.V7);
        List<GenericReplicationStub> replicationStubs = transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo();
        /**
         * Adjust relationships as the angel side does it.
         */
        for (GenericReplicationStub replicationStub : replicationStubs) {
            if (replicationStub instanceof GenericEnrolmentStub) {
                ((GenericEnrolmentStub)replicationStub).setStudentId(2L);
                ((ish.oncourse.webservices.v7.stubs.replication.EnrolmentStub)replicationStub).setInvoiceLineId(1L);

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
    public void testDeleteV11Object() throws Exception {
    	setup();
        /**
         * Transaction with one Contact delete.
         */
    	GenericTransactionGroup transactionGroup = getTransactionGroup(0, SupportedVersions.V11);
        List<GenericReplicatedRecord> replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
        assertEquals("Expecting one failed replicatedRecord, size test", 1, replicatedRecords.size());
        assertEquals("Expecting one failed replicatedRecord, status test", true, StubUtils.hasFailedStatus(replicatedRecords.get(0)));
        assertEquals("Expecting one failed replicatedRecord, message test", "Failed to process transaction group: " + String.format(TransactionGroupProcessorImpl.MESSAGE_TEMPLATE_NO_STUB,1,"Contact",1,"Invoice") +  " and collegeId: 1", replicatedRecords.get(0).getMessage());
        /**
         * Transaction with one Student delete.
         */
        transactionGroup = getTransactionGroup(1, SupportedVersions.V11);
        replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
        assertEquals("Expecting one failed replicatedRecord, size test", 1, replicatedRecords.size());
        assertEquals("Expecting one failed replicatedRecord, status test", true, StubUtils.hasFailedStatus(replicatedRecords.get(0)));
        assertEquals("Expecting one failed replicatedRecord, message test", "Failed to process transaction group: " + String.format(TransactionGroupProcessorImpl.MESSAGE_TEMPLATE_NO_STUB,1,"Student",1,"Enrolment") +  " and collegeId: 1", replicatedRecords.get(0).getMessage());
        /**
         * Transaction with Merge Student 1 to 2  delete.
         */
        transactionGroup = getTransactionGroup(2, SupportedVersions.V11);
        List<GenericReplicationStub> replicationStubs = transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo();
        /**
         * Adjust relationships as the angel side does it.
         */
        for (GenericReplicationStub replicationStub : replicationStubs) {
            if (replicationStub instanceof GenericEnrolmentStub) {
                ((GenericEnrolmentStub)replicationStub).setStudentId(2L);
                ((ish.oncourse.webservices.v11.stubs.replication.EnrolmentStub)replicationStub).setInvoiceLineId(1L);
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
	public void testDeleteV12Object() throws Exception {
		setup();
		/**
		 * Transaction with one Contact delete.
		 */
		GenericTransactionGroup transactionGroup = getTransactionGroup(0, SupportedVersions.V12);
		List<GenericReplicatedRecord> replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
		assertEquals("Expecting one failed replicatedRecord, size test", 1, replicatedRecords.size());
		assertEquals("Expecting one failed replicatedRecord, status test", true, StubUtils.hasFailedStatus(replicatedRecords.get(0)));
		assertEquals("Expecting one failed replicatedRecord, message test", "Failed to process transaction group: " + String.format(TransactionGroupProcessorImpl.MESSAGE_TEMPLATE_NO_STUB,1,"Contact",1,"Invoice") +  " and collegeId: 1", replicatedRecords.get(0).getMessage());
		/**
		 * Transaction with one Student delete.
		 */
		transactionGroup = getTransactionGroup(1, SupportedVersions.V12);
		replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
		assertEquals("Expecting one failed replicatedRecord, size test", 1, replicatedRecords.size());
		assertEquals("Expecting one failed replicatedRecord, status test", true, StubUtils.hasFailedStatus(replicatedRecords.get(0)));
		assertEquals("Expecting one failed replicatedRecord, message test", "Failed to process transaction group: " + String.format(TransactionGroupProcessorImpl.MESSAGE_TEMPLATE_NO_STUB,1,"Student",1,"Enrolment") +  " and collegeId: 1", replicatedRecords.get(0).getMessage());
		/**
		 * Transaction with Merge Student 1 to 2  delete.
		 */
		transactionGroup = getTransactionGroup(2, SupportedVersions.V12);
		List<GenericReplicationStub> replicationStubs = transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		/**
		 * Adjust relationships as the angel side does it.
		 */
		for (GenericReplicationStub replicationStub : replicationStubs) {
			if (replicationStub instanceof GenericEnrolmentStub) {
				((GenericEnrolmentStub)replicationStub).setStudentId(2L);
				((ish.oncourse.webservices.v12.stubs.replication.EnrolmentStub)replicationStub).setInvoiceLineId(1L);
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
    public void testBinaryDataProcessingV11() {
        GenericTransactionGroup transactionGroup = PortHelper.createTransactionGroup(SupportedVersions.V11);
        transactionGroup.getTransactionKeys().add("2e6ebaa0c38247ea4da3ae403315c970");

		ish.oncourse.webservices.v12.stubs.replication.DocumentStub documentStub = new ish.oncourse.webservices.v12.stubs.replication.DocumentStub();
		documentStub.setDescription("Presenter's Guide");
		documentStub.setName("Presenter's Guide");
		documentStub.setFileUUID("1234567890");
		documentStub.setWebVisible(0);
		documentStub.setEntityIdentifier("Document");
		documentStub.setRemoved(false);
		documentStub.setShared(true);
		documentStub.setAngelId(422l);

		ish.oncourse.webservices.v12.stubs.replication.DocumentVersionStub documentVersionStub = new ish.oncourse.webservices.v12.stubs.replication.DocumentVersionStub();
		documentVersionStub.setEntityIdentifier("DocumentVersion");
		documentVersionStub.setAngelId(422l);
		documentVersionStub.setByteSize(464609L);
		documentVersionStub.setMimeType("application/pdf");
		documentVersionStub.setDocumentId(422l);

		ish.oncourse.webservices.v12.stubs.replication.BinaryInfoStub binaryInfoStub = new ish.oncourse.webservices.v12.stubs.replication.BinaryInfoStub();
		binaryInfoStub.setEntityIdentifier("AttachmentInfo");
		binaryInfoStub.setAngelId(422l);
		binaryInfoStub.setWebVisible(0);
		binaryInfoStub.setByteSize(464609L);
		binaryInfoStub.setMimeType("application/pdf");
		binaryInfoStub.setName("Presenter's Guide");

		ish.oncourse.webservices.v12.stubs.replication.BinaryDataStub binaryDataStub = new ish.oncourse.webservices.v12.stubs.replication.BinaryDataStub();
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
            DocumentVersion documentVersion = Cayenne.objectForPK(cayenneService.sharedContext(), DocumentVersion.class, 1L);
			BinaryInfo binaryInfo = Cayenne.objectForPK(cayenneService.sharedContext(), BinaryInfo.class, 1L);

			assertNotNull("BinaryInfo form db", binaryInfo);
			assertNotNull(documentVersion);
            assertNotNull("BinaryInfo filePath", documentVersion.getFilePath());
        }

        transactionGroup.getTransactionKeys().add("2e6ebaa0c38247ea4da3ae403315c970");

		ish.oncourse.webservices.v12.stubs.replication.DeletedStub deletedStubBI = new ish.oncourse.webservices.v12.stubs.replication.DeletedStub();
        deletedStubBI.setAngelId(422l);
        deletedStubBI.setEntityIdentifier("AttachmentInfo");

		ish.oncourse.webservices.v12.stubs.replication.DeletedStub deletedStubBD = new ish.oncourse.webservices.v12.stubs.replication.DeletedStub();
        deletedStubBD.setAngelId(422l);
        deletedStubBD.setEntityIdentifier("AttachmentData");


        transactionGroup = PortHelper.createTransactionGroup(SupportedVersions.V11);
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

	@Test
	public void testBinaryDataProcessingV12() {
		GenericTransactionGroup transactionGroup = PortHelper.createTransactionGroup(SupportedVersions.V12);
		transactionGroup.getTransactionKeys().add("2e6ebaa0c38247ea4da3ae403315c970");

		ish.oncourse.webservices.v12.stubs.replication.DocumentStub documentStub = new ish.oncourse.webservices.v12.stubs.replication.DocumentStub();
		documentStub.setDescription("Presenter's Guide");
		documentStub.setName("Presenter's Guide");
		documentStub.setFileUUID("1234567890");
		documentStub.setWebVisible(0);
		documentStub.setEntityIdentifier("Document");
		documentStub.setRemoved(false);
		documentStub.setShared(true);
		documentStub.setAngelId(422l);

		ish.oncourse.webservices.v12.stubs.replication.DocumentVersionStub documentVersionStub = new ish.oncourse.webservices.v12.stubs.replication.DocumentVersionStub();
		documentVersionStub.setEntityIdentifier("DocumentVersion");
		documentVersionStub.setAngelId(422l);
		documentVersionStub.setByteSize(464609L);
		documentVersionStub.setMimeType("application/pdf");
		documentVersionStub.setDocumentId(422l);

		ish.oncourse.webservices.v12.stubs.replication.BinaryInfoStub binaryInfoStub = new ish.oncourse.webservices.v12.stubs.replication.BinaryInfoStub();
		binaryInfoStub.setEntityIdentifier("AttachmentInfo");
		binaryInfoStub.setAngelId(422l);
		binaryInfoStub.setWebVisible(0);
		binaryInfoStub.setByteSize(464609L);
		binaryInfoStub.setMimeType("application/pdf");
		binaryInfoStub.setName("Presenter's Guide");

		ish.oncourse.webservices.v12.stubs.replication.BinaryDataStub binaryDataStub = new ish.oncourse.webservices.v12.stubs.replication.BinaryDataStub();
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
			DocumentVersion documentVersion = Cayenne.objectForPK(cayenneService.sharedContext(), DocumentVersion.class, 1L);
			BinaryInfo binaryInfo = Cayenne.objectForPK(cayenneService.sharedContext(), BinaryInfo.class, 1L);

			assertNotNull("BinaryInfo form db", binaryInfo);
			assertNotNull(documentVersion);
			assertNotNull("BinaryInfo filePath", documentVersion.getFilePath());
		}

		transactionGroup.getTransactionKeys().add("2e6ebaa0c38247ea4da3ae403315c970");

		ish.oncourse.webservices.v12.stubs.replication.DeletedStub deletedStubBI = new ish.oncourse.webservices.v12.stubs.replication.DeletedStub();
		deletedStubBI.setAngelId(422l);
		deletedStubBI.setEntityIdentifier("AttachmentInfo");

		ish.oncourse.webservices.v12.stubs.replication.DeletedStub deletedStubBD = new ish.oncourse.webservices.v12.stubs.replication.DeletedStub();
		deletedStubBD.setAngelId(422l);
		deletedStubBD.setEntityIdentifier("AttachmentData");


		transactionGroup = PortHelper.createTransactionGroup(SupportedVersions.V12);
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
	public void testMergeContactsDenyRuleV11() {
		GenericTransactionGroup transactionGroup = PortHelper.createTransactionGroup(SupportedVersions.V11);

		GenericReplicationStub deleteContactSub = PortHelper.createDeleteStub(SupportedVersions.V11);

		deleteContactSub.setWillowId(3l);
		deleteContactSub.setAngelId(3l);
		deleteContactSub.setEntityIdentifier("Contact");

		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(deleteContactSub);

		ish.oncourse.webservices.v11.stubs.replication.ContactStub updateContactSub = new ish.oncourse.webservices.v11.stubs.replication.ContactStub();
		updateContactSub.setWillowId(4l);
		updateContactSub.setAngelId(4l);
		updateContactSub.setEntityIdentifier("Contact");
		updateContactSub.setCreated(new Date());
		updateContactSub.setModified(new Date());
		updateContactSub.setFamilyName("Contact2");
		updateContactSub.setGivenName("Contact32");
		updateContactSub.setEmailAddress("1@kremlin.ru");

		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(updateContactSub);

		ish.oncourse.webservices.v11.stubs.replication.ContactRelationStub updateContactRelationSub = new ish.oncourse.webservices.v11.stubs.replication.ContactRelationStub();
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
	public void testMergeContactsDenyRuleV12() {
		GenericTransactionGroup transactionGroup = PortHelper.createTransactionGroup(SupportedVersions.V12);

		GenericReplicationStub deleteContactSub = PortHelper.createDeleteStub(SupportedVersions.V12);

		deleteContactSub.setWillowId(3l);
		deleteContactSub.setAngelId(3l);
		deleteContactSub.setEntityIdentifier("Contact");

		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(deleteContactSub);

		ish.oncourse.webservices.v12.stubs.replication.ContactStub updateContactSub = new ish.oncourse.webservices.v12.stubs.replication.ContactStub();
		updateContactSub.setWillowId(4l);
		updateContactSub.setAngelId(4l);
		updateContactSub.setEntityIdentifier("Contact");
		updateContactSub.setCreated(new Date());
		updateContactSub.setModified(new Date());
		updateContactSub.setFamilyName("Contact2");
		updateContactSub.setGivenName("Contact32");
		updateContactSub.setEmailAddress("1@kremlin.ru");

		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(updateContactSub);

		ish.oncourse.webservices.v12.stubs.replication.ContactRelationStub updateContactRelationSub = new ish.oncourse.webservices.v12.stubs.replication.ContactRelationStub();
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
}
