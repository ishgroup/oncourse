/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.lifecycle

import ish.oncourse.server.ICayenneService
import ish.oncourse.commercial.cayenne.QueuedRecord
import ish.oncourse.server.cayenne.QueuedTransaction
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.annotation.PostUpdate
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.SelectQuery

class QueuedTransactionListener {
    
    private ICayenneService cayenneService

    /**
     * @param cayenneService
     */
    QueuedTransactionListener(ICayenneService cayenneService) {
        super()
        this.cayenneService = cayenneService
    }

    @PostUpdate(value = QueuedTransaction.class)
    void postUpdate(QueuedTransaction t) {

        def q = SelectQuery.query(QueuedRecord.class)
        q.andQualifier(ExpressionFactory.matchExp(QueuedRecord.QUEUED_TRANSACTION_PROPERTY, t))
        def list = t.getObjectContext().select(q)

        if (list.isEmpty()) {
            ObjectContext objectContext = this.cayenneService.getNewNonReplicatingContext()
            objectContext.deleteObjects(objectContext.localObject(t))
            objectContext.commitChanges()
        }
    }
}
