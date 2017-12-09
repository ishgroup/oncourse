package ish.oncourse.services.lifecycle;

import ish.oncourse.model.*;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class QueueableLifecycleRelationTest extends ServiceTest {

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceTestModule.class);
		new LoadDataSet().dataSetFile("ish/oncourse/services/lifecycle/relationDataSet.xml").load(testContext.getDS());
	}

	@Test
	public void testContactRelationUpdate() throws Exception {

		ICayenneService cs = getService(ICayenneService.class);

		ObjectContext ctx = cs.newContext();

		Student st3 = Cayenne.objectForPK(ctx, Student.class, 3);

		Tutor t5 = Cayenne.objectForPK(ctx, Tutor.class, 5);

		Contact c1 = Cayenne.objectForPK(ctx, Contact.class, 1);
		Contact c2 = Cayenne.objectForPK(ctx, Contact.class, 2);

		c1.setFamilyName("FamilyName");
		c1.setGivenName("GivenName");

		c2.setFamilyName("FamilyName");
		c2.setGivenName("GivenName");


		c1.setStudent(st3);
		c2.setTutor(t5);

		ctx.commitChanges();

		DatabaseConnection dbUnitConnection = new DatabaseConnection(testContext.getDS().getConnection(), null);
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
