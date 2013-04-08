package ish.oncourse.webservices.replication.v4.updaters;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Message;
import ish.oncourse.model.MessagePerson;
import ish.oncourse.model.Queueable;
import ish.oncourse.model.Student;
import ish.oncourse.model.Tutor;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;
import ish.oncourse.webservices.v4.stubs.replication.MessagePersonStub;

public class MessagePersonUpdaterTest extends ServiceTest {
	private static Logger logger = Logger.getLogger(MessagePersonUpdaterTest.class.getName());
	
	@Before
    public void setupDataSet() throws Exception {
        initTest("ish.oncourse.webservices.services", StringUtils.EMPTY, ReplicationTestModule.class);
    }
	
	@Test
	public void testValidMessagePersonWithMessage() {
		MessagePersonUpdater updater = new MessagePersonUpdater();
		MessagePersonStub stub = new MessagePersonStub();
		stub.setContactId(1L);
		stub.setMessageId(1l);
		stub.setType(2);//sms
		stub.setStatus(2);//sent
		stub.setStudentId(1L);
		stub.setAngelId(1L);
		
		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		MessagePerson messagePerson = objectContext.newObject(MessagePerson.class);
		try {
			updater.updateEntityFromStub(stub, messagePerson, getRelationShipCallback(objectContext));
			assertNotNull("update should passed correct", messagePerson.getMessage());
		} catch (UpdaterException e) {
			logger.info( e.getMessage(), e);
			assertTrue("The updater should not throw this kind of exception", false);
		} catch (IllegalStateException e) {
			logger.info( e.getMessage(), e);
			assertTrue("The updater should not throw this kind of exception", false);
		} finally {
            objectContext.rollbackChanges();
        }
	}
	
	@Test
	public void testValidMessagePersonWithoutMessage() {
		MessagePersonUpdater updater = new MessagePersonUpdater();
		MessagePersonStub stub = new MessagePersonStub();
		stub.setContactId(1L);
		stub.setMessageId(null);
		stub.setType(2);//sms
		stub.setStatus(1);//queued
		stub.setStudentId(1L);
		stub.setAngelId(1L);
		
		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		MessagePerson messagePerson = objectContext.newObject(MessagePerson.class);
		try {
			updater.updateEntityFromStub(stub, messagePerson, getRelationShipCallback(objectContext));
			if (messagePerson.getMessage() != null) {
				assertTrue("The updater should throw exception", false);
			}
		} catch (UpdaterException e) {
			logger.info( e.getMessage(), e);
			assertTrue("test error message", e.getMessage().contains("No message object found for message person with angelid"));
		} finally {
            objectContext.rollbackChanges();
        }
	}
	
	private RelationShipCallback getRelationShipCallback(final ObjectContext objectContext) {
		return new RelationShipCallback() {
            @Override
            public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
                if (clazz == Contact.class) return objectContext.newObject(clazz);
                if (clazz == Message.class) return objectContext.newObject(clazz);
                if (clazz == Student.class) return objectContext.newObject(clazz);
                if (clazz == Tutor.class) return objectContext.newObject(clazz);
                return null;
            }
        };
	}
	@Test
	public void testInvalidMessagePersonWithMessage() {
		MessagePersonUpdater updater = new MessagePersonUpdater();
		MessagePersonStub stub = new MessagePersonStub();
		stub.setContactId(1L);
		stub.setMessageId(1l);
		stub.setType(2);//sms
		stub.setStatus(2);//sent
		stub.setStudentId(1L);
		stub.setAngelId(1L);
		
		MessagePersonStub stub2 = new MessagePersonStub();
		stub2.setContactId(1L);
		stub2.setMessageId(1l);
		stub2.setType(2);//sms
		stub2.setStatus(1);//sent
		stub2.setStudentId(1L);
		stub2.setAngelId(1L);
		
		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		MessagePerson messagePerson = objectContext.newObject(MessagePerson.class);
		try {
			updater.updateEntityFromStub(stub, messagePerson, getRelationShipCallback(objectContext));
			assertNotNull("update should passed correct", messagePerson.getMessage());
			updater.updateEntityFromStub(stub2, messagePerson, getRelationShipCallback(objectContext));
			assertTrue("The updater should throw exception", false);
		} catch (UpdaterException e) {
			logger.info( e.getMessage(), e);
			assertTrue("The updater should not throw this kind of exception", false);
		} catch (IllegalArgumentException e) {
			logger.info( e.getMessage(), e);
			assertTrue("test error message", e.getMessage().contains("Can't set the queued status for MessagePerson with sent status and id = "));
		}catch (IllegalStateException e) {
			logger.info( e.getMessage(), e);
			assertTrue("test error message", e.getMessage().contains(
				"We should not allow to change status from SENT to any other state! But try to change it to"));
		} finally {
            objectContext.rollbackChanges();
        }
	}
}
