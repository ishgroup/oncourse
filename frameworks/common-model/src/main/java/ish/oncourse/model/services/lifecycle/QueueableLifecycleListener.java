/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.model.services.lifecycle;

import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.QueuedRecordAction;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.utils.EntityUtils;

import org.apache.cayenne.LifecycleListener;
import org.apache.cayenne.ObjectContext;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;


/**
 * Listens for lifecycle events on entities and creates queue records for 
 * queueable records.
 *
 * @author marek
 */
public class QueueableLifecycleListener implements LifecycleListener {

	@Inject
	private ICayenneService cayenneService;

	private static final Logger LOGGER = Logger.getLogger(QueueableLifecycleListener.class);


	/**
	 * New record event - post save.
	 *
	 * @param entity
	 */
	public void postPersist(Object entity) {
		if (entity instanceof Queueable) {
			Queueable queueable = (Queueable) entity;
			LOGGER.debug("Post Persist event on : Entity: " + queueable.getClass().getSimpleName()
					+ " with ID : " + queueable.getObjectId());
			enqueue(queueable, QueuedRecordAction.CREATE);
		}
	}

	/**
	 * Delete record event - post delete.
	 *
	 * @param entity
	 */
	public void postRemove(Object entity) {
		if (entity instanceof Queueable) {
			Queueable queueable = (Queueable) entity;
			LOGGER.debug("Post Remove event on : Entity: " + queueable.getClass().getSimpleName()
					+ " with ID : " + queueable.getObjectId());
			enqueue(queueable, QueuedRecordAction.DELETE);
		}
	}

	/**
	 * Update record event - post save.
	 *
	 * @param entity
	 */
	public void postUpdate(Object entity) {
		if (entity instanceof Queueable) {
			Queueable queueable = (Queueable) entity;
			LOGGER.debug("Post Update event on : Entity: " + queueable.getClass().getSimpleName()
				+ " with ID : " + queueable.getObjectId());
			enqueue(queueable, QueuedRecordAction.UPDATE);
		}
	}

	/**
	 * Adds a new record to the queue.
	 *
	 * <p>Note that the code does not check for existing instances of the same
	 * record in the queue.</p>
	 *
	 * @param entity record to enqueue
	 * @param action the type of action that triggered the queueing {@see QueuedRecordAction}
	 */
	private void enqueue(Queueable entity, QueuedRecordAction action) {

		if (EntityUtils.doQueue(entity)) {
			String entityName = entity.getObjectId().getEntityName();
			Long entityId = entity.getId();
			College entityCollege = entity.getCollege();

			ObjectContext oc = cayenneService.newContext();
			QueuedRecord qr = oc.newObject(QueuedRecord.class);
			qr.setCollege(entityCollege);
			qr.setEntityIdentifier(entityName);
			qr.setEntityWillowId(entityId);
			qr.setAction(action);
			qr.setNumberOfAttempts(0);
			qr.setLastAttemptTimestamp(null);

			oc.commitChanges();
		}
	}

	// The following events are ignored for queueing purposes:
	public void postAdd(Object entity) {
		// Not used
	}
	public void postLoad(Object entity) {
		// Not used
	}
	public void prePersist(Object entity) {
		// Not used
	}
	public void preRemove(Object entity) {
		// Not used
	}
	public void preUpdate(Object entity) {
		// Not used
	}

}