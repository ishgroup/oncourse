/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.traits

import ish.common.types.ClassCostFlowType
import ish.oncourse.server.api.v1.model.ClassCostFlowTypeDTO

trait ClassCostFlowTypeDTOTrait {
    ClassCostFlowType getDbType() {
        switch (this as ClassCostFlowTypeDTO) {
            case ClassCostFlowTypeDTO.DISCOUNT:
                return ClassCostFlowType.DISCOUNT
            case ClassCostFlowTypeDTO.EXPENSE:
                return ClassCostFlowType.EXPENSE
            case ClassCostFlowTypeDTO.INCOME:
                return ClassCostFlowType.INCOME
            case ClassCostFlowTypeDTO.WAGES:
                return ClassCostFlowType.WAGES
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    ClassCostFlowTypeDTO fromDbType(ClassCostFlowType dataType) {
        switch (dataType) {
            case ClassCostFlowType.DISCOUNT:
                return ClassCostFlowTypeDTO.DISCOUNT
            case ClassCostFlowType.EXPENSE:
                return ClassCostFlowTypeDTO.EXPENSE
            case ClassCostFlowType.INCOME:
                return ClassCostFlowTypeDTO.INCOME
            case ClassCostFlowType.WAGES:
                return ClassCostFlowTypeDTO.WAGES
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }
}
