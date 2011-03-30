package ish.oncourse.webservices.services.replication;

import ish.oncourse.model.QueuedRecord;
import ish.oncourse.services.AbstractDatabaseTest;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.webservices.builders.replication.IWillowStubBuilder;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

import java.io.InputStream;

import javax.sql.DataSource;

import org.apache.cayenne.Cayenne;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

public class WillowStubBuilderTest extends AbstractDatabaseTest {

	@Before
	public void setupDataSet() throws Exception {
	
		InputStream st = WillowStubBuilderTest.class.getClassLoader().getResourceAsStream("reference/referenceDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

		DataSource refDataSource = getDataSource("jdbc/oncourse_reference");

		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

		st = WillowStubBuilderTest.class.getClassLoader().getResourceAsStream("stubbuilder/stubBuilderDataSet.xml");
		dataSet = new FlatXmlDataSetBuilder().build(st);

		DataSource onDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), dataSet);
	}
	
	@Test
	public void testOnlyFullStubs() {
		
		WillowStubBuilderFactory factory = getService(WillowStubBuilderFactory.class);
		ICayenneService cayenneService = getService(ICayenneService.class);
		
		QueuedRecord record = Cayenne.objectForPK(cayenneService.sharedContext(), QueuedRecord.class, 1l);
		
		IWillowStubBuilder builder = factory.newReplicationStubBuilder();
		
		ReplicationStub replStub = builder.convert(record);
	}
}
