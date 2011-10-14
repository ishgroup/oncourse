package ish.oncourse.model;

import ish.common.util.DisplayableExtendedEnumeration;

import java.io.Serializable;

/**
 * Indicates the status of Queued Record. {@see QueuedRecord#getStatus()}
 *
 * @author Marek
 *
 */
public enum QueuedRecordAction implements Serializable, DisplayableExtendedEnumeration {
	/**
	 * Indicates that the corresponding record was created.
	 */
	CREATE("Create", "Create"),

	/**
	 * Indicates that the corresponding record was updated.
	 */
	UPDATE("Update", "Update"),

	/**
	 * Indicates that the corresponding record was deleted.
	 */
	DELETE("Delete", "Delete");


	/**
	 * Display name for the item.
	 */
	private String displayName;

	/**
	 * The value stored in db.
	 */
	private String databaseValue;

	private QueuedRecordAction(String databaseValue, String displayName) {
		this.databaseValue = databaseValue;
		this.displayName = displayName;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.apache.cayenne.ExtendedEnumeration#getDatabaseValue()
	 */
	public Object getDatabaseValue() {
		return databaseValue;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDisplayName()
	 */
	public String getDisplayName() {
		return displayName;
	}

}
