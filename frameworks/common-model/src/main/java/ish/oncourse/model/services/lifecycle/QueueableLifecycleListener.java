/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.model.services.lifecycle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.LifecycleListener;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.utils.EntityUtils;


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
			enqueue(queueable, false);
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
			enqueue(queueable, true);
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
			enqueue(queueable, false);
		}
	}

	/**
	 * Attempts to find existing Queue Records for the entity.
	 *
	 * <p>If found, these will be reused with attempt information reset and
	 * deletion information adjusted accordingly.</p>
	 *
	 * @param entity
	 * @param isForDeletion
	 */
	private void enqueue(Queueable entity, boolean isForDeletion) {

		if (EntityUtils.doQueue(entity)) {
			String entityName = entity.getObjectId().getEntityName();
			Long entityId = entity.getId();
			College entityCollege = entity.getCollege();

			Map<String, Object> map = new HashMap<String, Object>();
			map.put(QueuedRecord.COLLEGE_PROPERTY, entityCollege);
			map.put(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, entityName);
			map.put(QueuedRecord.ENTITY_WILLOW_ID_PROPERTY, entityId);

			Expression exp = ExpressionFactory.matchAllExp(map, Expression.EQUAL_TO);
			SelectQuery query = new SelectQuery(QueuedRecord.class);
			query.andQualifier(exp);
			@SuppressWarnings("unchecked")
			List<QueuedRecord> records = cayenneService.sharedContext().performQuery(query);

			ObjectContext oc = cayenneService.newContext();
			QueuedRecord qr = null;

			if ((records == null) || (records.isEmpty())) {
				qr = oc.newObject(QueuedRecord.class);
				qr.setCollege(entityCollege);
				qr.setEntityIdentifier(entityName);
				qr.setEntityWillowId(entityId);
			} else if (records.size() > 1) {
				// TODO: Should this be an exception instead or delete the others and create new?
				LOGGER.error("More than one Queue record for entity:" + entityName
						+ " with id:" + entityId);
				return;
			} else {
				QueuedRecord temp = records.get(0);
				qr = (QueuedRecord) oc.localObject(temp.getObjectId(), temp);
			}

			// TODO: Add for deletion marker
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