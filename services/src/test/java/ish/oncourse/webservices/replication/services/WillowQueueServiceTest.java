package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.QueuedTransaction;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.replication.builders.WillowStubBuilderTest;
import ish.oncourse.webservices.soap.ReplicationTestModule;
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

public class WillowQueueServiceTest extends ServiceTest {

	private WillowQueueService service;
	
	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.webservices.services", "", ReplicationTestModule.class);
		
		InputStream st = WillowStubBuilderTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/replication/services/willowQueueServiceTest.xml");
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

		DataSource onDataSource = getDataSource("jdbc/oncourse");
		DatabaseConnection dbConnection = new DatabaseConnection(onDataSource.getConnection(), null);
		dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);
		
		DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
		
		this.service = new WillowQueueService(getObject(IWebSiteService.class, null), getService(ICayenneService.class));
	}

	@Test
	public void testWillowQueue() throws Exception {
		
		int numberOfTransactions = service.getNumberOfTransactions();
		assertEquals("Expecting 2 transactions.", 2, numberOfTransactions);

		List<QueuedTransaction> transactions = service.getReplicationQueue(0, 2);
		assertEquals("Expecting 2 transactions.", 2, transactions.size());

		int numberOfRecords = 0;
		for (QueuedTransaction t : transactions) {
			numberOfRecords += t.getQueuedRecords().size();
		}

		assertEquals("Expecting 6 records.", 6, numberOfRecords);

		transactions = service.getReplicationQueue(-1, 4);
		assertEquals("Expecting 2 transactions.", 2, transactions.size());

		transactions = service.getReplicationQueue(200, 4);
		assertEquals("Expecting 0 transactions.", 0, transactions.size());
	}
}
