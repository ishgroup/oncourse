package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.QueuedTransaction;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.replication.builders.WillowStubBuilderTest;
import ish.oncourse.webservices.replication.v4.builders.IWillowStubBuilder;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;
import ish.oncourse.webservices.util.GenericReplicatedRecord;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub;
import ish.oncourse.webservices.v4.stubs.replication.InvoiceStub;
import ish.oncourse.webservices.v4.stubs.replication.PaymentInStub;
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

import static org.junit.Assert.assertEquals;

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
    public void testDeleteObject() throws Exception {


        /**
         * Transaction with one Contact delete.
         */
    	GenericTransactionGroup transactionGroup = getTransactionGroup(0);
        List<GenericReplicatedRecord> replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
        assertEquals("Expecting one failed replicatedRecord, size test", 1, replicatedRecords.size());
        assertEquals("Expecting one failed replicatedRecord, status test", true, replicatedRecords.get(0).isFailedStatus());
        assertEquals("Expecting one failed replicatedRecord, message test", "Failed to process transaction group: " + String.format(TransactionGroupProcessorImpl.MESSAGE_TEMPLATE_NO_STUB,1,"Contact",1,"Invoice") +  " and collegeId: 1", replicatedRecords.get(0).getMessage());

        /**
         * Transaction with one Student delete.
         */
        transactionGroup = getTransactionGroup(1);
        replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
        assertEquals("Expecting one failed replicatedRecord, size test", 1, replicatedRecords.size());
        assertEquals("Expecting one failed replicatedRecord, status test", true, replicatedRecords.get(0).isFailedStatus());
        assertEquals("Expecting one failed replicatedRecord, message test", "Failed to process transaction group: " + String.format(TransactionGroupProcessorImpl.MESSAGE_TEMPLATE_NO_STUB,1,"Student",1,"Enrolment") +  " and collegeId: 1", replicatedRecords.get(0).getMessage());


        /**
         * Transaction with Merge Student 1 to 2  delete.
         */
        transactionGroup = getTransactionGroup(2);
        List<GenericReplicationStub> replicationStubs = transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo();
        /**
         * Adjust relationships as the angel side does it.
         */
        for (GenericReplicationStub replicationStub : replicationStubs) {
            if (replicationStub instanceof EnrolmentStub)
            {
                ((EnrolmentStub)replicationStub).setStudentId(2L);
            }
            else if (replicationStub instanceof PaymentInStub)
            {
                ((PaymentInStub) replicationStub).setContactId(2L);
            }
            else if (replicationStub instanceof InvoiceStub)
            {
                ((InvoiceStub) replicationStub).setContactId(2L);
            }
        }
        replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
        assertEquals("Expecting success records, size test", 5, replicatedRecords.size());
        for (GenericReplicatedRecord replicatedRecord : replicatedRecords) {
            assertEquals("Expecting success record, status test", true, replicatedRecord.isSuccessStatus());
        }
    }

    private GenericTransactionGroup getTransactionGroup(int fromTransaction) {
        List<QueuedTransaction> transactions = willowQueueService.getReplicationQueue(fromTransaction, 1);
        GenericTransactionGroup transactionGroup = PortHelper.createTransactionGroup(SupportedVersions.V4);

        for (QueuedRecord record: transactions.get(0).getQueuedRecords())
        {
            GenericReplicationStub replicationStub = willowStubBuilder.convert(record,PortHelper.getVersionByTransactionGroup(transactionGroup));
            transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(replicationStub);
        }
        return transactionGroup;
    }
}
