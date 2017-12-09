package ish.oncourse.listeners;

import ish.oncourse.model.Qualification;
import ish.oncourse.model.TrainingPackage;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class IshVersionListenerTest extends ServiceTest {
	
	private ICayenneService cayenneService;

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceTestModule.class);
		new LoadDataSet().dataSetFile("ish/oncourse/services/lifecycle/referenceDataSet.xml").load(testContext.getDS());
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
