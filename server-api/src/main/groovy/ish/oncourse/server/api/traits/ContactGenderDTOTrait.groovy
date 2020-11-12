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

import ish.common.types.Gender
import ish.oncourse.server.api.v1.model.ContactGenderDTO

trait ContactGenderDTOTrait {

    Gender getDbType() {
        switch (this as ContactGenderDTO) {
            case ContactGenderDTO.OTHER:
                return Gender.OTHER_GENDER
            case ContactGenderDTO.MALE:
                return Gender.MALE
            case ContactGenderDTO.FEMALE:
                return Gender.FEMALE
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    ContactGenderDTO fromDbType(Gender dataType) {
        if(dataType == null) {
            return null
        }
        switch (dataType) {
            case Gender.MALE:
                return ContactGenderDTO.MALE
            case Gender.FEMALE:
                return ContactGenderDTO.FEMALE
            case Gender.OTHER_GENDER:
                return ContactGenderDTO.OTHER

            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }
}
