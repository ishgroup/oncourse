/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.services

import groovy.transform.CompileStatic
import ish.oncourse.commercial.replication.cayenne.QueueKey
import ish.oncourse.server.cayenne.Attendance
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.commercial.replication.cayenne.QueuedRecord
import org.apache.cayenne.ObjectContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@CompileStatic
final class DedupperFactory {
    private List<QueuedRecord> currentBatch
    private  ObjectContext ctx
    private Logger logger = LogManager.getLogger()
    private DedupperFactory() {}
    private List<String> attendanceOutcomeNames = Arrays.asList(Attendance.class.getSimpleName(), Outcome.class.getSimpleName())

    static DedupperFactory valueOf(List<QueuedRecord> currentBatch, ObjectContext ctx) {
        def factory = new DedupperFactory()
        factory.currentBatch = currentBatch
        factory.ctx = ctx
        return factory
    }

    List<DFADedupper> assembleDeduppers() {

        Map<QueueKey, DFADedupper> dedupMap = new LinkedHashMap<>()
        for (def r : currentBatch) {
            if (r.getAction() == null) {
                logger.info("Deleting QueuedRecord with foreignRecordId {} of type {} as it has null action", r.getForeignRecordId(), r.getEntityName())
                ctx.deleteObjects(ctx.localObject(r))
                ctx.commitChanges()
            } else {
                def key = new QueueKey(r.getForeignRecordId(), r.getTableName())
                def deduper = dedupMap.get(key)
                if (deduper == null) {
                    deduper = getDedupperBy(r.getTableName())
                    dedupMap.put(key, deduper)
                }
                try {
                    deduper.nextState(r)
                } catch (DedupperException e) {
                    // increase number of attempts for all the duplicate records and rethrow
                    for (def record : deduper.getAllRecords()) {
                        def qr = ctx.localObject(record)
                        qr.setNumberOfAttempts(qr.getNumberOfAttempts() + 1)
                        qr.setErrorMessage("Deduplication failed. Please see server log for details.")
                    }
                    ctx.commitChanges()

                    throw e
                }
            }
        }
        List<DFADedupper> sortedDeduppers = new ArrayList<>(dedupMap.entrySet().size())
        for (Map.Entry<QueueKey, DFADedupper> entry : dedupMap.entrySet()) {
            sortedDeduppers.add(entry.getValue())
        }
        return sortedDeduppers.sort() as List<DFADedupper>
    }

    private DFADedupper getDedupperBy(String tableName) {
        if (attendanceOutcomeNames.contains(tableName)) {
            return new AttendanceOutcomeDedupper()
        } else {
            return new DFADedupper()
        }
    }
}
