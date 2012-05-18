package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.Contact;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.QueuedTransaction;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.replication.builders.IWillowStubBuilder;
import ish.oncourse.webservices.replication.builders.WillowStubBuilderTest;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;
import ish.oncourse.webservices.v4.stubs.replication.*;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import javax.swing.*;
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

        InputStream st = WillowStubBuilderTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/soap/v4/referenceDataSet.xml");

        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

        DataSource refDataSource = getDataSource("jdbc/oncourse_reference");

        DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

        st = WillowStubBuilderTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/replication/services/TransactionGroupProcessorTest.xml");
        dataSet = new FlatXmlDataSetBuilder().build(st);

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
        TransactionGroup transactionGroup = getTransactionGroup(0);
        List<ReplicatedRecord> replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
        assertEquals("Expecting one failed replicatedRecord, size test", 1, replicatedRecords.size());
        assertEquals("Expecting one failed replicatedRecord, status test", Status.FAILED, replicatedRecords.get(0).getStatus());
        assertEquals("Expecting one failed replicatedRecord, message test", "Failed to process transaction group: " + String.format(TransactionGroupProcessorImpl.MESSAGE_TEMPLATE_NO_STUB,1,"Contact",1,"Invoice") +  " and collegeId: 1", replicatedRecords.get(0).getMessage());

        /**
         * Transaction with one Student delete.
         */
        transactionGroup = getTransactionGroup(1);
        replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
        assertEquals("Expecting one failed replicatedRecord, size test", 1, replicatedRecords.size());
        assertEquals("Expecting one failed replicatedRecord, status test", Status.FAILED, replicatedRecords.get(0).getStatus());
        assertEquals("Expecting one failed replicatedRecord, message test", "Failed to process transaction group: " + String.format(TransactionGroupProcessorImpl.MESSAGE_TEMPLATE_NO_STUB,1,"Student",1,"Enrolment") +  " and collegeId: 1", replicatedRecords.get(0).getMessage());


        /**
         * Transaction with Merge Student 1 to 2  delete.
         */
        transactionGroup = getTransactionGroup(2);
        List<ReplicationStub> replicationStubs = transactionGroup.getAttendanceOrBinaryDataOrBinaryInfo();
        /**
         * Adjust relationships as the angel side does it.
         */
        for (ReplicationStub replicationStub : replicationStubs) {
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
        for (ReplicatedRecord replicatedRecord : replicatedRecords) {
            assertEquals("Expecting success record, status test", Status.SUCCESS, replicatedRecord.getStatus());
        }
    }

    private TransactionGroup getTransactionGroup(int fromTransaction) {
        List<QueuedTransaction> transactions = willowQueueService.getReplicationQueue(fromTransaction, 1);
        TransactionGroup transactionGroup = new TransactionGroup();

        for (QueuedRecord record: transactions.get(0).getQueuedRecords())
        {
            ReplicationStub replicationStub = willowStubBuilder.convert(record);
            transactionGroup.getAttendanceOrBinaryDataOrBinaryInfo().add(replicationStub);
        }
        return transactionGroup;
    }
}
