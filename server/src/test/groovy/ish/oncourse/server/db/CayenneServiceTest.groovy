/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.db

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Contact
import org.apache.cayenne.CayenneRuntimeException
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.configuration.server.ServerRuntime
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.tx.TransactionalOperation
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import java.util.concurrent.*

import static org.junit.jupiter.api.Assertions.fail

@CompileStatic
@DatabaseSetup
class CayenneServiceTest extends TestWithDatabase {

    /**
     * check if the network interface of the db comes up.
     *
     * @throws URISyntaxException
     */
    @Test
    void testInitCayenneStack() throws URISyntaxException {

        try {
            //Assertions.assertEquals("Checking cayenne context", ISHDataContext.class, dc.getClass());
            Assertions.assertEquals(true, cayenneContext.getUserProperty("replicating"), "Checking cayenne context")

            cayenneContext = cayenneService.getNewNonReplicatingContext()
            //Assertions.assertEquals("Checking cayenne context", ISHDataContext.class, dc.getClass());
            Assertions.assertEquals(false, cayenneContext.getUserProperty("replicating"), "Checking cayenne context")

        } catch (Exception e) {
            fail("the test database startup failed")
        }
    }

    /**
     * check if the network interface of the db comes up.
     *
     * @throws URISyntaxException
     */
    @Test
    void testInitCayenneStack2() throws URISyntaxException {
        //TODO Refactoring
//		DatabaseService dbService;
//		try {
//		    // load the map from XML
//			CayenneService cayService = injector.getInstance(CayenneService.class);
//			DataMap cayMap = cayService.getSharedContext().getParentDataDomain().getDataMap("AngelMap");
//
//			// Now load the map from the database
//			DbAdapter adapter = cayService.getSharedContext().getParentDataDomain().getDataNode("AngelNode").getAdapter();
//			dbService = injector.getInstance(DatabaseService.class);
//			DbLoader loader = new DbLoader(adapter, dbService.getDBAdapter().createConnection(), new DbLoaderConfiguration(), new DefaultDbLoaderDelegate(), new DefaultObjectNameGenerator());
//            DataMap dbMap = loader.load();
//
//		Assertions.assertEquals("comparing data map", cayMap.getDbEntities().size(), dbMap.getDbEntities().size());
//
//		} catch (Exception e) {
//			logger.warn("the test db startup failed", e);
//			fail("the test db startup failed");
//		}
    }

    //@Test
    
    void testTansactions() {
        ServerRuntime runtime = cayenneService.getServerRuntime()
        ExecutorService asyncThreadExecutor = Executors.newFixedThreadPool(3)

        final DataContext context1 = cayenneService.getNewContext()
        Contact contact1 = context1.newObject(Contact.class)
        contact1.setFirstName("John")
        contact1.setLastName("Smith")

        final DataContext context2 = cayenneService.getNewContext()
        Contact contact2 = context2.newObject(Contact.class)
        contact2.setFirstName("John")
        contact2.setLastName("Lie")

        TransactionalOperation operation = new TransactionalOperation() {
            @Override
            Object perform() {
                try {

                    List<Contact> contacts = ObjectSelect.query(Contact.class).select(cayenneService.getSharedContext())
                    Assertions.assertTrue(contacts.isEmpty())
                    context1.commitChanges()

                    contacts = ObjectSelect.query(Contact.class).select(cayenneService.getSharedContext())
                    Assertions.assertEquals(1, contacts.size())

                } catch (InterruptedException e) {
                    fail()
                }
                return null
            }
        }

        Future<Boolean> future = asyncThreadExecutor.submit(new Callable<Boolean>() {
            @Override
            Boolean call() throws InterruptedException {
                Thread.sleep(10000)
                context2.commitChanges()
                List<Contact> contacts = ObjectSelect.query(Contact.class).select(cayenneService.getSharedContext())
                Assertions.assertEquals(2, contacts.size())
                return true
            }
        })

        runtime.performInTransaction(operation)

        try {
            while (!future.get()) {
                //do nothing, need to keep main thread
            }
        } catch (InterruptedException | ExecutionException e) {
            fail()
        } finally {
            asyncThreadExecutor.shutdown()
        }
    }

    @Test
    void testRollback() {
        final ICayenneService cayService = injector.getInstance(ICayenneService.class)
        ServerRuntime runtime = cayService.getServerRuntime()

        final DataContext context1 = cayService.getNewContext()
        Contact contact1 = context1.newObject(Contact.class)
        contact1.setFirstName("John")
        contact1.setLastName("Smith")

        final DataContext context2 = cayService.getNewContext()
        Contact contact2 = context2.newObject(Contact.class)
        contact2.setFirstName("John")
        contact2.setLastName("Lie")
        TransactionalOperation operation = new TransactionalOperation() {
            @Override
            Object perform() {
                context1.commitChanges()
                List<Contact> contacts = ObjectSelect.query(Contact.class).select(cayService.getSharedContext())
                Assertions.assertEquals(1, contacts.size())
                context2.commitChanges()
                contacts = ObjectSelect.query(Contact.class).select(cayService.getSharedContext())
                Assertions.assertEquals(2, contacts.size())
                throw new CayenneRuntimeException("Commit failed")
            }
        }

        try {
            runtime.performInTransaction(operation)
        } catch (CayenneRuntimeException e) {
        }

        List<Contact> contacts = ObjectSelect.query(Contact.class).select(cayService.getSharedContext())
        Assertions.assertEquals(0, contacts.size())

    }
}