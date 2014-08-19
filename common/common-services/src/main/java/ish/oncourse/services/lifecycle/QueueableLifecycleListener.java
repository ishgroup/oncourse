/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.services.lifecycle;

import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.persistence.ISHObjectContext;
import org.apache.cayenne.*;
import org.apache.cayenne.annotation.PostRemove;
import org.apache.cayenne.graph.GraphDiff;
import org.apache.cayenne.query.ObjectIdQuery;
import org.apache.cayenne.query.Query;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Listens for lifecycle events on entities and creates queue records for
 * queueable records.
 *
 * @author marek
 */
public class QueueableLifecycleListener implements LifecycleListener, DataChannelFilter {

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(QueueableLifecycleListener.class);

    /**
     * Cayenne service.
     */
    private ICayenneService cayenneService;

    /**
     * DataContext threadlocal storage
     */
    private static final ThreadLocal<Deque<StackFrame>> STACK_STORAGE = new InheritableThreadLocal<>();

    /**
     * Storage to hold college between invocations of preRemove and postRemove methods.
     */
    private Map<ObjectId, College> objectIdCollegeMap = new WeakHashMap<>();

    public QueueableLifecycleListener(ICayenneService cayenneService) {
        this.cayenneService = cayenneService;
    }

    /**
     * @see DataChannelFilter#init(DataChannel)
     */
    @Override
    public void init(DataChannel dataChannel) {

    }

    /**
     * @see DataChannelFilter#onQuery(ObjectContext, Query, DataChannelFilterChain)
     */
    @Override
    public QueryResponse onQuery(ObjectContext originatingContext, Query query, DataChannelFilterChain filterChain) {
        return filterChain.onQuery(originatingContext, query);
    }

    /**
     * @see DataChannelFilter#onSync(ObjectContext, GraphDiff, int, DataChannelFilterChain)
     */
    @Override
    public GraphDiff onSync(ObjectContext originatingContext, GraphDiff changes, int syncType, DataChannelFilterChain filterChain) {

        try {
            Deque<StackFrame> stack = STACK_STORAGE.get();

            if (stack == null) {
                stack = new LinkedList<>();
                STACK_STORAGE.set(stack);
            }

            StackFrame frame = stack.isEmpty() ? new StackFrame(cayenneService.newNonReplicatingContext(), new HashMap<String, QueuedTransaction>()) : stack
                    .peek();

            try {
                stack.push(frame);
                return filterChain.onSync(originatingContext, changes, syncType);
            }
            catch (Throwable e)
            {
                LOGGER.error("QueueableLifecycleListener thrown an exception", e);
                throw new RuntimeException(e);
            }
            finally {
                /**
                 * Pop and commit are called from finally block because they should be called always.
                 * Otherwise we can lose uncommitted QueuedRecords when  filterChain.onSync will throw any exception.
                 */
                stack.pop();
                if (stack.isEmpty()) {
                    frame.getObjectContext().commitChanges();
                }
            }
        }
        catch (Throwable e)
        {
            LOGGER.error("QueueableLifecycleListener thrown an exception", e);
            throw new RuntimeException(e);
        }
        finally {
            if (STACK_STORAGE.get() != null && STACK_STORAGE.get().isEmpty()) {
                STACK_STORAGE.set(null);
            }
        }
    }

    /**
     * Updates modified date on the object.
     */
    public void preUpdate(Object entity) {
        if (entity instanceof Queueable) {
            Queueable p = (Queueable) entity;
            p.setModified(new Date());
        }
    }

    /**
     * Initially sets created and modified dates on the object.
     */
    public void postAdd(Object entity) {
        if (entity instanceof Queueable) {
            Queueable p = (Queueable) entity;
            Date today = new Date();
            /**
             * The test has been introduced to exclude rewrite created date when the entity came from angel
             */
            if (p.getCreated() == null)
                p.setCreated(today);
            p.setModified(today);
        }
    }

    // The following events are ignored for queueing purposes:
    public void prePersist(Object entity) {
    }

    /**
     * Adds object context to the weak hash map, so we can pick it up in the
     * next event method postRemove. In postRemove the object context is always
     * null.
     */
    public void preRemove(Object entity) {
        if (entity instanceof Queueable) {
            Queueable q = (Queueable) entity;
            if (q.getObjectContext() != null && (q.getObjectContext() instanceof ISHObjectContext)) {
                ISHObjectContext recordContext = (ISHObjectContext) q.getObjectContext();
                if (!recordContext.getIsRecordQueueingEnabled()) {
                    return;
                }
                if (q.isAsyncReplicationAllowed()) {
                    objectIdCollegeMap.put(q.getObjectId(), q.getCollege());
                }
            }
        }
    }

    /**
     * New record event - post save.
     *
     * @param entity - created entity
     */

    public void postPersist(Object entity) {
		addRecord(entity, QueuedRecordAction.CREATE);
    }

	/**
	 * Update record event - post save.
	 *
	 * @param entity - changed entity
	 */
	public void postUpdate(Object entity) {
		addRecord(entity, QueuedRecordAction.UPDATE);
	}

	private void addRecord(Object entity, QueuedRecordAction action)
	{
		if (entity instanceof Queueable) {
			Queueable q = (Queueable) entity;
			if (q.getObjectContext() != null && (q.getObjectContext() instanceof ISHObjectContext)) {
				ISHObjectContext recordContext = (ISHObjectContext) q.getObjectContext();
				boolean isAsyncReplicationAllowed = q.isAsyncReplicationAllowed();
				boolean replicatedContext = recordContext.getIsRecordQueueingEnabled();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format("Post %s entered with %s %s %s with ID : %s %s", action,
							(replicatedContext ? "replication context " : "non-replication context "),
							(isAsyncReplicationAllowed ? "async replication allowed for this object ":"async replication not allowed for this object"),
							q.getClass().getSimpleName(),
							q.getObjectId(),
							traceObjectInfo(q)));
				}
				if (!replicatedContext) {
					return;
				}
				if (isAsyncReplicationAllowed) {
					LOGGER.debug(String.format("Post %s event on : Entity: %s  with ID : %s",
							action,
							q.getClass().getSimpleName(),
							q.getObjectId()));
					enqueue(q, action);
				}
			}
		}
	}

    /**
     * Delete record event - post delete.
     */
    @PostRemove
    public void postRemove(Object entity) {
        if (entity instanceof Queueable) {
            Queueable q = (Queueable) entity;
            if (q.isAsyncReplicationAllowed()) {
                LOGGER.debug("Post Remove event on : Entity: " + q.getClass().getSimpleName() + " with ID : " + q.getObjectId());
                College college = objectIdCollegeMap.remove(q.getObjectId());
                if (college != null) {
                    q.setCollege(college);
                    enqueue(q, QueuedRecordAction.DELETE);
                }
            }
        }

    }

    public void postLoad(Object entity) {
        // Not used
    }

    private String traceObjectInfo(Queueable entity) {
        String message = StringUtils.EMPTY;
        if (entity instanceof PaymentIn) {
            PaymentIn payment = (PaymentIn) entity;
            message = String.format(" PaymentIn with id = %s and paymenttype = %s and payment status = %s and amount = %s and source = %s",
                    payment.getId(), payment.getType().name(), payment.getStatus().name(), payment.getAmount().toBigDecimal().toPlainString(), payment.getSource().name());
        } else if (entity instanceof PaymentInLine) {
            PaymentInLine pLine = (PaymentInLine) entity;
            message = String.format(" PaymentInLine with id = %s and amount = %s", pLine.getId(), pLine.getAmount().toBigDecimal().toPlainString());
        } else if (entity instanceof Enrolment) {
            Enrolment enrl = (Enrolment) entity;
            message = String.format(" Enrolment with id = %s and source = %s and status = %s", enrl.getId(), enrl.getSource().name(), enrl.getStatus().name());
        } else if (entity instanceof Invoice) {
            Invoice invoice = (Invoice) entity;
            message = String.format(" Invoice with id = %s source = %s and amount owing = %s", invoice.getId(), invoice.getSource().name(), invoice.getAmountOwing());
        } else if (entity instanceof InvoiceLine) {
            InvoiceLine invoiceLine = (InvoiceLine) entity;
            message = String.format(" InvoiceLine with id = %s linked with enrollment with id = %s", invoiceLine.getId(), invoiceLine.getEnrolment() != null ? invoiceLine.getEnrolment().getId() : null);
        }
        return message;
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
        College college = entity.getCollege();
        if (college == null) {
            // we don't need to add QueuedRecords on entities where
            // collegeId=null, such as global preferences.
            return;
        }

        ISHObjectContext commitingContext = (ISHObjectContext) college.getObjectContext();
        if (action != QueuedRecordAction.DELETE) {
            ObjectIdQuery query = new ObjectIdQuery(entity.getObjectId(), false, ObjectIdQuery.CACHE_REFRESH);
            entity = (Queueable) Cayenne.objectForQuery(commitingContext, query);
        }

        ObjectContext currentContext = STACK_STORAGE.get().peek().getObjectContext();
		college = currentContext.localObject(college);

		String transactionKey = commitingContext.getTransactionKey();

        //We store transaction using key which is collegeId + transactionKey to
        //properly handle commit() which touches records across several colleges
        String queuedTransactionKey = String.format("%s:%s", college.getId(), transactionKey);
        QueuedTransaction t = STACK_STORAGE.get().peek().getTransactionMapping().get(queuedTransactionKey);
        
        if (t == null) {
            t = currentContext.newObject(QueuedTransaction.class);
            Date today = new Date();
            t.setCreated(today);
            t.setModified(today);
            t.setTransactionKey(transactionKey);
            t.setCollege(college);
            STACK_STORAGE.get().peek().getTransactionMapping().put(queuedTransactionKey, t);
        }
        
        String entityName = entity.getObjectId().getEntityName();
        Long entityId = entity.getId();
        Long angelId = entity.getAngelId();

        LOGGER.debug(String.format("Creating QueuedRecord<id:%s, entityName:%s, action:%s, transactionKey:%s>", entityId, entityName,
                action, transactionKey));

        QueuedRecord qr = currentContext.newObject(QueuedRecord.class);
        qr.setCollege(college);
        qr.setEntityIdentifier(entityName);
        qr.setEntityWillowId(entityId);
        qr.setAngelId(angelId);
        qr.setQueuedTransaction(t);
        qr.setAction(action);
        qr.setNumberOfAttempts(0);
        qr.setLastAttemptTimestamp(new Date());
    }
}