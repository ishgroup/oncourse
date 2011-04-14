/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.services.lifecycle;

import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.QueuedRecordAction;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.persistence.ISHObjectContext;
import ish.oncourse.services.persistence.WillowChangeSetFilter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.cayenne.LifecycleListener;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.lifecycle.changeset.ChangeSet;
import org.apache.cayenne.lifecycle.changeset.PropertyChange;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.Relationship;
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
			LOGGER.debug("Post Remove event on : Entity: " + queueable.getClass().getSimpleName() + " with ID : "
					+ queueable.getObjectId());
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
			if (isPropertyChanged(queueable)) {
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
		
		if (college == null) {
			college = objectContextMap().remove(entity.getObjectId());
		}
		
		ISHObjectContext commitingContext = (ISHObjectContext) college.getObjectContext();
		
		if (commitingContext.getIsRecordQueueingEnabled()) {
		
			String transactionKey = commitingContext.getTransactionKey();

			String entityName = entity.getObjectId().getEntityName();
			Long entityId = entity.getId();

			ObjectContext ctx = cayenneService.newNonReplicatingContext();
			
			LOGGER.debug(String.format("Creating QueuedRecord<id:%s, entityName:%s, action:%s, transactionKey:%s>", entityId, entityName, action, transactionKey));
			
			QueuedRecord qr = ctx.newObject(QueuedRecord.class);
			
			
			qr.setCollege((College) ctx.localObject(college.getObjectId(), null));
			qr.setEntityIdentifier(entityName);
			qr.setEntityWillowId(entityId);
			qr.setAction(action);
			qr.setNumberOfAttempts(0);
			qr.setLastAttemptTimestamp(new Date());
			qr.setTransactionKey(transactionKey);

			ctx.commitChanges();
		}
	}
	
	//Determine if any property was changes, thus skipping updates which cased by add/remove relationship
	private boolean isPropertyChanged(Queueable entity) {
		
		ChangeSet changeSet = WillowChangeSetFilter.preCommitChangeSet(entity.getObjectContext());	
		
		if (changeSet == null) {
			return false;
		}
		
		Map<String, PropertyChange> changes = changeSet.getChanges(entity);
		
		ObjEntity objEntity = entity.getObjectContext().getEntityResolver().getObjEntity(entity.getObjectId().getEntityName());
		
		for (ObjAttribute attr : objEntity.getAttributes()) {
			if(changes.containsKey(attr.getName())) {
				return true;
			}
		}
				
		for (Relationship r : objEntity.getRelationships()) {
			if (!r.isToMany() && changes.containsKey(r.getName())) {
				return true;
			}
		}
		
		return false;
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