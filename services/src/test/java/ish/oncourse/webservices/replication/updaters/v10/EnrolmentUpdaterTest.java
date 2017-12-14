package ish.oncourse.webservices.replication.updaters.v10;

import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.tapestry.ServiceTest;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.updaters.UpdaterException;
import ish.oncourse.webservices.replication.v10.updaters.EnrolmentUpdater;
import ish.oncourse.webservices.soap.ReplicationTestModule;
import ish.oncourse.webservices.v10.stubs.replication.EnrolmentStub;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EnrolmentUpdaterTest extends ServiceTest {
	private static Logger logger = LogManager.getLogger(EnrolmentUpdaterTest.class.getName());
	
	@Before
    public void setupDataSet() throws Exception {
        initTest("ish.oncourse.webservices.services", StringUtils.EMPTY, ReplicationTestModule.class);
    }
	
	@Test
	public void testValidEnrolment() {
		EnrolmentUpdater updater = new EnrolmentUpdater();
		EnrolmentStub stub = new EnrolmentStub();
		stub.setAngelId(1l);
		stub.setWillowId(1l);
		stub.setInvoiceLineId(1l);
		stub.setCourseClassId(1l);
		stub.setStudentId(1l);
		stub.setStatus("IN_TRANSACTION");
		
		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		try {
			Enrolment entity = objectContext.newObject(Enrolment.class);
			College college = objectContext.newObject(College.class);
			college.setAngelVersion("4.1b3");
			entity.setCollege(college);
			RelationShipCallback relationShipCallback = new RelationShipCallback() {
                @Override
                public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
                    if (clazz == CourseClass.class && entityId == 1l) return objectContext.newObject(clazz);
                    if (clazz == Student.class && entityId == 1l) return objectContext.newObject(clazz);
                    if (clazz == InvoiceLine.class && entityId == 1l) return objectContext.newObject(clazz);
                    return null;
                }
            };
            updater.updateEntity(stub, entity, relationShipCallback);
            assertNotNull("Course class should not be empty", entity.getCourseClass());
            assertNotNull("Student should not be empty", entity.getStudent());
            assertNotNull(String.format("InvoiceLine should not be empty and should be setted by updater for angel version %s",
            	college.getAngelVersion()), entity.getInvoiceLines());
		} catch (UpdaterException e) {
			logger.error( e.getMessage(), e);
            assertTrue(e.getMessage(), false);
		} finally {
			objectContext.rollbackChanges();
		}
	}
	
	@Test
	public void testInvalidEnrolment() {
		EnrolmentUpdater updater = new EnrolmentUpdater();
		EnrolmentStub stub = new EnrolmentStub();
		stub.setAngelId(1l);
		stub.setWillowId(1l);
		stub.setInvoiceLineId(1l);
		stub.setCourseClassId(1l);
		stub.setStudentId(1l);
		stub.setStatus("IN_TRANSACTION");
		
		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		try {
			Enrolment entity = objectContext.newObject(Enrolment.class);
			College college = objectContext.newObject(College.class);
			college.setAngelVersion("4.1b3");
			entity.setCollege(college);
			RelationShipCallback relationShipCallback = new RelationShipCallback() {
                @Override
                public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
                    if (clazz == CourseClass.class && entityId == 1l) return objectContext.newObject(clazz);
                    if (clazz == Student.class && entityId == 1l) return objectContext.newObject(clazz);
                    return null;
                }
            };
            updater.updateEntity(stub, entity, relationShipCallback);
            assertTrue(String.format("EnrolmentUpdater should throw en UpdaterException for college with angelversion %s", college.getAngelVersion()), false);
		} catch (UpdaterException e) {
			logger.info( e.getMessage(), e);
			assertTrue("Updater should throw an exception because invoiceline missed in transaction!", 
				e.getMessage().startsWith("Enrollment with angelId = 1 and willowid = 1 with missed original invoiceline with id = 1 record detected"));
		} finally {
			objectContext.rollbackChanges();
		}
	}

}
