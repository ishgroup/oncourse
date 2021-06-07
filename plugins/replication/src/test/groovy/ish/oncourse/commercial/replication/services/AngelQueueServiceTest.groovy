/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.services

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.oncourse.commercial.replication.cayenne.QueuedTransaction
import ish.oncourse.server.ICayenneService
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*


/**
 */
@CompileStatic
@DatabaseSetup(value = 'ish/oncourse/commercial/replication/services/angelQueueServiceTest.xml')
class AngelQueueServiceTest extends TestWithDatabase {



	@Test
    void testAngelQueue() throws Exception {
        AngelQueueService service = new AngelQueueService(cayenneService)
		int numberOfTransactions = service.getNumberOfTransactions()
        assertEquals( 2, numberOfTransactions, "Expecting 2 transactions.")

        List<QueuedTransaction> transactions = service.getReplicationQueue(0, 2)
        assertEquals( 2, transactions.size(),"Expecting 2 transactions.")

        int numberOfRecords = 0
        for (QueuedTransaction t : transactions) {
			numberOfRecords += t.getQueuedRecords().size()
        }

		assertEquals( 8, numberOfRecords)

        transactions = service.getReplicationQueue(-1, 4)
        assertEquals( 2, transactions.size())

        transactions = service.getReplicationQueue(200, 4)
        assertEquals( 0, transactions.size())
    }
}
