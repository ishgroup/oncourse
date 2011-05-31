/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.services.lifecycle;

import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.QueuedRecordAction;
import ish.oncourse.model.QueuedTransaction;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.persistence.ISHObjectContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

	private ThreadLocal<Map<ObjectId, College>> ocStorage = new InheritableThreadLocal<Map<ObjectId, College>>();

	public QueueableLifecycleListener(ICayenneService cayenneService) {
		this.cayenneService = cayenneService;
	}

	public void preUpdate(Object entity) {
		// Not used
	}

	public void prePersist(Object entity) {
		// Not used
	}

	public void preRemove(Object entity) {
		if (entity instanceof Queueable) {
			Queueable p = (Queueable) entity;
			ObjectIdQuery query = new ObjectIdQuery(p.getObjectId(), false, ObjectIdQuery.CACHE_REFRESH);
			Cayenne.objectForQuery(p.getObjectContext(), query);
			objectContextMap().put(p.getObjectId(), p.getCollege());
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
			LOGGER.debug("Post Persist event on : Entity: " + queueable.getClass().getSimpleName() + " with ID : "
					+ queueable.getObjectId());
			enqueue(queueable, QueuedRecordAction.CREATE);
		}
	}

	/**
	 * Delete record event - post delete.
	 * 
	 * @param entity
	 */
	public void postRemove(Object entity) {
		// Not used
		if (entity instanceof Queueable) {
			Queueable queueable = (Queueable) entity;
			LOGGER.debug("Post Remove event on : Entity: " + queueable.getClass().getSimpleName() + " with ID : " + queueable.getObjectId());
			enqueue(queueable, QueuedRecordAction.DELETE);
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
			LOGGER.debug("Post Update event on : Entity: " + queueable.getClass().getSimpleName() + " with ID : " + queueable.getObjectId());
			enqueue(queueable, QueuedRecordAction.UPDATE);

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
			college = objectContextMap().remove(entity.getObjectId());
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

	private Map<ObjectId, College> objectContextMap() {
		Map<ObjectId, College> ctxMap = ocStorage.get();
		if (ctxMap == null) {
			ctxMap = new HashMap<ObjectId, College>();
			ocStorage.set(ctxMap);
		}
		return ctxMap;
	}
}