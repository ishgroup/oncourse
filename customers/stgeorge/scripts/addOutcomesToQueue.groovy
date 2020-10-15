/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import ish.oncourse.cayenne.QueuedRecordAction

def run(args) {

    def batchCount = 100
    def today = new Date()
    def context = args.context
    def count = 0
    def outcomes = ObjectSelect.query(Outcome)
            .where(Outcome.START_DATE.isNotNull())
            .or(Outcome.END_DATE.isNotNull())
            .pageSize(batchCount)
            .select(context)

    outcomes.each { o ->
        def qt = context.newObject(QueuedTransaction)
        qt.setTransactionKey("customReplicationScriptFor_${o.objectId.entityName}_with_Id${o.id}_willowId${o.willowId}")

        def qr = context.newObject(QueuedRecord)
        qr.setLastAttemptOn(today)
        qr.setNumberOfAttempts(0)
        qr.setTableName(o.objectId.entityName)
        qr.setForeignRecordId(o.id)
        qr.setWillowId(o.willowId)
        qr.setAction(QueuedRecordAction.UPDATE)
        qr.setQueuedTransaction(qt)

        if (count > batchCount) {
            count = 0
            context.commitChanges()
        }
    }
    context.commitChanges()
}
