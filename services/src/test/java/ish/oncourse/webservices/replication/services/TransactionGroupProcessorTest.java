package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.Contact;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.QueuedTransaction;
import ish.oncourse.model.Student;
import ish.oncourse.model.Tutor;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.replication.builders.WillowStubBuilderTest;
import ish.oncourse.webservices.replication.v4.builders.IWillowStubBuilder;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;
import ish.oncourse.webservices.util.GenericEnrolmentStub;
import ish.oncourse.webservices.util.GenericInvoiceStub;
import ish.oncourse.webservices.util.GenericPaymentInStub;
import ish.oncourse.webservices.util.GenericReplicatedRecord;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;;

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
    public void testCayenneFailWhenSetToOneRelationShip() throws Exception {
    	GenericTransactionGroup transactionGroup = getTransactionGroup(6, SupportedVersions.V4);
    	@SuppressWarnings("rawtypes")
    	List result = cayenneService.sharedContext().performQuery(new SelectQuery(Contact.class, ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 3L)
        	.andExp(ExpressionFactory.matchDbExp(Contact.STUDENT_PROPERTY + "." + Student.ID_PK_COLUMN, 3L))));
        assertEquals("Expected 1 contact matching the expression", 1, result.size());
        assertNotNull("Expected contact have matching the expression student", ((Contact) result.get(0)).getStudent());
        assertEquals("Expected contact have matching the expression student", 3L, ((Contact) result.get(0)).getStudent().getId().longValue());
        result = cayenneService.sharedContext().performQuery(new SelectQuery(Contact.class, ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 4L)));
        assertEquals("Expected 1 contact matching the expression", 1, result.size());
        assertNull("Expected contact have not student", ((Contact) result.get(0)).getStudent());
        result = cayenneService.sharedContext().performQuery(new SelectQuery(Student.class, ExpressionFactory.matchDbExp(Student.ID_PK_COLUMN, 3L)
            .andExp(ExpressionFactory.matchDbExp(Student.CONTACT_PROPERTY + "." + Contact.ID_PK_COLUMN, 3L))));
        assertEquals("Expected 1 student matching the expression", 1, result.size());
        assertNotNull("Expected student have matching the expression contact", ((Student) result.get(0)).getContact());
        assertEquals("Expected student have matching the expression contact", 3L, ((Student) result.get(0)).getContact().getId().longValue());
        //change the relationship to resolve
        GenericReplicationStub deleteStub = null;
        for (GenericReplicationStub stub : transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
        	if ("Contact".equals(stub.getEntityIdentifier()) && stub.getWillowId().equals(4L)) {
        		((ish.oncourse.webservices.v4.stubs.replication.ContactStub) stub).setStudentId(3L);
        	}
        	if ("Student".equals(stub.getEntityIdentifier()) && stub.getWillowId().equals(3L)) {
        		((ish.oncourse.webservices.v4.stubs.replication.StudentStub) stub).setContactId(4L);
        	}
        }
        transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().remove(deleteStub);
        List<GenericReplicatedRecord> replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
        assertEquals("Expecting all the success replicatedRecord, size test", 2, replicatedRecords.size());
        assertEquals("Expecting first success replicatedRecord, status test", true, replicatedRecords.get(0).isSuccessStatus());
        assertEquals("Expecting second success replicatedRecord, status test", true, replicatedRecords.get(1).isSuccessStatus());
        result = cayenneService.sharedContext().performQuery(new SelectQuery(Contact.class, ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 4L)
            .andExp(ExpressionFactory.matchDbExp(Contact.STUDENT_PROPERTY + "." + Student.ID_PK_COLUMN, 3L))));
        assertEquals("Expected 1 contact matching the expression", 1, result.size());
        assertNotNull("Expected contact have matching the expression student", ((Contact) result.get(0)).getStudent());
        assertEquals("Expected contact matching the expression", 3L, ((Contact) result.get(0)).getStudent().getId().longValue());
        result = cayenneService.newNonReplicatingContext().performQuery(new SelectQuery(Contact.class, 
        	ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 3L)));
        assertEquals("Expected 1 contact matching the expression", 1, result.size());
        result = cayenneService.sharedContext().performQuery(new SelectQuery(Student.class, ExpressionFactory.matchDbExp(Student.ID_PK_COLUMN, 3L)
            .andExp(ExpressionFactory.matchDbExp(Student.CONTACT_PROPERTY + "." + Contact.ID_PK_COLUMN, 4L))));
        assertEquals("Expected 1 student matching the expression", 1, result.size());
        assertNotNull("Expected student have matching the expression contact", ((Student) result.get(0)).getContact());
        assertEquals("Expected student matching the expression", 4L, ((Student) result.get(0)).getContact().getId().longValue());
    }
    
    //@Test
    public void testMergeContactWithTutor() throws Exception {
    	GenericTransactionGroup transactionGroup = getTransactionGroup(5, SupportedVersions.V4);
    	@SuppressWarnings("rawtypes")
    	List result = cayenneService.sharedContext().performQuery(new SelectQuery(Contact.class, ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 7L)
            	.andExp(ExpressionFactory.matchDbExp(Contact.TUTOR_PROPERTY + "." + Tutor.ID_PK_COLUMN, 7L))));
        assertEquals("Expected 1 contact matching the expression", 1, result.size());
        assertNotNull("Expected contact have matching the expression student", ((Contact) result.get(0)).getTutor());
        assertEquals("Expected contact have matching the expression student", 7L, ((Contact) result.get(0)).getTutor().getId().longValue());
        result = cayenneService.sharedContext().performQuery(new SelectQuery(Contact.class, ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 8L)));
        assertEquals("Expected 1 contact matching the expression", 1, result.size());
        assertNull("Expected contact have not student", ((Contact) result.get(0)).getStudent());
        result = cayenneService.sharedContext().performQuery(new SelectQuery(Tutor.class, ExpressionFactory.matchDbExp(Tutor.ID_PK_COLUMN, 7L)
            .andExp(ExpressionFactory.matchDbExp(Tutor.CONTACT_PROPERTY + "." + Contact.ID_PK_COLUMN, 7L))));
        assertEquals("Expected 1 student matching the expression", 1, result.size());
        assertNotNull("Expected student have matching the expression contact", ((Tutor) result.get(0)).getContact());
        assertEquals("Expected student have matching the expression contact", 7L, ((Tutor) result.get(0)).getContact().getId().longValue());
    	//change the relationship to resolve
        for (GenericReplicationStub stub : transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
        	if ("Contact".equals(stub.getEntityIdentifier()) && stub.getWillowId().equals(8L)) {
        		((ish.oncourse.webservices.v4.stubs.replication.ContactStub) stub).setTutorId(7L);
        	}
        	if ("Tutor".equals(stub.getEntityIdentifier()) && stub.getWillowId().equals(7L)) {
        		((ish.oncourse.webservices.v4.stubs.replication.TutorStub) stub).setContactId(8L);
        	}
        }
        List<GenericReplicatedRecord> replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
        assertEquals("Expecting all the success replicatedRecord, size test", 3, replicatedRecords.size());
        assertEquals("Expecting first success replicatedRecord, status test", true, replicatedRecords.get(0).isSuccessStatus());
        assertEquals("Expecting second success replicatedRecord, status test", true, replicatedRecords.get(1).isSuccessStatus());
        assertEquals("Expecting third success replicatedRecord, status test", true, replicatedRecords.get(2).isSuccessStatus());
        
        result = cayenneService.newNonReplicatingContext().performQuery(new SelectQuery(Contact.class, ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 8L)
            	.andExp(ExpressionFactory.matchDbExp(Contact.TUTOR_PROPERTY + "." + Tutor.ID_PK_COLUMN, 7L))));
        assertEquals("Expected 1 contact matching the expression", 1, result.size());
        result = cayenneService.newNonReplicatingContext().performQuery(new SelectQuery(Contact.class, ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 7L)));
        assertEquals("Expected 0 contact matching the expression", 0, result.size());
        result = cayenneService.newNonReplicatingContext().performQuery(new SelectQuery(Tutor.class, ExpressionFactory.matchDbExp(Tutor.ID_PK_COLUMN, 7L)
            	.andExp(ExpressionFactory.matchDbExp(Tutor.CONTACT_PROPERTY + "." + Contact.ID_PK_COLUMN, 8L))));
        assertEquals("Expected 1 contact matching the expression", 1, result.size());
        transactionGroup = getTransactionGroup(7, SupportedVersions.V4);
    }
    
    //@Test
    public void testMergeContactWithStudent() throws Exception {
    	GenericTransactionGroup transactionGroup = getTransactionGroup(3, SupportedVersions.V4);
    	@SuppressWarnings("rawtypes")
    	List result = cayenneService.sharedContext().performQuery(new SelectQuery(Contact.class, ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 3L)
        	.andExp(ExpressionFactory.matchDbExp(Contact.STUDENT_PROPERTY + "." + Student.ID_PK_COLUMN, 3L))));
        assertEquals("Expected 1 contact matching the expression", 1, result.size());
        assertNotNull("Expected contact have matching the expression student", ((Contact) result.get(0)).getStudent());
        assertEquals("Expected contact have matching the expression student", 3L, ((Contact) result.get(0)).getStudent().getId().longValue());
        result = cayenneService.sharedContext().performQuery(new SelectQuery(Contact.class, ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 4L)));
        assertEquals("Expected 1 contact matching the expression", 1, result.size());
        assertNull("Expected contact have not student", ((Contact) result.get(0)).getStudent());
        result = cayenneService.sharedContext().performQuery(new SelectQuery(Student.class, ExpressionFactory.matchDbExp(Student.ID_PK_COLUMN, 3L)
            .andExp(ExpressionFactory.matchDbExp(Student.CONTACT_PROPERTY + "." + Contact.ID_PK_COLUMN, 3L))));
        assertEquals("Expected 1 student matching the expression", 1, result.size());
        assertNotNull("Expected student have matching the expression contact", ((Student) result.get(0)).getContact());
        assertEquals("Expected student have matching the expression contact", 3L, ((Student) result.get(0)).getContact().getId().longValue());
        //change the relationship to resolve
        for (GenericReplicationStub stub : transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
        	if ("Contact".equals(stub.getEntityIdentifier()) && stub.getWillowId().equals(4L)) {
        		((ish.oncourse.webservices.v4.stubs.replication.ContactStub) stub).setStudentId(3L);
        	}
        	if ("Student".equals(stub.getEntityIdentifier()) && stub.getWillowId().equals(3L)) {
        		((ish.oncourse.webservices.v4.stubs.replication.StudentStub) stub).setContactId(4L);
        	}
        }
        List<GenericReplicatedRecord> replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
        assertEquals("Expecting all the success replicatedRecord, size test", 3, replicatedRecords.size());
        assertEquals("Expecting first success replicatedRecord, status test", false, replicatedRecords.get(0).isSuccessStatus());
        assertEquals("Expecting second success replicatedRecord, status test", false, replicatedRecords.get(1).isSuccessStatus());
        assertEquals("Expecting third success replicatedRecord, status test", false, replicatedRecords.get(2).isSuccessStatus());
        /*final Expression contact4Student3expression = ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 4L)
        	.andExp(ExpressionFactory.matchDbExp(Contact.STUDENT_PROPERTY + "." + Student.ID_PK_COLUMN, 3L));
        final Expression student3Contact4expression = ExpressionFactory.matchDbExp(Student.ID_PK_COLUMN, 3L)
        	.andExp(ExpressionFactory.matchDbExp(Student.CONTACT_PROPERTY + "." + Contact.ID_PK_COLUMN, 4L));
		result = cayenneService.sharedContext().performQuery(new SelectQuery(Contact.class, contact4Student3expression));
        assertEquals("Expected 1 contact matching the expression", 1, result.size());
        assertNotNull("Expected contact have matching the expression student", ((Contact) result.get(0)).getStudent());
        assertEquals("Expected contact matching the expression", 3L, ((Contact) result.get(0)).getStudent().getId().longValue());
        result = cayenneService.newNonReplicatingContext().performQuery(new SelectQuery(Contact.class, ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 3L)));
        assertEquals("Expected 0 contact matching the expression", 0, result.size());
        result = cayenneService.sharedContext().performQuery(new SelectQuery(Student.class, student3Contact4expression));
        assertEquals("Expected 1 student matching the expression", 1, result.size());
        assertNotNull("Expected student have matching the expression contact", ((Student) result.get(0)).getContact());
        assertEquals("Expected student matching the expression", 4L, ((Student) result.get(0)).getContact().getId().longValue());*/
        assertEquals("Expecting first failed replicatedRecord, message test", 
        	"Failed to process transaction group: " + String.format(TransactionGroupProcessorImpl.MESSAGE_TEMPLATE_NO_STUB,3,"Contact",3,"Student") +  " and collegeId: 1", replicatedRecords.get(0).getMessage());
        assertEquals("Expecting first failed replicatedRecord, message test", 
        	"Failed to process transaction group: " + String.format(TransactionGroupProcessorImpl.MESSAGE_TEMPLATE_NO_STUB,3,"Contact",3,"Student") +  " and collegeId: 1", replicatedRecords.get(1).getMessage());
        assertEquals("Expecting first failed replicatedRecord, message test", 
            "Failed to process transaction group: " + String.format(TransactionGroupProcessorImpl.MESSAGE_TEMPLATE_NO_STUB,3,"Contact",3,"Student") +  " and collegeId: 1", replicatedRecords.get(2).getMessage());

        transactionGroup = getTransactionGroup(4, SupportedVersions.V4);
        result = cayenneService.sharedContext().performQuery(new SelectQuery(Contact.class, ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 5L)
        	.andExp(ExpressionFactory.matchDbExp(Contact.STUDENT_PROPERTY + "." + Student.ID_PK_COLUMN, 5L))));
        assertEquals("Expected 1 contact matching the expression", 1, result.size());
        assertNotNull("Expected contact have matching the expression student", ((Contact) result.get(0)).getStudent());
        assertEquals("Expected contact have matching the expression student", 5L, ((Contact) result.get(0)).getStudent().getId().longValue());
        result = cayenneService.sharedContext().performQuery(new SelectQuery(Contact.class, ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 6L)
        		.andExp(ExpressionFactory.matchDbExp(Contact.STUDENT_PROPERTY + "." + Student.ID_PK_COLUMN, 6L))));
        assertEquals("Expected 1 contact matching the expression", 1, result.size());
        assertNotNull("Expected contact have matching the expression student", ((Contact) result.get(0)).getStudent());
        assertEquals("Expected contact have matching the expression student", 6L, ((Contact) result.get(0)).getStudent().getId().longValue());
        result = cayenneService.sharedContext().performQuery(new SelectQuery(Student.class, ExpressionFactory.matchDbExp(Student.ID_PK_COLUMN, 5L)
            	.andExp(ExpressionFactory.matchDbExp(Student.CONTACT_PROPERTY + "." + Contact.ID_PK_COLUMN, 5L))));
        assertEquals("Expected 1 student matching the expression", 1, result.size());
        assertNotNull("Expected student have matching the expression contact", ((Student) result.get(0)).getContact());
        assertEquals("Expected student have matching the expression contact", 5L, ((Student) result.get(0)).getContact().getId().longValue());
        result = cayenneService.sharedContext().performQuery(new SelectQuery(Student.class, ExpressionFactory.matchDbExp(Student.ID_PK_COLUMN, 6L)
            	.andExp(ExpressionFactory.matchDbExp(Student.CONTACT_PROPERTY + "." + Contact.ID_PK_COLUMN, 6L))));
        assertEquals("Expected 1 student matching the expression", 1, result.size());
        assertNotNull("Expected student have matching the expression contact", ((Student) result.get(0)).getContact());
        assertEquals("Expected student have matching the expression contact", 6L, ((Student) result.get(0)).getContact().getId().longValue());
        //change the relationship to resolve
        for (GenericReplicationStub stub : transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
        	if ("Contact".equals(stub.getEntityIdentifier()) && stub.getWillowId().equals(5L)) {
        		((ish.oncourse.webservices.v4.stubs.replication.ContactStub) stub).setStudentId(6L);
        	}
        	if ("Student".equals(stub.getEntityIdentifier()) && stub.getWillowId().equals(6L)) {
        		((ish.oncourse.webservices.v4.stubs.replication.StudentStub) stub).setContactId(5L);
        	}
        }
        replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
        assertEquals("Expecting all the failed replicatedRecord, size test", 4, replicatedRecords.size());
        assertEquals("Expecting first failed replicatedRecord, status test", false, replicatedRecords.get(0).isSuccessStatus());
        assertEquals("Expecting second failed replicatedRecord, status test", false, replicatedRecords.get(1).isSuccessStatus());
        assertEquals("Expecting third failed replicatedRecord, status test", false, replicatedRecords.get(2).isSuccessStatus());
        assertEquals("Expecting fourth failed replicatedRecord, status test", false, replicatedRecords.get(3).isSuccessStatus());
        
        /*final Expression contact5Student6expression = ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 5L)
        	.andExp(ExpressionFactory.matchDbExp(Contact.STUDENT_PROPERTY + "." + Student.ID_PK_COLUMN, 6L));
        final Expression student6Contact5expression = ExpressionFactory.matchDbExp(Student.ID_PK_COLUMN, 6L)
        	.andExp(ExpressionFactory.matchDbExp(Student.CONTACT_PROPERTY + "." + Contact.ID_PK_COLUMN, 5L));
        result = cayenneService.newNonReplicatingContext().performQuery(new SelectQuery(Contact.class, contact5Student6expression));
        assertEquals("Expected 1 contact matching the expression", 1, result.size());
        result = cayenneService.newNonReplicatingContext().performQuery(new SelectQuery(Contact.class, ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 6L)));
        assertEquals("Expected 0 contact matching the expression", 0, result.size());
        result = cayenneService.newNonReplicatingContext().performQuery(new SelectQuery(Student.class, student6Contact5expression));
        assertEquals("Expected 1 contact matching the expression", 1, result.size());
        result = cayenneService.newNonReplicatingContext().performQuery(new SelectQuery(Student.class, ExpressionFactory.matchDbExp(Student.ID_PK_COLUMN, 5L)));
        assertEquals("Expected 0 contact matching the expression", 0, result.size());*/

    }

    @Test
    public void testDeleteV4Object() throws Exception {
        /**
         * Transaction with one Contact delete.
         */
    	GenericTransactionGroup transactionGroup = getTransactionGroup(0, SupportedVersions.V4);
        List<GenericReplicatedRecord> replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
        assertEquals("Expecting one failed replicatedRecord, size test", 1, replicatedRecords.size());
        assertEquals("Expecting one failed replicatedRecord, status test", true, replicatedRecords.get(0).isFailedStatus());
        assertEquals("Expecting one failed replicatedRecord, message test", "Failed to process transaction group: " + String.format(TransactionGroupProcessorImpl.MESSAGE_TEMPLATE_NO_STUB,1,"Contact",1,"Invoice") +  " and collegeId: 1", replicatedRecords.get(0).getMessage());
        /**
         * Transaction with one Student delete.
         */
        transactionGroup = getTransactionGroup(1, SupportedVersions.V4);
        replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
        assertEquals("Expecting one failed replicatedRecord, size test", 1, replicatedRecords.size());
        assertEquals("Expecting one failed replicatedRecord, status test", true, replicatedRecords.get(0).isFailedStatus());
        assertEquals("Expecting one failed replicatedRecord, message test", "Failed to process transaction group: " + String.format(TransactionGroupProcessorImpl.MESSAGE_TEMPLATE_NO_STUB,1,"Student",1,"Enrolment") +  " and collegeId: 1", replicatedRecords.get(0).getMessage());
        /**
         * Transaction with Merge Student 1 to 2  delete.
         */
        transactionGroup = getTransactionGroup(2, SupportedVersions.V4);
        List<GenericReplicationStub> replicationStubs = transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo();
        /**
         * Adjust relationships as the angel side does it.
         */
        for (GenericReplicationStub replicationStub : replicationStubs) {
            if (replicationStub instanceof GenericEnrolmentStub) {
                ((GenericEnrolmentStub)replicationStub).setStudentId(2L);
            }
            else if (replicationStub instanceof GenericPaymentInStub) {
                ((GenericPaymentInStub) replicationStub).setContactId(2L);
            }
            else if (replicationStub instanceof GenericInvoiceStub) {
                ((GenericInvoiceStub) replicationStub).setContactId(2L);
            }
        }
        replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
        assertEquals("Expecting success records, size test", 5, replicatedRecords.size());
        for (GenericReplicatedRecord replicatedRecord : replicatedRecords) {
            assertEquals("Expecting success record, status test", true, replicatedRecord.isSuccessStatus());
        }
    }
    
    @Test
    public void testDeleteV5Object() throws Exception {
    	setup();
        /**
         * Transaction with one Contact delete.
         */
    	GenericTransactionGroup transactionGroup = getTransactionGroup(0, SupportedVersions.V5);
        List<GenericReplicatedRecord> replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
        assertEquals("Expecting one failed replicatedRecord, size test", 1, replicatedRecords.size());
        assertEquals("Expecting one failed replicatedRecord, status test", true, replicatedRecords.get(0).isFailedStatus());
        assertEquals("Expecting one failed replicatedRecord, message test", "Failed to process transaction group: " + String.format(TransactionGroupProcessorImpl.MESSAGE_TEMPLATE_NO_STUB,1,"Contact",1,"Invoice") +  " and collegeId: 1", replicatedRecords.get(0).getMessage());
        /**
         * Transaction with one Student delete.
         */
        transactionGroup = getTransactionGroup(1, SupportedVersions.V5);
        replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
        assertEquals("Expecting one failed replicatedRecord, size test", 1, replicatedRecords.size());
        assertEquals("Expecting one failed replicatedRecord, status test", true, replicatedRecords.get(0).isFailedStatus());
        assertEquals("Expecting one failed replicatedRecord, message test", "Failed to process transaction group: " + String.format(TransactionGroupProcessorImpl.MESSAGE_TEMPLATE_NO_STUB,1,"Student",1,"Enrolment") +  " and collegeId: 1", replicatedRecords.get(0).getMessage());
        /**
         * Transaction with Merge Student 1 to 2  delete.
         */
        transactionGroup = getTransactionGroup(2, SupportedVersions.V5);
        List<GenericReplicationStub> replicationStubs = transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo();
        /**
         * Adjust relationships as the angel side does it.
         */
        for (GenericReplicationStub replicationStub : replicationStubs) {
            if (replicationStub instanceof GenericEnrolmentStub) {
                ((GenericEnrolmentStub)replicationStub).setStudentId(2L);
            }
            else if (replicationStub instanceof GenericPaymentInStub) {
                ((GenericPaymentInStub) replicationStub).setContactId(2L);
            }
            else if (replicationStub instanceof GenericInvoiceStub) {
                ((GenericInvoiceStub) replicationStub).setContactId(2L);
            }
        }
        replicatedRecords = transactionGroupProcessor.processGroup(transactionGroup);
        assertEquals("Expecting success records, size test", 5, replicatedRecords.size());
        for (GenericReplicatedRecord replicatedRecord : replicatedRecords) {
            assertEquals("Expecting success record, status test", true, replicatedRecord.isSuccessStatus());
        }
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
