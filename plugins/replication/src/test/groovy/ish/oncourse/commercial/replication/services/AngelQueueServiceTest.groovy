/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.services

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.oncourse.commercial.replication.cayenne.QueuedTransaction
import ish.oncourse.server.ICayenneService
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals

/**
 */
@CompileStatic
class AngelQueueServiceTest extends CayenneIshTestCase {

	private AngelQueueService service

    @Before
    void setup() throws Exception {
		wipeTables()
        InputStream st = AngelQueueServiceTest.class.getClassLoader().getResourceAsStream("ish/oncourse/commercial/replication/services/angelQueueServiceTest.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)
        executeDatabaseOperation(dataSet)

        ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
        this.service = new AngelQueueService(cayenneService)
    }

	@Test
    void testAngelQueue() throws Exception {
		int numberOfTransactions = service.getNumberOfTransactions()
        assertEquals("Expecting 2 transactions.", 2, numberOfTransactions)

        List<QueuedTransaction> transactions = service.getReplicationQueue(0, 2)
        assertEquals("Expecting 2 transactions.", 2, transactions.size())

        int numberOfRecords = 0
        for (QueuedTransaction t : transactions) {
			numberOfRecords += t.getQueuedRecords().size()
        }

		assertEquals("Expecting 8 records.", 8, numberOfRecords)

        transactions = service.getReplicationQueue(-1, 4)
        assertEquals("Expecting 2 transactions.", 2, transactions.size())

        transactions = service.getReplicationQueue(200, 4)
        assertEquals("Expecting 0 transactions.", 0, transactions.size())
    }
}
