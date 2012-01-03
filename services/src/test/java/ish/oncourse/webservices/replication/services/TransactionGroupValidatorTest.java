package ish.oncourse.webservices.replication.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.jobs.PaymentInExpireJobTest;
import ish.oncourse.webservices.replication.builders.ITransactionStubBuilder;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;
import ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub;
import ish.oncourse.webservices.v4.stubs.replication.InvoiceStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationRecords;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;

public class TransactionGroupValidatorTest extends ServiceTest {

	private IReplicationService replicationService;
	
	private ITransactionGroupValidator transactionGroupValidator;
	
	private ITransactionStubBuilder transactionBuilder;
	
	private ICayenneService cayenneService;
	
	
	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.webservices.services", "", ReplicationTestModule.class);
		InputStream st = PaymentInExpireJobTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/webservices/replication/services/transactionGroupValidatorDataSet.xml");
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource refDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);
		
		this.replicationService = getService(IReplicationService.class);
		this.transactionBuilder = getService(ITransactionStubBuilder.class);
		this.cayenneService = getService(ICayenneService.class);
		this.replicationService = getService(IReplicationService.class);
		this.transactionGroupValidator = new TransactionGroupValidatorImpl(transactionBuilder, cayenneService);
	}
	
	@Test
	public void testValidateAndReturnFixedGroups() throws Exception {
		ObjectContext objectContext = cayenneService.newContext();
	
		PaymentIn p = Cayenne.objectForPK(objectContext, PaymentIn.class, 2000);
		p.abandonPayment();
		objectContext.commitChanges();
		
		ReplicationRecords result = replicationService.getRecords();
		List<TransactionGroup> groups = result.getGroups();
		assertTrue("Groups size is one.", groups.size() == 1);
		
		//removing invoices from the group
		TransactionGroup group = groups.get(0);
		List<ReplicationStub> stubs = group.getAttendanceOrBinaryDataOrBinaryInfo();
		int initialSize = stubs.size();
		
		//break payment's transaction group by removing invoices
		for (ReplicationStub stub : new ArrayList<ReplicationStub>(stubs)) {
			if (stub instanceof InvoiceStub || stub instanceof InvoiceLineStub) {
				stubs.remove(stub);
			}
		}
		
		int afterRemoveSize = group.getAttendanceOrBinaryDataOrBinaryInfo().size();
		assertTrue("Records were removed", initialSize > afterRemoveSize);
		
		groups = transactionGroupValidator.validateAndReturnFixedGroups(Collections.singletonList(group));
		assertEquals("Group size eq 1", 1, groups.size());
		
		int afterFixSize = groups.get(0).getAttendanceOrBinaryDataOrBinaryInfo().size();
		assertTrue("Records were added again.", afterRemoveSize < afterFixSize);
		
		boolean invoiceFound = false;
		boolean invoiceLineFound = false;
		
		for (ReplicationStub stub : groups.get(0).getAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof InvoiceStub) {
				invoiceFound = true;
			}
			else if (stub instanceof InvoiceLineStub) {
				invoiceLineFound = true;
			}
		}
		
		assertTrue("Invoice found in group.", invoiceFound);
		assertTrue("InvoiceLine found in group.", invoiceLineFound);
	}
}
