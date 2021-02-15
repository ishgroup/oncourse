/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.lifecycle

import groovy.transform.CompileStatic
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.commercial.replication.cayenne.QueuedRecordAction
import ish.oncourse.commercial.replication.cayenne.QueuedTransaction
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.ISHDataContext
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Queueable
import ish.oncourse.commercial.replication.cayenne.QueuedRecord
import ish.oncourse.server.license.LicenseService
import org.apache.cayenne.Cayenne
import org.apache.cayenne.DataChannelSyncFilter
import org.apache.cayenne.DataChannelSyncFilterChain
import org.apache.cayenne.LifecycleListener
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.ObjectId
import org.apache.cayenne.annotation.PostPersist
import org.apache.cayenne.annotation.PostRemove
import org.apache.cayenne.annotation.PostUpdate
import org.apache.cayenne.annotation.PreRemove
import org.apache.cayenne.graph.GraphDiff
import org.apache.cayenne.map.LifecycleEvent
import org.apache.cayenne.query.ObjectIdQuery
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static org.apache.cayenne.map.LifecycleEvent.*

@CompileStatic
class QueueableLifecycleListener implements DataChannelSyncFilter {

    /**
     * Logger
     */
    private static final Logger logger = LogManager.getLogger()

    /**
     * Cayenne service.
     */
    private final ICayenneService cayenneService

    /**
     * LicenseS controller
     */
    private final LicenseService licenseService

    /**
     * Storage to hold cayenne object context between invocations of preRemove and postRemove methods.
     */
    private Map<ObjectId, ObjectContext> objectIdContextMap = new WeakHashMap<>()

    /**
     * DataContext threadlocal storage
     */
    private static final ThreadLocal<Deque<StackFrame>> STACK_STORAGE = new InheritableThreadLocal<>()

    /**
     * Constructor
     *
     * @param cayenneService
     * @param prefController
     */
    QueueableLifecycleListener(ICayenneService cayenneService, LicenseService licenseService ) {
        this.cayenneService = cayenneService
        this.licenseService = licenseService
    }

    /**
     * @see DataChannelSyncFilter#onSync(ObjectContext, GraphDiff, int, DataChannelSyncFilterChain)
     */
    @Override
    GraphDiff onSync(ObjectContext originatingContext, GraphDiff changes, int syncType, DataChannelSyncFilterChain filterChain) {

        try {
            Deque<StackFrame> stack = STACK_STORAGE.get()
            if (stack == null) {
                stack = new LinkedList<>()
                STACK_STORAGE.set(stack)
            }

            def frame = stack.isEmpty() ? new StackFrame(cayenneService.getNewNonReplicatingContext(), new HashMap<>()) : stack
                    .peek()

            try {
                stack.push(frame)
                return filterChain.onSync(originatingContext, changes, syncType)
            } catch (Throwable e) {
                logger.error("QueueableLifecycleListener thrown an exception", e)
                throw new RuntimeException(e)
            } finally {
                /*
                 * Pop and commit are called from finally block because they should be called always. Otherwise we can lose uncommitted QueuedRecords when
                 * filterChain.onSync will throw any exception.
                 */
                stack.pop()
                if (stack.isEmpty()) {
                    frame.getObjectContext().commitChanges()
                }
            }

        } catch (Throwable e) {
            logger.error("QueueableLifecycleListener thrown an exception", e)
            throw new RuntimeException(e)
        } finally {
            if (STACK_STORAGE.get() != null && STACK_STORAGE.get().isEmpty()) {
                STACK_STORAGE.set(null)
            }
        }
    }

    /**
     * Within "ObjectContext.commitChanges()", after commit of a new object is done.
     *
     * @see LifecycleListener#postPersist(Object)
     */
    @PostPersist(entityAnnotations = QueueableEntity.class)
    void postPersist(Object entity) {
        logger.entry(entity)
        def cdo = (Queueable) entity

        if (cdo.getObjectContext() != null && cdo.getObjectContext() instanceof ISHDataContext) {
            def recordContext = (ISHDataContext) cdo.getObjectContext()
            if (!recordContext.getIsRecordQueueingEnabled()) {
                return
            }

            if (isAsyncReplicationAllowed(cdo)) {
                addRecordToReplicationQueue(cdo, POST_PERSIST)
            }
        }
    }

    /**
     * This method must not alter the current CayenneDataObject
     *
     * @param event cayenne lifecycle event
     */
    void addRecordToReplicationQueue(Queueable record, final LifecycleEvent event) {

        def recordContext = (ISHDataContext) record.getObjectContext()

        if (event != LifecycleEvent.POST_REMOVE) {
            def query = new ObjectIdQuery(record.getObjectId(), false, ObjectIdQuery.CACHE_REFRESH)
            record = (Queueable) Cayenne.objectForQuery(recordContext, query)
        }

        logger.debug("adding to replication queue {}", getClass())

        def result = new QueuedRecord()
        result.setLastAttemptOn(new Date())
        result.setNumberOfAttempts(0)
        result.setTableName(getTableName(record))
        result.setForeignRecordId(record.getId())
        result.setWillowId(record.getWillowId())

        switch (event) {
            case POST_PERSIST:
                result.setAction(QueuedRecordAction.CREATE)
                break
            case POST_UPDATE:
                result.setAction(QueuedRecordAction.UPDATE)
                break
            case POST_REMOVE:
                result.setAction(QueuedRecordAction.DELETE)
                break
            default:
                logger.error("Queued records is only created on post* events, but got {}.", event)
        }

        if (result.getAction() != null) {

            ObjectContext currentContext = STACK_STORAGE.get().peek().getObjectContext()
            String transactionKey = recordContext.getTransactionKey()
            QueuedTransaction t = STACK_STORAGE.get().peek().getTransactionMapping().get(transactionKey)

            if (t == null) {
                t = currentContext.newObject(QueuedTransaction)
                Date today = new Date()
                t.setCreatedOn(today)
                t.setModifiedOn(today)
                t.setTransactionKey(transactionKey)
                STACK_STORAGE.get().peek().getTransactionMapping().put(transactionKey, t)
            }

            currentContext.registerNewObject(result)
            result.setQueuedTransaction(t)

            logger.debug("Creating replication queue record, table name:{} id:{} action:{} ", result.getTableName(), result.getForeignRecordId(),
                    result.getAction())
        }
    }

    /**
     * Gets table name
     *
     * @param record queueable record
     * @return table name
     */
    private String getTableName(Queueable record) {
        return record.getObjectId().getEntityName()
    }

    /**
     * Within "ObjectContext.commitChanges()", after commit of a deleted object is done.
     *
     * @see LifecycleListener#postRemove(Object)
     */
    @PostRemove(entityAnnotations = QueueableEntity.class)
    void postRemove(Object entity) {
        def cdo = (Queueable) entity
        if (isAsyncReplicationAllowed(cdo)) {
            // the object context in post remove method is always null
            def context = (ISHDataContext) this.objectIdContextMap.remove(cdo.getObjectId())
            if (context != null) {
                cdo.setObjectContext(context)
                addRecordToReplicationQueue(cdo, POST_REMOVE)
            }
        }
    }

    /**
     * @see LifecycleListener#preRemove(Object)
     */
    @PreRemove(entityAnnotations = QueueableEntity.class)
    void preRemove(Object entity) {

        def cdo = (Queueable) entity

        if (cdo.getObjectContext() != null && cdo.getObjectContext() instanceof ISHDataContext) {
            def recordContext = (ISHDataContext) cdo.getObjectContext()
            if (!recordContext.getIsRecordQueueingEnabled()) {
                return
            }

            if (isAsyncReplicationAllowed(cdo)) {
                this.objectIdContextMap.put(cdo.getObjectId(), cdo.getObjectContext())
            }
        }
    }

    /**
     * Within "ObjectContext.commitChanges()", after commit of a modified object is done.
     *
     * @see LifecycleListener#postUpdate(Object)
     */
    @PostUpdate(entityAnnotations = QueueableEntity.class)
    void postUpdate(Object entity) {
        logger.entry(entity)

        def cdo = (Queueable) entity

        if (cdo.getObjectContext() != null && cdo.getObjectContext() instanceof ISHDataContext) {
            def recordContext = (ISHDataContext) cdo.getObjectContext()
            if (!recordContext.getIsRecordQueueingEnabled()) {
                return
            }

            if (isAsyncReplicationAllowed(cdo)) {
                addRecordToReplicationQueue(cdo, POST_UPDATE)
            }
        }
    }

    /**
     * Methods which filters out some entities from regular replication, process this entities is supposed to be processed synchronously.
     *
     * @param entity cayenne entity
     * @return true - if regular replication allowed, false - otherwise.
     */
    private boolean isAsyncReplicationAllowed(Queueable entity) {
        return !licenseService.replicationDisabled && entity.isAsyncReplicationAllowed()
    }
}
