package ish.oncourse.willow.cayenne

import groovy.transform.CompileStatic
import ish.oncourse.model.*
import ish.oncourse.services.lifecycle.StackFrame
import org.apache.cayenne.*
import org.apache.cayenne.graph.GraphDiff
import org.apache.cayenne.query.ObjectIdQuery
import org.apache.cayenne.query.Query
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.security.SecureRandom
import java.util.*

@CompileStatic
class QueueableLifecycleListener implements LifecycleListener, DataChannelFilter {

    public static final String TRANSACTION_KEY_PROPERTY = 'transactionKey'

    /**
     * Logger
     */
    final static  Logger logger = LoggerFactory.getLogger(QueueableLifecycleListener.class)

    /**
     * Cayenne service.
     */

    /**
     * DataContext threadlocal storage
     */

    /**
     * Storage to hold college between invocations of preRemove and postRemove methods.
     */
    private Map<ObjectId, College> objectIdCollegeMap = new WeakHashMap<>()
    private static final ThreadLocal<Deque<StackFrame>> STACK_STORAGE = new InheritableThreadLocal<>()


    CayenneService cayenneService
    
/**
     * Updates modified date on the object.
     */
    void preUpdate(Object entity) {
        if (entity instanceof Queueable) {
            Queueable p = entity as Queueable
            p.modified = new Date()
        }
    }

    /**
     * Initially sets created and modified dates on the object.
     */
    void postAdd(Object entity) {
        if (entity instanceof Queueable) {
            Queueable p = entity as Queueable
            Date today = new Date()
            /**
             * The test has been introduced to exclude rewrite created date when the entity came from angel
             */
            if (p.created == null) {
                p.created = today
            }
            p.modified = today
        }
    }

    // The following events are ignored for queueing purposes:
    void prePersist(Object entity) {
    }

    /**
     * Adds object context to the weak hash map, so we can pick it up in the
     * next event method postRemove. In postRemove the object context is always
     * null.
     */
    void preRemove(Object entity) {
        if (entity instanceof Queueable) {
            Queueable q = entity as Queueable
            
            if (q.objectContext.getUserProperty(CayenneService.DISABLE_REPLICATION_PROPERTY)) {
                return
            }
            if (q.asyncReplicationAllowed) {
                objectIdCollegeMap.put(q.objectId, q.college)
            }
        }
    }

    /**
     * New record event - post save.
     *
     * @param entity - created entity
     */
    void postPersist(Object entity) {
        addRecord(entity, QueuedRecordAction.CREATE)
    }

    /**
     * Update record event - post save.
     *
     * @param entity - changed entity
     */
    void postUpdate(Object entity) {
        addRecord(entity, QueuedRecordAction.UPDATE)
    }

    private void addRecord(Object entity, QueuedRecordAction action)
    {
        if (entity instanceof Queueable) {
            Queueable q =  entity as Queueable

            boolean isAsyncReplicationAllowed = q.asyncReplicationAllowed
            boolean replicatedContext = q.objectContext.getUserProperty(CayenneService.DISABLE_REPLICATION_PROPERTY) == null
            
            if (replicatedContext && isAsyncReplicationAllowed) {
                enqueue(q, action)
            }
        }
        
    }

    /**
     * Delete record event - post delete.
     */
    void postRemove(Object entity) {
        if (entity instanceof Queueable) {
            Queueable q = entity as Queueable
            if (q.asyncReplicationAllowed) {
                College college = objectIdCollegeMap.remove(q.getObjectId())
                if (college != null) {
                    q.college = college
                    enqueue(q, QueuedRecordAction.DELETE)
                }
            }
        }
    }

    void postLoad(Object entity) {
        // Not used
    }
    

    /**
     * Adds a new record to the queue.
     *
     * <p>
     * Note that the code does not check for existing instances of the same
     * record in the queue.
     * </p>
     *
     * @param entity
     *            record to enqueue
     * @param action
     *            the type of action that triggered the queueing {@see
     *            QueuedRecordAction}
     */
    private void enqueue(Queueable entity, QueuedRecordAction action) {
        College college = entity.college
        if (college == null) {
            // we don't need to add QueuedRecords on entities where
            // collegeId=null, such as global preferences.
            return
        }

        ObjectContext commitingContext = college.objectContext
        if (action != QueuedRecordAction.DELETE) {
            ObjectIdQuery query = new ObjectIdQuery(entity.getObjectId(), false, ObjectIdQuery.CACHE_REFRESH)
            entity = (Queueable) Cayenne.objectForQuery(commitingContext, query)
        }

        ObjectContext currentContext = STACK_STORAGE.get().peek().objectContext
        college = currentContext.localObject(college)

        String transactionKey = commitingContext.getUserProperty(TRANSACTION_KEY_PROPERTY)

        //We store transaction using key which is collegeId + transactionKey to
        //properly handle commit() which touches records across several colleges
        String queuedTransactionKey = "$college.id:$transactionKey"
        QueuedTransaction t = STACK_STORAGE.get().peek().transactionMapping[queuedTransactionKey]
        
        Date today = new Date()
        
        if (t == null) {
            t = currentContext.newObject(QueuedTransaction)
            t.created = today
            t.modified = today
            t.transactionKey = queuedTransactionKey
            t.college = college
            STACK_STORAGE.get().peek().transactionMapping[queuedTransactionKey] = t 
        }

        QueuedRecord qr = currentContext.newObject(QueuedRecord)
        qr.college = college
        qr.entityIdentifier = entity.objectId.entityName
        qr.entityWillowId =  entity.id
        qr.angelId = entity.angelId
        qr.queuedTransaction = t
        qr.action = action
        qr.numberOfAttempts = 0
        qr.lastAttemptTimestamp = today
    }
    

    String generateTransactionKey(ObjectContext context) {
        SecureRandom random = new SecureRandom(context.toString().bytes)
        byte[] bytes = new byte[20]
        random.nextBytes(bytes)
        return DigestUtils.md5Hex(bytes)
    }

    @Override
    void init(DataChannel channel) {

    }

    @Override
    QueryResponse onQuery(ObjectContext originatingContext, Query query, DataChannelFilterChain filterChain) {
        return filterChain.onQuery(originatingContext, query)
    }

    @Override
    GraphDiff onSync(ObjectContext originatingContext, GraphDiff changes, int syncType, DataChannelFilterChain filterChain) {
        try {
            
            Deque<StackFrame> stack = STACK_STORAGE.get()

            if (stack == null) {
                stack = new LinkedList<>()
                STACK_STORAGE.set(stack)
            }

            StackFrame frame = stack.isEmpty() ? new StackFrame(cayenneService.newNonReplicatingContext, new HashMap<String, QueuedTransaction>()) : stack.peek()

            try {
                stack.push(frame)
                originatingContext.setUserProperty(TRANSACTION_KEY_PROPERTY, generateTransactionKey(originatingContext))
                return filterChain.onSync(originatingContext, changes, syncType)
            }
            catch (Throwable e)
            {
                logger.error("QueueableLifecycleListener thrown an exception", e)
                throw new RuntimeException(e)
            }
            finally {
                /**
                 * Pop and commit are called from finally block because they should be called always.
                 * Otherwise we can lose uncommitted QueuedRecords when  filterChain.onSync will throw any exception.
                 */
                stack.pop()
                if (stack.empty) {
                    frame.objectContext.commitChanges()
                }
            }
        } catch (Throwable e) {
            logger.error('QueueableLifecycleListener thrown an exception', e)
            throw new RuntimeException(e)
        } finally {
            if (STACK_STORAGE.get() != null && STACK_STORAGE.get().empty) {
                STACK_STORAGE.set(null)
            }
        }
    }
}