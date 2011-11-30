/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.services.lifecycle;

import ish.common.types.PaymentStatus;
import ish.oncourse.model.College;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.EnrolmentStatus;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.InvoiceLineDiscount;
import ish.oncourse.model.InvoiceStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.QueuedRecordAction;
import ish.oncourse.model.QueuedTransaction;
import ish.oncourse.model.Tag;
import ish.oncourse.model.Taggable;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.persistence.ISHObjectContext;

import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.DataChannel;
import org.apache.cayenne.DataChannelFilter;
import org.apache.cayenne.DataChannelFilterChain;
import org.apache.cayenne.LifecycleListener;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.QueryResponse;
import org.apache.cayenne.graph.GraphDiff;
import org.apache.cayenne.query.ObjectIdQuery;
import org.apache.cayenne.query.Query;
import org.apache.log4j.Logger;

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
	private static final ThreadLocal<Deque<QueuedTransactionContext>> TRANSACTION_CONTEXT_STORAGE = new InheritableThreadLocal<Deque<QueuedTransactionContext>>();
	
	/**
	 * Storage to hold college between invocations of preRemove and postRemove methods.
	 */
	private Map<ObjectId, College> objectIdCollegeMap = new WeakHashMap<ObjectId, College>();

	/**
	 * Constructor
	 * @param cayenneService
	 */
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
		Deque<QueuedTransactionContext> deque = null;

		try {
			QueuedTransactionContext tContext;
			deque = TRANSACTION_CONTEXT_STORAGE.get();

			if (deque != null && !deque.isEmpty()) {
				tContext = deque.peek().shallowCopy();
			} else {
				deque = new LinkedList<QueuedTransactionContext>();
				TRANSACTION_CONTEXT_STORAGE.set(deque);
				tContext = new QueuedTransactionContext(cayenneService.newNonReplicatingContext());
			}

			deque.push(tContext);
			GraphDiff diff = filterChain.onSync(originatingContext, changes, syncType);
			tContext = deque.pop();

			if (deque.isEmpty()) {
				ObjectContext currentContext = tContext.getCurrentObjectContext();
				if (currentContext.hasChanges()) {
					currentContext.commitChanges();
				}
			}

			return diff;

		} finally {
			if (deque == null || deque.isEmpty()) {
				TRANSACTION_CONTEXT_STORAGE.set(null);
			}
		}
	}
	
	/**
	 * Gets current transaction context
	 */
	private QueuedTransactionContext getTransactionContext() {
		return TRANSACTION_CONTEXT_STORAGE.get().peek();
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
	public void prePersist(Object entity) {
		if (entity instanceof Queueable) {
			Queueable p = (Queueable) entity;
			Date today = new Date();
			p.setCreated(today);
			p.setModified(today);
		}
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

				if (isAsyncReplicationAllowed(q)) {
					objectIdCollegeMap.put(q.getObjectId(), q.getCollege());
				}
			}
		}
	}

	/**
	 * New record event - post save.
	 * 
	 * @param entity
	 */

	public void postPersist(Object entity) {
		if (entity instanceof Queueable) {
			Queueable q = (Queueable) entity;

			if (q.getObjectContext() != null && (q.getObjectContext() instanceof ISHObjectContext)) {

				ISHObjectContext recordContext = (ISHObjectContext) q.getObjectContext();
				if (!recordContext.getIsRecordQueueingEnabled()) {
					return;
				}

				if (isAsyncReplicationAllowed(q)) {
					LOGGER.debug("Post Persist event on : Entity: " + q.getClass().getSimpleName() + " with ID : " + q.getObjectId());
					enqueue(q, QueuedRecordAction.CREATE);
				}
			}
		}
	}

	/**
	 * Delete record event - post delete.
	 * 
	 * @param entity
	 */
	public void postRemove(Object entity) {

		if (entity instanceof Queueable) {
			Queueable q = (Queueable) entity;

			if (isAsyncReplicationAllowed(q)) {
				LOGGER.debug("Post Remove event on : Entity: " + q.getClass().getSimpleName() + " with ID : "
						+ q.getObjectId());
				College college = objectIdCollegeMap.remove(q.getObjectId());
				if (college != null) {
					q.setCollege(college);
					enqueue(q, QueuedRecordAction.DELETE);
				}
			}
		}

	}

	/**
	 * Update record event - post save.
	 * 
	 * @param entity
	 */
	public void postUpdate(Object entity) {
		if ((entity instanceof Queueable)) {
			Queueable q = (Queueable) entity;

			if (q.getObjectContext() != null && (q.getObjectContext() instanceof ISHObjectContext)) {
				
				ISHObjectContext recordContext = (ISHObjectContext) q.getObjectContext();
				if (!recordContext.getIsRecordQueueingEnabled()) {
					return;
				}
				
				if (isAsyncReplicationAllowed(q)) {
					LOGGER.debug("Post Update event on : Entity: " + q.getClass().getSimpleName() + " with ID : "
							+ q.getObjectId());
					enqueue(q, QueuedRecordAction.UPDATE);
				}
			}
		}
	}

	// The following events are ignored for queueing purposes:
	public void postAdd(Object entity) {
		// Not used
	}

	public void postLoad(Object entity) {
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
		
		QueuedTransactionContext transactionContext = getTransactionContext();
		ObjectContext currentContext = transactionContext.getCurrentObjectContext();
		String transactionKey = commitingContext.getTransactionKey();
		
		QueuedTransaction t = transactionContext.getTransactionForKey(transactionKey);
		if (t == null) {
			t = currentContext.newObject(QueuedTransaction.class);
			Date today = new Date();
			t.setCreated(today);
			t.setModified(today);
			t.setTransactionKey(transactionKey);
			t.setCollege((College) currentContext.localObject(college.getObjectId(), null));
			transactionContext.assignTransactionToKey(transactionKey, t);
		}

		String entityName = entity.getObjectId().getEntityName();
		Long entityId = entity.getId();
		Long angelId = entity.getAngelId();

		LOGGER.debug(String.format("Creating QueuedRecord<id:%s, entityName:%s, action:%s, transactionKey:%s>", entityId, entityName,
				action, transactionKey));

		QueuedRecord qr = currentContext.newObject(QueuedRecord.class);
		qr.setCollege((College) currentContext.localObject(college.getObjectId(), null));
		qr.setEntityIdentifier(entityName);
		qr.setEntityWillowId(entityId);
		qr.setAngelId(angelId);
		qr.setQueuedTransaction(t);
		qr.setAction(action);
		qr.setNumberOfAttempts(0);
		qr.setLastAttemptTimestamp(new Date());
	}

	/**
	 * Method which decides, whether creation of QueuedRecords is allowed. We do
	 * not allow in progress payment transactions to be replicated.
	 * 
	 * @param entity
	 *            queued entity
	 * @return true - queued record allowed, false - not allowed.
	 */
	private boolean isAsyncReplicationAllowed(Queueable entity) {

		boolean isAsyncAllowed = true;
		
		if (entity instanceof Tag || entity instanceof Taggable) {
			//currently we do not allow Tags to be replicated willow-angel
			isAsyncAllowed = false;
		} else if (entity instanceof PaymentIn) {

			PaymentIn payment = (PaymentIn) entity;

			isAsyncAllowed = payment.getStatus() != null && payment.getStatus() != PaymentStatus.IN_TRANSACTION
					&& payment.getStatus() != PaymentStatus.CARD_DETAILS_REQUIRED;

		} else if (entity instanceof PaymentInLine) {

			PaymentInLine pLine = (PaymentInLine) entity;
			PaymentStatus status = pLine.getPaymentIn().getStatus();

			isAsyncAllowed = status != PaymentStatus.IN_TRANSACTION && status != PaymentStatus.CARD_DETAILS_REQUIRED;

		} else if (entity instanceof Enrolment) {
			Enrolment enrl = (Enrolment) entity;

			if (enrl.getInvoiceLine() != null && !enrl.getInvoiceLine().getInvoice().getPaymentInLines().isEmpty()) {
				PaymentStatus status = PaymentStatus.IN_TRANSACTION;
				for (PaymentInLine line : enrl.getInvoiceLine().getInvoice().getPaymentInLines()) {
					PaymentIn paymentIn = line.getPaymentIn();
					if (paymentIn.getStatus() != PaymentStatus.IN_TRANSACTION
							|| paymentIn.getStatus() != PaymentStatus.CARD_DETAILS_REQUIRED) {
						status = paymentIn.getStatus();
					}
				}

				isAsyncAllowed = status != PaymentStatus.IN_TRANSACTION && status != PaymentStatus.CARD_DETAILS_REQUIRED;
			} else {
				isAsyncAllowed = enrl.getStatus() != null && enrl.getStatus() != EnrolmentStatus.IN_TRANSACTION
						&& enrl.getStatus() != EnrolmentStatus.PENDING;
			}

		} else if (entity.getClass().getSimpleName().startsWith("Invoice")) {

			Invoice inv = null;

			if (entity instanceof Invoice) {
				inv = (Invoice) entity;
			} else if (entity instanceof InvoiceLine) {
				inv = ((InvoiceLine) entity).getInvoice();
			} else if (entity instanceof InvoiceLineDiscount) {
				inv = ((InvoiceLineDiscount) entity).getInvoiceLine().getInvoice();
			}

			if (inv != null) {

				if (!inv.getPaymentInLines().isEmpty()) {
					PaymentStatus status = PaymentStatus.IN_TRANSACTION;

					for (PaymentInLine line : inv.getPaymentInLines()) {
						PaymentIn paymentIn = line.getPaymentIn();
						if (paymentIn.getStatus() != PaymentStatus.IN_TRANSACTION
								|| paymentIn.getStatus() != PaymentStatus.CARD_DETAILS_REQUIRED) {
							status = paymentIn.getStatus();
						}
					}

					isAsyncAllowed = status != PaymentStatus.IN_TRANSACTION && status != PaymentStatus.CARD_DETAILS_REQUIRED;
				} else {
					isAsyncAllowed = inv != null && inv.getStatus() != null && inv.getStatus() != InvoiceStatus.IN_TRANSACTION
							&& inv.getStatus() != InvoiceStatus.PENDING;
				}

			} else {
				LOGGER.error("Unrecognized object " + entity + " is being attepted to requeue!");
			}
		}

		return isAsyncAllowed;
	}
}