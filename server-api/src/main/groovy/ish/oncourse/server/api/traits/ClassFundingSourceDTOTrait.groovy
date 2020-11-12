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

import ish.common.types.ClassFundingSource
import ish.oncourse.server.api.v1.model.ClassFundingSourceDTO

trait ClassFundingSourceDTOTrait {
    ClassFundingSource getDbType() {
        switch (this as ClassFundingSourceDTO) {
            case ClassFundingSourceDTO.COMMONWEALTH_AND_STATE_GENERAL_RECURRENT:
                return ClassFundingSource.COMMONWEALTH_AND_STATE_GENERAL
            case ClassFundingSourceDTO.COMMONWEALTH_SPECIFIC:
                return ClassFundingSource.COMMONWEALTH_SPECIFIC
            case ClassFundingSourceDTO.DOMESTIC_FULL_FEE_PAYING_STUDENT:
                return ClassFundingSource.DOMESTIC_FULL_FEE
            case ClassFundingSourceDTO.INTERNATIONAL_FULL_FEE_PAYING_STUDENT:
                return ClassFundingSource.INTERNATIONAL_FULL_FEE
            case ClassFundingSourceDTO.REVENUE_FROM_OTHER_RTO:
                return ClassFundingSource.REVENUE_FROM_OTHER_TO
            case ClassFundingSourceDTO.STATE_SPECIFIC:
                return ClassFundingSource.STATE_SPECIFIC
            case ClassFundingSourceDTO.INTERNATIONAL_ONSHORE_CLIENT:
                return ClassFundingSource.INTERNATIONAL_ONSHORE
            case ClassFundingSourceDTO.INTERNATIONAL_OFFSHORE_CLIENT:
                return ClassFundingSource.INTERNATIONAL_OFFSHORE
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    ClassFundingSourceDTO fromDbType(ClassFundingSource dataType) {
        switch (dataType) {
            case ClassFundingSource.COMMONWEALTH_AND_STATE_GENERAL:
                return ClassFundingSourceDTO.COMMONWEALTH_AND_STATE_GENERAL_RECURRENT
            case ClassFundingSource.COMMONWEALTH_SPECIFIC:
                return ClassFundingSourceDTO.COMMONWEALTH_SPECIFIC
            case ClassFundingSource.DOMESTIC_FULL_FEE:
                return ClassFundingSourceDTO.DOMESTIC_FULL_FEE_PAYING_STUDENT
            case ClassFundingSource.INTERNATIONAL_FULL_FEE:
                return ClassFundingSourceDTO.INTERNATIONAL_FULL_FEE_PAYING_STUDENT
            case ClassFundingSource.REVENUE_FROM_OTHER_TO:
                return ClassFundingSourceDTO.REVENUE_FROM_OTHER_RTO
            case ClassFundingSource.STATE_SPECIFIC:
                return ClassFundingSourceDTO.STATE_SPECIFIC
            case ClassFundingSource.INTERNATIONAL_ONSHORE:
                return ClassFundingSourceDTO.INTERNATIONAL_ONSHORE_CLIENT
            case ClassFundingSource.INTERNATIONAL_OFFSHORE:
                return ClassFundingSourceDTO.INTERNATIONAL_OFFSHORE_CLIENT
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }
}
