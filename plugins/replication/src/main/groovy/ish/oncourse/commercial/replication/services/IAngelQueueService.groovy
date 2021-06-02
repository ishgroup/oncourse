/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.services

import groovy.transform.CompileStatic
import ish.oncourse.commercial.replication.cayenne.QueuedTransaction
import org.apache.cayenne.DataRow

@CompileStatic
interface IAngelQueueService {
    String STACKED_RECORDS_COUNT_ALIAS = "cnt"

    /**
     * Get size of current queue.
     *
     * @return queue size
     */
    int getNumberOfTransactions()

    /**
     * Gets replication records starting from transaction with index, the number of records is limit.
     *
     * @param fromTransaction starting index
     * @param numberOfTransactions number of transactions
     * @return queued transactions
     */
    List<QueuedTransaction> getReplicationQueue(int fromTransaction, int numberOfTransactions)

    /**
     * Removes empty transactions from the queue.
     */
    void cleanEmptyTransactions()

    /**
     * Check that queue contains stacked data.
     *
     * @return is queue contains stacked data.
     */
    boolean isStackedRecordsExist()

    List<DataRow> receiveStackedDataInformation()

    /**
     * The method returns true if the async replication should be blocked while the sync replication (payment processing) is active.
     */
    boolean isReplicationBlocked()

    void setReplicationBlocked(Object blockInstance)

    void resetReplicationBlocked(Object blockInstance)
}
