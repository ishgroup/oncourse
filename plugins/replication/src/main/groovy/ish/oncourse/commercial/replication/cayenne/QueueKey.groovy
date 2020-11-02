/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.cayenne

class QueueKey {

    private Long id
    private String entityName

    QueueKey(Long id, String entityName) {
        this.id = id
        this.entityName = entityName
    }

    @Override
    boolean equals(Object obj) {

        if (obj instanceof QueueKey) {
            QueueKey k = (QueueKey) obj
            return this.id.equals(k.id) && this.entityName.equals(k.entityName)
        }

        return false
    }

    @Override
    int hashCode() {
        return this.id.hashCode()
    }
}
