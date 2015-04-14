package ish.oncourse.listeners;

import ish.oncourse.model.Qualification;
import ish.oncourse.model.TrainingPackage;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;

import static org.junit.Assert.*;

public class IshVersionListenerTest extends ServiceTest {
	
	private ICayenneService cayenneService;

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceModule.class);

		InputStream st = IshVersionListenerTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/services/lifecycle/referenceDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource refDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

		this.cayenneService = getService(ICayenneService.class);
	}
	
	@Test
	public void testReferenceObjectNotChanged() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		
		Qualification q = Cayenne.objectForPK(context, Qualification.class, 1);
		
		IshVersionHolder.setIshVersion(500L);
		
		Long ishVersion = q.getIshVersion();
		q.setNationalCode("10351TAS");
		q.setLevelCode(null);
		
		context.commitChanges();
		
		assertTrue(q.getIshVersion().equals(ishVersion));
	}
	
	@Test
	public void testReferenceObjectPropertyChanged() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		
		Qualification q = Cayenne.objectForPK(context, Qualification.class, 1);
		IshVersionHolder.setIshVersion(500L);
		q.setNationalCode("TEST123");
		
		context.commitChanges();
		
		assertTrue(q.getIshVersion().equals(500L));
	}
	
	@Test
	public void testReferenceObjectNullPropertyChanged() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		
		Qualification q = Cayenne.objectForPK(context, Qualification.class, 1);
		TrainingPackage tp = Cayenne.objectForPK(context, TrainingPackage.class, 1);
		
		IshVersionHolder.setIshVersion(500L);
		
		assertNull(q.getLevelCode());
		assertNotNull(tp.getTitle());
		
		q.setLevelCode("123");
		tp.setTitle(null);
		
		context.commitChanges();
		
		assertTrue(q.getIshVersion().equals(500L));
		assertTrue(tp.getIshVersion().equals(500L));
	}
}
