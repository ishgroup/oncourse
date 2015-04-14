package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.replication.v8.updaters.InvoiceLineUpdater;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;
import ish.oncourse.webservices.v8.stubs.replication.InvoiceLineStub;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InvoiceLineUpdaterTest extends ServiceTest {
	private static Logger logger = LogManager.getLogger();
	
	@Before
    public void setupDataSet() throws Exception {
        initTest("ish.oncourse.webservices.services", StringUtils.EMPTY, ReplicationTestModule.class);
    }
	
	@Test
	public void testValidInvoiceLineWithEnrolment() {
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
            assertNull(String.format("Enrolment should not be updated for angel version %s", college.getAngelVersion()), entity.getEnrolment());
		} catch (UpdaterException e) {
			logger.error( e.getMessage(), e);
            assertTrue(e.getMessage(), false);
		} finally {
			objectContext.rollbackChanges();
		}
	}
	
	@Test
	public void testInvalidInvoiceLineWithEnrolment() {
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
            assertNull(String.format("Enrolment should not be updated for angel version %s", college.getAngelVersion()), entity.getEnrolment());
		} catch (UpdaterException e) {
			//updater not throw an exception if there are no related enrollment entity, only log error.
			logger.error( e.getMessage(), e);
            assertTrue(e.getMessage(), false);
		} finally {
			objectContext.rollbackChanges();
		}
	}
}
