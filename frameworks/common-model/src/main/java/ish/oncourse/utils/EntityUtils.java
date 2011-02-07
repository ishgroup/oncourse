/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.utils;

import org.apache.cayenne.Persistent;


/**
 *
 * @author marek
 */
public final class EntityUtils {

	private EntityUtils() {};

	public static String getEntityName(Persistent entity) {
		return entity.getObjectId().getEntityName();
	}

	public static Long getEntityId(Persistent entity) {
		return (entity.getObjectId() != null && !entity.getObjectId().isTemporary()) ?
				(Long) entity.getObjectId().getIdSnapshot().get("id") : null;
	}
}
