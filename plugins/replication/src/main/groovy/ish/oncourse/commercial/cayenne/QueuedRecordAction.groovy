/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.cayenne

enum QueuedRecordAction {
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
    DELETE("Delete", "Delete")

    /**
     * Display name for the item.
     */
    private String displayName

    /**
     * The value stored in db.
     */
    private String databaseValue

    QueuedRecordAction(String databaseValue, String displayName) {
        this.databaseValue = databaseValue
        this.displayName = displayName
    }

    /**
     * {@inheritDoc}
     *
     * @see org.apache.cayenne.ExtendedEnumeration#getDatabaseValue()
     */
    Object getDatabaseValue() {
        return this.databaseValue
    }

    /**
     * {@inheritDoc}
     *
     * @see ish.common.util.DisplayableExtendedEnumeration#getDisplayName()
     */
    String getDisplayName() {
        return this.displayName
    }
}