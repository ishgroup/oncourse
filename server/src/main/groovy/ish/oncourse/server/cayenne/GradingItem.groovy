/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._GradingItem

import javax.annotation.Nonnull

/**
 * The GradingItem is one of the entry of grading type, if the grading entry type is entries.
 * The GradingItem has next properties:
 * - name
 * - a lower bound which allows specifying the appropriate entry
 */
@API
@QueueableEntity
class GradingItem extends _GradingItem {

    /**
     * @return the date and time this record was created
     */
    @API
    @Override
    Date getCreatedOn() {
        return super.getCreatedOn()
    }

    /**
     * @return the date and time this record was modified
     */
    @API
    @Override
    Date getModifiedOn() {
        return super.getModifiedOn()
    }

    /**
     * @return name of this grading type
     */
    @Nonnull
    @API
    @Override
    String getItemName() {
        return super.getItemName()
    }

    /**
     * @return a lower bound of allowed range for the entry
     */
    @Nonnull
    @API
    @Override
    BigDecimal getLowerBound() {
        return super.getLowerBound()
    }

    /**
     * @return a grading type which this item has related to
     */
    @Nonnull
    @API
    @Override
    GradingType getGradingType() {
        return super.getGradingType()
    }
}
