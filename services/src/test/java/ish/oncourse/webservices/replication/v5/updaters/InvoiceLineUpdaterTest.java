package ish.oncourse.webservices.replication.v5.updaters;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import ish.oncourse.model.College;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.Queueable;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.v4.updaters.UpdaterException;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;
import ish.oncourse.webservices.v5.stubs.replication.InvoiceLineStub;

import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

public class InvoiceLineUpdaterTest extends ServiceTest {
	private static Logger logger = Logger.getLogger(InvoiceLineUpdaterTest.class.getName());
	
	@Before
    public void setupDataSet() throws Exception {
        initTest("ish.oncourse.webservices.services", StringUtils.EMPTY, ReplicationTestModule.class);
    }
	
	@Test
	public void testValidInvoiceLineWithEnrolmentForAngelVersionNotSupportEnrolmentToManyRelation() {
		InvoiceLineUpdater updater = new InvoiceLineUpdater();
		InvoiceLineStub stub = new InvoiceLineStub();
		stub.setAngelId(1l);
		stub.setWillowId(1l);
		stub.setEnrolmentId(1l);
		stub.setInvoiceId(1l);
		
		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		try {
			InvoiceLine entity = objectContext.newObject(InvoiceLine.class);
			College college = objectContext.newObject(College.class);
			college.setAngelVersion("4.1b3");
			entity.setCollege(college);
			RelationShipCallback relationShipCallback = new RelationShipCallback() {
                @Override
                public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
                    if (clazz == Invoice.class && entityId == 1l) return objectContext.newObject(clazz);
                    if (clazz == Enrolment.class && entityId == 1l) return objectContext.newObject(clazz);
                    return null;
                }
            };
            updater.updateEntity(stub, entity, relationShipCallback);
            assertNotNull("Invoice should not be empty", entity.getInvoice());
            assertNull(String.format("Enrolment should not be updated for angel version %s lover then %s", college.getAngelVersion(), 
            	Enrolment.TO_MANY_INVOICE_LINE_SUPPORT_VERSION), entity.getEnrolment());
		} catch (UpdaterException e) {
			logger.error( e.getMessage(), e);
            assertTrue(e.getMessage(), false);
		} finally {
			objectContext.rollbackChanges();
		}
	}
	
	@Test
	public void testInvalidInvoiceLineWithEnrolmentForAngelVersionNotSupportEnrolmentToManyRelation() {
		InvoiceLineUpdater updater = new InvoiceLineUpdater();
		InvoiceLineStub stub = new InvoiceLineStub();
		stub.setAngelId(1l);
		stub.setWillowId(1l);
		stub.setEnrolmentId(1l);
		stub.setInvoiceId(1l);
		
		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		try {
			InvoiceLine entity = objectContext.newObject(InvoiceLine.class);
			College college = objectContext.newObject(College.class);
			college.setAngelVersion("4.1b3");
			entity.setCollege(college);
			RelationShipCallback relationShipCallback = new RelationShipCallback() {
                @Override
                public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
                    if (clazz == Invoice.class && entityId == 1l) return objectContext.newObject(clazz);
                    return null;
                }
            };
            updater.updateEntity(stub, entity, relationShipCallback);
            assertNotNull("Invoice should not be empty", entity.getInvoice());
            assertNull(String.format("Enrolment should not be updated for angel version %s lover then %s", college.getAngelVersion(), 
            	Enrolment.TO_MANY_INVOICE_LINE_SUPPORT_VERSION), entity.getEnrolment());
		} catch (UpdaterException e) {
			//updater not throw an exception if there are no related enrollment entity, only log error.
			logger.error( e.getMessage(), e);
            assertTrue(e.getMessage(), false);
		} finally {
			objectContext.rollbackChanges();
		}
	}
	
	@Test
	public void testValidInvoiceLineWithEnrolmentForAngelVersionSupportEnrolmentToManyRelation() {
		InvoiceLineUpdater updater = new InvoiceLineUpdater();
		InvoiceLineStub stub = new InvoiceLineStub();
		stub.setAngelId(1l);
		stub.setWillowId(1l);
		stub.setEnrolmentId(1l);
		stub.setInvoiceId(1l);
		
		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		try {
			InvoiceLine entity = objectContext.newObject(InvoiceLine.class);
			College college = objectContext.newObject(College.class);
			college.setAngelVersion(Enrolment.TO_MANY_INVOICE_LINE_SUPPORT_VERSION);
			entity.setCollege(college);
			RelationShipCallback relationShipCallback = new RelationShipCallback() {
                @Override
                public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
                    if (clazz == Invoice.class && entityId == 1l) return objectContext.newObject(clazz);
                    if (clazz == Enrolment.class && entityId == 1l) return objectContext.newObject(clazz);
                    return null;
                }
            };
            updater.updateEntity(stub, entity, relationShipCallback);
            assertNotNull("Invoice should not be empty", entity.getInvoice());
            assertNotNull(String.format("Enrolment should not be empty for angel version %s", college.getAngelVersion(), 
            	Enrolment.TO_MANY_INVOICE_LINE_SUPPORT_VERSION), entity.getEnrolment());
		} catch (UpdaterException e) {
			logger.error( e.getMessage(), e);
            assertTrue(e.getMessage(), false);
		} finally {
			objectContext.rollbackChanges();
		}
	}
	
	@Test
	public void testInvalidInvoiceLineWithEnrolmentForAngelVersionSupportEnrolmentToManyRelation() {
		InvoiceLineUpdater updater = new InvoiceLineUpdater();
		InvoiceLineStub stub = new InvoiceLineStub();
		stub.setAngelId(1l);
		stub.setWillowId(1l);
		stub.setEnrolmentId(1l);
		stub.setInvoiceId(1l);
		
		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		College college = objectContext.newObject(College.class);
		try {
			InvoiceLine entity = objectContext.newObject(InvoiceLine.class);
			
			college.setAngelVersion(Enrolment.TO_MANY_INVOICE_LINE_SUPPORT_VERSION);
			entity.setCollege(college);
			RelationShipCallback relationShipCallback = new RelationShipCallback() {
                @Override
                public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
                    if (clazz == Invoice.class && entityId == 1l) return objectContext.newObject(clazz);
                    return null;
                }
            };
            updater.updateEntity(stub, entity, relationShipCallback);
            assertTrue(String.format("InvoiceLineUpdater should throw en UpdaterException for college with angelversion %s", college.getAngelVersion()), false);
		} catch (UpdaterException e) {
			logger.info(e.getMessage(), e);
			assertTrue("Updater should throw an exception because enrolment missed in transaction!", 
				e.getMessage().startsWith("InvoiceLine with angelId = 1 and willowid = 1 with missed enrolment id = 1 record detected for update!"));
		} finally {
			objectContext.rollbackChanges();
		}
	}
}
