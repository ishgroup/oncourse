/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.cayenne

import ish.oncourse.server.cayenne.glue._QueuedTransaction

import javax.annotation.Nonnull
import java.util.Date
import java.util.List

/**
 * A persistent class mapped as "QueuedTransaction" Cayenne entity.
 */
class QueuedTransaction extends _QueuedTransaction {


    /**
     * @return the date and time this record was created
     */
    @Override
    Date getCreatedOn() {
        return super.getCreatedOn()
    }


    /**
     * @return the date and time this record was modified
     */
    @Override
    Date getModifiedOn() {
        return super.getModifiedOn()
    }

    /**
     * @return
     */
    @Nonnull
    @Override
    String getTransactionKey() {
        return super.getTransactionKey()
    }

    /**
     * @return
     */
    @Nonnull
    @Override
    List<QueuedRecord> getQueuedRecords() {
        return super.getQueuedRecords()
    }

    @Override
    boolean isAuditAllowed() {
        return false
    }
}
