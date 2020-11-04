/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.lifecycle

import ish.oncourse.commercial.replication.cayenne.QueuedRecord
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.CourseClass
import org.apache.cayenne.annotation.PreRemove
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.validation.ValidationException
import org.apache.cayenne.validation.ValidationResult

class CourseClassLifecycleListener {
    
    static final String CLASS_HAS_QUEUED_RECORDS_MESSAGE = "Synchronisation is not completed for this CourseClass. Please wait for it and then try again, or just cancel the class instead."

    ICayenneService cayenneService
    
    CourseClassLifecycleListener(ICayenneService cayenneService) {
        this.cayenneService = cayenneService
    }
    
    
    
    @PreRemove(value = CourseClass)
    void preRemove(CourseClass courseClass) {
        List<QueuedRecord> records = ObjectSelect.query(QueuedRecord)
                .where(QueuedRecord.TABLE_NAME.eq(courseClass.entityName))
                .and(QueuedRecord.FOREIGN_RECORD_ID.eq(courseClass.id))
                .select(cayenneService.newContext)
        throw new ValidationException(CLASS_HAS_QUEUED_RECORDS_MESSAGE)
    }
}
