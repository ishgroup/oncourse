/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.services

import com.google.inject.Inject
import ish.oncourse.commercial.replication.cayenne.QueuedTransaction
import ish.oncourse.server.ICayenneService
import ish.oncourse.commercial.replication.cayenne.QueuedRecord
import org.apache.cayenne.DataRow
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.EJBQLQuery
import org.apache.cayenne.query.Ordering
import org.apache.cayenne.query.SQLTemplate
import org.apache.cayenne.query.SelectQuery
import org.apache.cayenne.query.SortOrder


class AngelQueueService implements IAngelQueueService {
    /**
     * Cayenne service.
     */
    private final ICayenneService cayenneService

    private List<Object> blockInstances = new ArrayList<>()

    @Inject
    AngelQueueService(ICayenneService cayenneService) {
        this.cayenneService = cayenneService
    }

    /**
     * @see IAngelQueueService#getNumberOfTransactions()
     */
    @Override
    int getNumberOfTransactions() {
        cleanEmptyTransactions()
        def sql = String.format("select count(distinct t.id) as SIZE from QueuedTransaction t inner join QueuedRecord q on t.id = q.transactionId where q.numberOfAttempts < %s", QueuedRecord.MAX_NUMBER_OF_RETRY)
        def q = new SQLTemplate(QueuedRecord.class, sql)
        q.setFetchingDataRows(true)

        List<DataRow> rows = cayenneService.getSharedContext().performQuery(q)
        def size = (Number)rows.get(0).get("SIZE")
        return size.intValue()
    }

    /**
     * @see IAngelQueueService#getReplicationQueue(int, int)
     */
    @Override
    List<QueuedTransaction> getReplicationQueue(int fromTransaction, int numberOfTransactions) {
        def qualifier = ExpressionFactory.lessExp(QueuedTransaction.QUEUED_RECORDS_PROPERTY + "." + QueuedRecord.NUMBER_OF_ATTEMPTS_PROPERTY,
                QueuedRecord.MAX_NUMBER_OF_RETRY)
        def q = SelectQuery.query(QueuedTransaction.class, qualifier)
        // q.addPrefetch(QueuedTransaction.QUEUED_RECORDS_PROPERTY)
        q.addOrdering(new Ordering("db:" + QueuedRecord.ID_PK_COLUMN, SortOrder.ASCENDING))
        q.setPageSize(numberOfTransactions)
        q.setFetchOffset(fromTransaction)
        def list = this.cayenneService.getSharedContext().select(q)
        List<QueuedTransaction> result = new ArrayList<>(numberOfTransactions)
        def maxNumber = Math.min(list.size(), numberOfTransactions)
        def index = 0
        while (index < maxNumber) {
            result.add(list.get(index++))
        }
        return result
    }

    @Override
    boolean isStackedRecordsExist() {
        final def q = new EJBQLQuery(String.format("select count(r) from QueuedRecord r where r.%s >= %s", QueuedRecord.NUMBER_OF_ATTEMPTS_PROPERTY,
                QueuedRecord.MAX_NUMBER_OF_RETRY - 1))
        // everything what not replicated from 2 attempts are the stacked data.
        return (Long) this.cayenneService.getSharedContext().performQuery(q).get(0) > 0
    }

    @Override
    List<DataRow> receiveStackedDataInformation() {
        final def template = new SQLTemplate(QueuedRecord.class, "select #result('" + QueuedRecord.TABLE_NAME_PROPERTY + "' 'java.lang.String' '" +
                QueuedRecord.TABLE_NAME_PROPERTY + "'), #result('count(" + QueuedRecord.ID_PK_COLUMN + ")' 'long' '" + STACKED_RECORDS_COUNT_ALIAS +
                "'),#result('count(" + QueuedRecord.TRANSACTION_ID_PROPERTY + ")' 'long' '" + QueuedRecord.TRANSACTION_ID_PROPERTY +
                "')  from QueuedRecord where " + QueuedRecord.NUMBER_OF_ATTEMPTS_PROPERTY + ">= " + (QueuedRecord.MAX_NUMBER_OF_RETRY - 1) + " GROUP BY " +
                QueuedRecord.TABLE_NAME_PROPERTY + " ORDER BY count(id) DESC")
        template.setFetchingDataRows(true)
        List<DataRow> rows = this.cayenneService.getSharedContext().performQuery(template)
        return rows
    }

    @Override
    void setReplicationBlocked(Object session) {
        synchronized (blockInstances) {
            blockInstances.add(session)
        }
    }

    void resetReplicationBlocked(Object session) {
        synchronized (blockInstances) {
            blockInstances.remove(session)
        }
    }

    boolean isReplicationBlocked() {
        synchronized (blockInstances) {
            return blockInstances.size() > 0
        }
    }



    /**
     * @see IAngelQueueService#cleanEmptyTransactions()
     */
    @Override
    void cleanEmptyTransactions() {
        ObjectContext objectContext = this.cayenneService.getNewNonReplicatingContext()
        def q = new EJBQLQuery("select qt from QueuedTransaction qt left join qt.queuedRecords qr where qr.foreignRecordId is null")
        List<QueuedTransaction> emptyTransactions = objectContext.performQuery(q)
        if (!emptyTransactions.isEmpty()) {
            objectContext.deleteObjects(emptyTransactions)
            objectContext.commitChanges()
        }
    }
}
