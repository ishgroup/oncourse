package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.QueuedRecord;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.SupportedVersions;
import ish.oncourse.webservices.v6.stubs.replication.EnrolmentStub;
import org.apache.cayenne.Cayenne;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class WillowStubBuilderTest extends ServiceTest {
	
	@Before
	public void setupDataSet() throws Exception {
		initTest("ish.oncourse.webservices.services", "", ReplicationTestModule.class);

		InputStream st = WillowStubBuilderTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/replication/builders/stubBuilderDataSet.xml");
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

		DataSource onDataSource = getDataSource("jdbc/oncourse");
		DatabaseConnection dbConnection = new DatabaseConnection(onDataSource.getConnection(), null);
		dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);
		
		DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
	}
	
	@Test
	public void testOnlyFullStubs() {
		
		ICayenneService cayenneService = getService(ICayenneService.class);
		
		QueuedRecord record = Cayenne.objectForPK(cayenneService.sharedContext(), QueuedRecord.class, 1l);
		
		IWillowStubBuilder builder = getService(IWillowStubBuilder.class);
		
		GenericReplicationStub replStub = builder.convert(record, SupportedVersions.V6);
		
		assertNotNull("Expecting not null willow id.", replStub.getWillowId());
		assertTrue("Expecting EnrolmentStub.", replStub instanceof EnrolmentStub);
		
		EnrolmentStub enrlStub = (EnrolmentStub) replStub;
		
		assertTrue("Checking courseClass id.", enrlStub.getCourseClassId().equals(1482l));
		assertTrue("Checking student id.", enrlStub.getStudentId().equals(1540l));
	}
}
