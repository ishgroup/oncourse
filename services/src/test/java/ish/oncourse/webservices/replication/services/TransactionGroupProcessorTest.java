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
import ish.oncourse.webservices.v6.stubs.replication.BinaryDataStub;
import ish.oncourse.webservices.v6.stubs.replication.BinaryInfoStub;
import ish.oncourse.webservices.v6.stubs.replication.DeletedStub;
import ish.oncourse.webservices.v6.stubs.replication.EnrolmentStub;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
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
    public void testDeleteV6Object() throws Exception {
    	setup();
        /**
         * Transaction with one Contact delete.
         */
    	GenericTransactionGroup transactionGroup = getTransactionGroup(0, SupportedVersions.V6);
        List<GenericReplicatedRecord> replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
        assertEquals("Expecting one failed replicatedRecord, size test", 1, replicatedRecords.size());
        assertEquals("Expecting one failed replicatedRecord, status test", true, StubUtils.hasFailedStatus(replicatedRecords.get(0)));
        assertEquals("Expecting one failed replicatedRecord, message test", "Failed to process transaction group: " + String.format(TransactionGroupProcessorImpl.MESSAGE_TEMPLATE_NO_STUB,1,"Contact",1,"Invoice") +  " and collegeId: 1", replicatedRecords.get(0).getMessage());
        /**
         * Transaction with one Student delete.
         */
        transactionGroup = getTransactionGroup(1, SupportedVersions.V6);
        replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
        assertEquals("Expecting one failed replicatedRecord, size test", 1, replicatedRecords.size());
        assertEquals("Expecting one failed replicatedRecord, status test", true, StubUtils.hasFailedStatus(replicatedRecords.get(0)));
        assertEquals("Expecting one failed replicatedRecord, message test", "Failed to process transaction group: " + String.format(TransactionGroupProcessorImpl.MESSAGE_TEMPLATE_NO_STUB,1,"Student",1,"Enrolment") +  " and collegeId: 1", replicatedRecords.get(0).getMessage());
        /**
         * Transaction with Merge Student 1 to 2  delete.
         */
        transactionGroup = getTransactionGroup(2, SupportedVersions.V6);
        List<GenericReplicationStub> replicationStubs = transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo();
        /**
         * Adjust relationships as the angel side does it.
         */
        for (GenericReplicationStub replicationStub : replicationStubs) {
            if (replicationStub instanceof GenericEnrolmentStub) {
                ((GenericEnrolmentStub)replicationStub).setStudentId(2L);
                ((EnrolmentStub)replicationStub).setInvoiceLineId(1L);
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
    public void testBinaryDataProcessing() {
        GenericTransactionGroup transactionGroup = PortHelper.createTransactionGroup(SupportedVersions.V6);
        transactionGroup.getTransactionKeys().add("2e6ebaa0c38247ea4da3ae403315c970");
        BinaryInfoStub binaryInfoStub = new BinaryInfoStub();
        binaryInfoStub.setEntityIdentifier("AttachmentInfo");
        binaryInfoStub.setAngelId(422l);
        binaryInfoStub.setWebVisible(0);
        binaryInfoStub.setByteSize(464609L);
        binaryInfoStub.setMimeType("application/pdf");
        binaryInfoStub.setName("Presenter's Guide");

        BinaryDataStub binaryDataStub = new BinaryDataStub();
        binaryDataStub.setEntityIdentifier("AttachmentData");
        binaryDataStub.setAngelId(422l);
        binaryDataStub.setContent("AttachmentData".getBytes());
        binaryDataStub.setBinaryInfoId(422l);

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

        DeletedStub deletedStubBI = new DeletedStub();
        deletedStubBI.setAngelId(422l);
        deletedStubBI.setEntityIdentifier("AttachmentInfo");

        DeletedStub deletedStubBD = new DeletedStub();
        deletedStubBD.setAngelId(422l);
        deletedStubBD.setEntityIdentifier("AttachmentData");


        transactionGroup = PortHelper.createTransactionGroup(SupportedVersions.V6);
        transactionGroup.getTransactionKeys().add("2e6ebaa0c38247ea4da3ae403315c970");
        transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(deletedStubBI);
        transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(deletedStubBD);
        records = transactionGroupProcessor.processGroup(transactionGroup);
		assertEquals("records size 2", 2, records.size());
        for (GenericReplicatedRecord record : records) {
            assertTrue("GenericReplicatedRecord success", StubUtils.hasSuccessStatus(record));
        }

		ObjectContext context = cayenneService.newContext();
		
		BinaryInfo binaryInfo = (BinaryInfo) Cayenne.objectForQuery(context, 
				new SelectQuery(BinaryInfo.class, ExpressionFactory.matchExp(BinaryInfo.ANGEL_ID_PROPERTY, 422l)));
		Document document = (Document) Cayenne.objectForQuery(context,
				new SelectQuery(DocumentVersion.class, ExpressionFactory.matchExp(Document.ANGEL_ID_PROPERTY, 422l)));
        DocumentVersion documentVersion = (DocumentVersion) Cayenne.objectForQuery(context,
				new SelectQuery(DocumentVersion.class, ExpressionFactory.matchExp(DocumentVersion.ANGEL_ID_PROPERTY, 422l)));
		
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
}
