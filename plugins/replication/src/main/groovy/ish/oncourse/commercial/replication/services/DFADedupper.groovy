/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.services

import groovy.transform.CompileStatic
import ish.oncourse.commercial.replication.cayenne.QueuedRecord
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static ish.oncourse.commercial.replication.cayenne.QueuedRecordAction.*
import static ish.oncourse.commercial.replication.services.DFADedupper.InternalState.*

@CompileStatic
class DFADedupper implements Comparable<DFADedupper> {

    /**
     * Loggger
     */
    private static final Logger logger = LogManager.getLogger()

    /**
     * Transaction keys
     */
    private SortedSet<String> transactionKeys = new TreeSet<>()

    /**
     * Internal states
     *
     */
    enum InternalState {
        DFA_START,
        DFA_CREATE,
        DFA_UPDATE,
        DFA_DELETE,
        DFA_NOP
    }

    /**
     * Current state
     */
    private InternalState currentState = DFA_START

    private LinkedList<QueuedRecord> recordSet = new LinkedList<>()

    void nextState(QueuedRecord record) throws DedupperException {
        this.transactionKeys.add(record.getQueuedTransaction().getTransactionKey())

        QueuedRecord currentRecord = this.recordSet.isEmpty() ? null : this.recordSet.getLast()

        if (currentRecord != null) {
            if (currentRecord.getTableName() != record.getTableName() || currentRecord.getForeignRecordId() != record.getForeignRecordId()) {
                throw new DedupperException(String.format("Expecting entity:%s, with id:%s, but got entity:%s with id:%s", currentRecord.getTableName(),
                        currentRecord.getForeignRecordId(), record.getTableName(), record.getForeignRecordId()))
            }
        }

        this.recordSet.add(record)

        switch (this.currentState) {
            case DFA_START:
                switch (record.getAction()) {
                    case CREATE:
                        this.currentState = InternalState.DFA_CREATE
                        break
                    case UPDATE:
                        this.currentState = InternalState.DFA_UPDATE
                        break
                    case DELETE:
                        this.currentState = InternalState.DFA_DELETE
                        break
                }
                break
            case DFA_CREATE:
                switch (record.getAction()) {
                    case CREATE:
                        throw new DedupperException("Can't accept second CREATE event. Deduplication log:\n" + getDedupperStateString())
                    case UPDATE:
                        break
                    case DELETE:
                        this.currentState = deleteAfterCreateState()
                        break
                }
                break
            case DFA_UPDATE:
                switch (record.getAction()) {
                    case CREATE:
                        logger.debug("Accept CREATE event after UPDATE event for entity:{} with id:{}.", record.getTableName(), record.getForeignRecordId())
                        break
                    case UPDATE:
                        break
                    case DELETE:
                        this.currentState = InternalState.DFA_DELETE
                        break
                }
                break
            case DFA_DELETE:
                throw new DedupperException("Can't accept any events after DELETE event. Deduplication log:\n" + getDedupperStateString())
            case DFA_NOP:
                throw new DedupperException("Can't accept any events in NOP state. Deduplication log:\n" + getDedupperStateString())
        }
    }

    protected InternalState deleteAfterCreateState() {
        return InternalState.DFA_NOP
    }

    /**
     * @see Comparable#compareTo(Object)
     */
    @Override
    int compareTo(DFADedupper o) {
        return o.getTransactionKeys().size() - getTransactionKeys().size()
    }

    SortedSet<String> getTransactionKeys() {
        return this.transactionKeys
    }

    QueuedRecord deDuppedRecord() {
        if (this.currentState == InternalState.DFA_NOP) {
            return null
        }
        return this.recordSet.getLast()
    }

    List<QueuedRecord> duplicates() {
        def dedupped = deDuppedRecord()
        if (dedupped != null) {
            return this.recordSet.subList(0, this.recordSet.size() - 1)
        }
        return this.recordSet
    }

    List<QueuedRecord> getAllRecords() {
        return this.recordSet
    }

    private String getDedupperStateString() {
        def builder = new StringBuilder()

        for (def record : recordSet) {
            if (builder.length() > 0) {
                builder.append("\n")
            }
            builder.append(String.format(
                    "QueuedRecord[transactionId: %d, action: %s, entity: %s, recordId: %d, created: %s, transactionDate: %s]",
                    record.getTransactionId(),
                    record.getAction().getDisplayName(),
                    record.getTableName(),
                    record.getForeignRecordId(),
                    record.getCreatedOn(),
                    record.getQueuedTransaction().getCreatedOn()))
        }

        return builder.toString()
    }
}
