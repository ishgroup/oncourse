package ish.oncourse.server.upgrades.liquibase.change

import static org.junit.Assert.*
import org.junit.Test

import java.text.SimpleDateFormat

class IshTaskChangeUtilsTest {

    @Test
    void testDeleteQueryBatch() {

        // even size ids list test
        Long[] evenRow = [1L, 2L, 3L, 4L, 5L, 6L]
        List<String> queries = IshTaskChangeUtils.getDeleteQueryBatch("Table", Arrays.asList(evenRow), 3)
        assertEquals(2, queries.size())

        assertEquals( "DELETE FROM Table WHERE id IN (1,2,3)" ,queries.get(0))
        assertEquals("DELETE FROM Table WHERE id IN (4,5,6)", queries.get(1))

        // odd size ids list test
        Long[] oddRow = [1L, 2L, 3L, 4L, 5L, 6L, 7L]
        queries = IshTaskChangeUtils.getDeleteQueryBatch("Table", Arrays.asList(oddRow), 3)
        assertEquals(3, queries.size())

        assertEquals( "DELETE FROM Table WHERE id IN (1,2,3)" ,queries.get(0))
        assertEquals("DELETE FROM Table WHERE id IN (4,5,6)", queries.get(1))
        assertEquals( "DELETE FROM Table WHERE id IN (7)" ,queries.get(2))

        // batch size is greater than ids list - one query expected
        Long[] bigBatch = [1L, 2L, 3L, 4L, 5L]
        queries = IshTaskChangeUtils.getDeleteQueryBatch("Table", Arrays.asList(bigBatch), 8)
        assertEquals(1, queries.size())

        assertEquals( "DELETE FROM Table WHERE id IN (1,2,3,4,5)" ,queries.get(0))

        // no ids in list - no one queries should be created
        queries = IshTaskChangeUtils.getDeleteQueryBatch("Table", Arrays.asList([] as Long[]), 8)
        assertEquals(0, queries.size())
    }

    @Test
    void testDeleteQueuedRecordsBatch() {
        List<String> queries
        Date date = new Date()

        String currentDateString = new SimpleDateFormat(IshTaskChangeUtils.DATE_FORMAT).format(date);

        // even size ids list test
        Long[] evenRow = [1L, 2L, 3L, 4L, 5L, 6L]
        queries = IshTaskChangeUtils.getDeleteQueuedRecordsBatch("Table", Arrays.asList(evenRow), 3, date)
        assertEquals(4, queries.size())

        String transactionUUID = queries.get(0).substring(102,138)
        assertEquals( "INSERT INTO QueuedTransaction (transactionKey, createdOn, modifiedOn) values ('remove_transaction_0-3_$transactionUUID', '$currentDateString', '$currentDateString')".toString() , queries.get(0).toString())
        assertEquals("INSERT INTO QueuedRecord (foreignRecordId, tableName, action, createdOn, modifiedOn, numberOfAttempts, transactionId) VALUES\n" +
                "(1,'Table','DELETE','$currentDateString','$currentDateString',0,(SELECT id FROM QueuedTransaction WHERE transactionKey = 'remove_transaction_0-3_$transactionUUID')),\n" +
                "(2,'Table','DELETE','$currentDateString','$currentDateString',0,(SELECT id FROM QueuedTransaction WHERE transactionKey = 'remove_transaction_0-3_$transactionUUID')),\n" +
                "(3,'Table','DELETE','$currentDateString','$currentDateString',0,(SELECT id FROM QueuedTransaction WHERE transactionKey = 'remove_transaction_0-3_$transactionUUID'))".toString(),
                queries.get(1).toString())

        // odd size ids list test
        Long[] oddRow = [1L, 2L, 3L, 4L, 5L, 6L, 7L]
        queries = IshTaskChangeUtils.getDeleteQueuedRecordsBatch("Table", Arrays.asList(oddRow), 3, date)
        assertEquals(6, queries.size())

        // no ids in list - no one queries should be created
        Long[] empty = []
        queries = IshTaskChangeUtils.getDeleteQueuedRecordsBatch("Table", Arrays.asList(empty), 8, date)
        assertEquals(0, queries.size())
    }
}

//"INSERT INTO QueuedTransaction (transactionKey, createdOn, modifiedOn) values ('remove_transaction_0-3_04bd472c-e765-48b6-9349-383687a2c4ea'', '2019-10-04 12:54:49', '2019-10-04 12:54:49');
//"INSERT INTO QueuedTransaction (transactionKey, createdOn, modifiedOn) values ('remove_transaction_0-3_04bd472c-e765-48b6-9349-383687a2c4ea', '2019-10-04 12:54:49', '2019-10-04 12:54:49')
//

