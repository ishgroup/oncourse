/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import ish.common.types.GradingEntryType
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._GradingType

import javax.annotation.Nonnull

/**
 * The GradingType allows to describe possible grading types. The description includes into itself:
 * - name
 * - min value of allowed range
 * - max value of allowed range
 * - entry type: numberic or entries.
 * If the entry type of grading is entries the grading type will have list of graiding items.
 */
@API
@QueueableEntity
class GradingType extends _GradingType implements Queueable {

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
    String getTypeName() {
        return super.getTypeName()
    }

    /**
     * @return min allowed value for this grading type
     */
    @Nonnull
    @API
    @Override
    BigDecimal getMinValue() {
        return super.getMinValue()
    }

    /**
     * @return max allowed value for this grading type
     */
    @Nonnull
    @API
    @Override
    BigDecimal getMaxValue() {
        return super.getMaxValue()
    }

    /**
     * @return entry type
     */
    @Nonnull
    @API
    @Override
    GradingEntryType getEntryType() {
        return super.getEntryType()
    }

    /**
     * @return list of grading items, which related to this type
     */
    @Nonnull
    @API
    @Override
    List<GradingItem> getGradingItems() {
        return super.getGradingItems()
    }
}
