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
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.persistence.ISHObjectContext;

import java.util.Date;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.LifecycleListener;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectIdQuery;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;

/**
 * Listens for lifecycle events on entities and creates queue records for
 * queueable records.
 * 
 * @author marek
 */
public class QueueableLifecycleListener implements LifecycleListener {

	private static final Logger LOGGER = Logger.getLogger(QueueableLifecycleListener.class);

	private ICayenneService cayenneService;

	private Map<ObjectId, College> contextMap = new WeakHashMap<ObjectId, College>();

	public QueueableLifecycleListener(ICayenneService cayenneService) {
		this.cayenneService = cayenneService;
	}

	public void preUpdate(Object entity) {
		if (entity instanceof Queueable) {
			Queueable p = (Queueable) entity;
			p.setModified(new Date());
		}
	}

	public void prePersist(Object entity) {
		if (entity instanceof Queueable) {
			Queueable p = (Queueable) entity;
			p.setCreated(new Date());
		}
	}

	public void preRemove(Object entity) {
		if (entity instanceof Queueable) {
			Queueable p = (Queueable) entity;
			if (isAsyncReplicationAllowed(p)) {
				ObjectIdQuery query = new ObjectIdQuery(p.getObjectId(), false, ObjectIdQuery.CACHE_REFRESH);
				Cayenne.objectForQuery(p.getObjectContext(), query);
				contextMap.put(p.getObjectId(), p.getCollege());
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
			Queueable queueable = (Queueable) entity;
			if (isAsyncReplicationAllowed(queueable)) {
				LOGGER.debug("Post Persist event on : Entity: " + queueable.getClass().getSimpleName() + " with ID : "
						+ queueable.getObjectId());
				enqueue(queueable, QueuedRecordAction.CREATE);
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
			Queueable p = (Queueable) entity;
			if (isAsyncReplicationAllowed(p)) {
				LOGGER.debug("Pre Remove event on : Entity: " + p.getClass().getSimpleName() + " with ID : " + p.getObjectId());
				enqueue(p, QueuedRecordAction.DELETE);
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
			Queueable queueable = (Queueable) entity;
			if (isAsyncReplicationAllowed(queueable)) {
				LOGGER.debug("Post Update event on : Entity: " + queueable.getClass().getSimpleName() + " with ID : "
						+ queueable.getObjectId());
				enqueue(queueable, QueuedRecordAction.UPDATE);
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
		ISHObjectContext commitingContext = null;

		if (college == null) {
			college = contextMap.remove(entity.getObjectId());
			commitingContext = (ISHObjectContext) college.getObjectContext();
		} else {
			commitingContext = (ISHObjectContext) college.getObjectContext();
			ObjectIdQuery query = new ObjectIdQuery(entity.getObjectId(), false, ObjectIdQuery.CACHE_REFRESH);
			entity = (Queueable) Cayenne.objectForQuery(commitingContext, query);
		}

		if (commitingContext.getIsRecordQueueingEnabled()) {

			String transactionKey = commitingContext.getTransactionKey();

			String entityName = entity.getObjectId().getEntityName();
			Long entityId = entity.getId();
			Long angelId = entity.getAngelId();

			ObjectContext ctx = cayenneService.newNonReplicatingContext();

			SelectQuery q = new SelectQuery(QueuedTransaction.class);
			q.andQualifier(ExpressionFactory.matchExp(QueuedTransaction.TRANSACTION_KEY_PROPERTY, transactionKey));

			QueuedTransaction t = (QueuedTransaction) Cayenne.objectForQuery(ctx, q);

			if (t == null) {
				t = ctx.newObject(QueuedTransaction.class);
				Date today = new Date();
				t.setCreated(today);
				t.setModified(today);
				t.setTransactionKey(transactionKey);
				t.setCollege((College) ctx.localObject(college.getObjectId(), null));
			}

			LOGGER.debug(String.format("Creating QueuedRecord<id:%s, entityName:%s, action:%s, transactionKey:%s>", entityId, entityName,
					action, transactionKey));

			QueuedRecord qr = ctx.newObject(QueuedRecord.class);

			qr.setCollege((College) ctx.localObject(college.getObjectId(), null));
			qr.setEntityIdentifier(entityName);
			qr.setEntityWillowId(entityId);
			qr.setAngelId(angelId);
			qr.setQueuedTransaction(t);
			qr.setAction(action);
			qr.setNumberOfAttempts(0);
			qr.setLastAttemptTimestamp(new Date());

			ctx.commitChanges();
		}
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

		if (entity instanceof PaymentIn) {

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