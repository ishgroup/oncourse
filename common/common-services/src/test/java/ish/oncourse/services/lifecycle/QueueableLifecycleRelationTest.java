package ish.oncourse.services.lifecycle;

import ish.oncourse.model.*;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;

import static org.junit.Assert.assertTrue;

public class QueueableLifecycleRelationTest extends ServiceTest {

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceModule.class);

		InputStream st = QueueableLifecycleListenerTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/services/lifecycle/relationDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource refDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);
	}

	@Test
	public void testContactRelationUpdate() throws Exception {

		ICayenneService cs = getService(ICayenneService.class);

		ObjectContext ctx = cs.newContext();

		Student st3 = Cayenne.objectForPK(ctx, Student.class, 3);

		Tutor t5 = Cayenne.objectForPK(ctx, Tutor.class, 5);

		Contact c1 = Cayenne.objectForPK(ctx, Contact.class, 1);
		Contact c2 = Cayenne.objectForPK(ctx, Contact.class, 2);

		c1.setStudent(st3);
		c2.setTutor(t5);

		ctx.commitChanges();

		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord"));

		assertTrue(actualData.getRowCount() > 0);

	}

	@Test
	public void testWaitingList() throws Exception {
		ICayenneService cs = getService(ICayenneService.class);

		ObjectContext ctx = cs.newContext();
		Student st4 = Cayenne.objectForPK(ctx, Student.class, 3);
		
		WaitingList wl = Cayenne.objectForPK(ctx, WaitingList.class, 1);
		st4.removeFromWaitingLists(wl);
		
		ctx.commitChanges();
	}
	
	@Test
	public void testRemoveWaitingList() throws Exception {
		ICayenneService cs = getService(ICayenneService.class);

		ObjectContext ctx = cs.newContext();
		
		WaitingListSite ws = Cayenne.objectForPK(ctx, WaitingListSite.class, 2);
		ctx.deleteObject(ws);
		
		WaitingList wl = Cayenne.objectForPK(ctx, WaitingList.class, 2);
		ctx.deleteObject(wl);
		
		ctx.commitChanges();
	}
}
