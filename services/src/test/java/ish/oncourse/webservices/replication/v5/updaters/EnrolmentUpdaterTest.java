package ish.oncourse.webservices.replication.v5.updaters;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import ish.oncourse.model.College;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.Queueable;
import ish.oncourse.model.Student;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.v4.updaters.UpdaterException;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;
import ish.oncourse.webservices.v5.stubs.replication.EnrolmentStub;

public class EnrolmentUpdaterTest extends ServiceTest {
	private static Logger logger = Logger.getLogger(EnrolmentUpdaterTest.class.getName());
	
	@Before
    public void setupDataSet() throws Exception {
        initTest("ish.oncourse.webservices.services", StringUtils.EMPTY, ReplicationTestModule.class);
    }
	
	@Test
	public void testValidEnrolmentForAngelVersionNotSupportEnrolmentToManyRelation() {
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
            assertNotNull(String.format("InvoiceLine should not be empty and should be setted by updater for angel version %s lover then %s",
            	college.getAngelVersion(), Enrolment.TO_MANY_INVOICE_LINE_SUPPORT_VERSION), entity.getInvoiceLines());
		} catch (UpdaterException e) {
			logger.error( e.getMessage(), e);
            assertTrue(e.getMessage(), false);
		} finally {
			objectContext.rollbackChanges();
		}
	}
	
	@Test
	public void testInvalidEnrolmentForAngelVersionNotSupportEnrolmentToManyRelation() {
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
	
	@Test
	public void testValidEnrolmentForAngelVersionSupportEnrolmentToManyRelation() {
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
			college.setAngelVersion(Enrolment.TO_MANY_INVOICE_LINE_SUPPORT_VERSION);
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
            assertTrue(String.format("InvoiceLines list should be empty and should not be setted by updater for angel version %s lover then %s",
                college.getAngelVersion(), Enrolment.TO_MANY_INVOICE_LINE_SUPPORT_VERSION), entity.getInvoiceLines().isEmpty());
            assertNull(String.format("Original InvoiceLine should be null and should not be setted by updater for angel version %s lover then %s",
                    college.getAngelVersion(), Enrolment.TO_MANY_INVOICE_LINE_SUPPORT_VERSION), entity.getOriginalInvoiceLine());
		} catch (UpdaterException e) {
			logger.error( e.getMessage(), e);
            assertTrue(e.getMessage(), false);
		} finally {
			objectContext.rollbackChanges();
		}
	}
	
	@Test
	public void testInvalidEnrolmentForAngelVersionSupportEnrolmentToManyRelation() {
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
			college.setAngelVersion(Enrolment.TO_MANY_INVOICE_LINE_SUPPORT_VERSION);
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
