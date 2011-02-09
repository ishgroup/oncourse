/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.utils;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.access.ISHDataContext;
import org.apache.cayenne.Persistent;


/**
 *
 * @author marek
 */
public final class EntityUtils {

	private EntityUtils() {};


	/**
	 * Convenience method for obtaining the Entity Name of the entity.
	 *
	 * @param entity
	 * @return Name of the entity as defined in the model
	 */
	public static String getEntityName(Persistent entity) {
		return entity.getObjectId().getEntityName();
	}

	/**
	 * Convenience method for retrieving the ID (Primary Key) of the entity.
	 *
	 * <p>Uses the convention of defining PKs as Long.</p>
	 *
	 * @param entity the record
	 * @return ID of the entity instance as Long
	 */
	public static Long getEntityId(Persistent entity) {
		return (entity.getObjectId() != null && !entity.getObjectId().isTemporary()) ?
				(Long) entity.getObjectId().getIdSnapshot().get("id") : null;
	}

	/**
	 * Checks the editing context of the entity to see if replication queueing
	 * required.
	 *
	 * @param entity to test
	 * @return true if entity needs to be queued, false otherwise
	 */
	public static boolean doQueue(Queueable entity) {
		return ((ISHDataContext) entity.getObjectContext()).getIsRecordQueueingEnabled();
	}
}
