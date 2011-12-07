package ish.oncourse.utils;

import org.apache.cayenne.PersistentObject;

public class QueueableObjectUtils {
	public static final String ID_PK_COLUMN = "id";
	
	/**
	 * @see ish.oncourse.model.Queueable#getId()
	 * @param entity entity to get id
	 * @return id value
	 */
	public static Long getId(final PersistentObject entity) {
		return getId(entity, ID_PK_COLUMN);
	}
	
	/**
	 * @see ish.oncourse.model.Queueable#getId()
	 * @param entity entity to get id
	 * @param columnName name of id column
	 * @return id value
	 */
	public static Long getId(final PersistentObject entity, final String columnName) {
		return (entity.getObjectId() != null && !entity.getObjectId().isTemporary()) ? (Long) entity.getObjectId().getIdSnapshot().get(columnName) : null;
	}
}
