/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import javax.inject.Inject
import ish.common.types.GradingEntryType
import ish.oncourse.server.ICayenneService
import org.apache.cayenne.query.ObjectSelect

trait GradingTypeTrait {

    @Inject
    private ICayenneService cayenneService

    abstract List<GradingItem> getGradingItems()

    abstract GradingEntryType getEntryType()

    String getDisplayableNameOfGrade(BigDecimal grade){
        if(grade == null)
            return null
        if(getEntryType().equals(GradingEntryType.NUMBER))
            return grade.toString()

        return ObjectSelect.columnQuery(GradingItem.class,GradingItem.ITEM_NAME)
                .where(
                        GradingItem.GRADING_TYPE.eq((GradingType)this)
                                .andExp(GradingItem.LOWER_BOUND.lte(grade))
                )
                .selectFirst(cayenneService.newReadonlyContext)
    }
}