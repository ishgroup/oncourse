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

import ish.oncourse.server.api.v1.model.WorkingWithChildrenStatusDTO
import ish.oncourse.types.WorkingWithChildrenStatus

trait WorkingWithChildrenStatusDTOTrait {

    WorkingWithChildrenStatus getDbType() {
        switch (this as WorkingWithChildrenStatusDTO) {
           case WorkingWithChildrenStatusDTO.NOT_CHECKED:
                return WorkingWithChildrenStatus.NOT_CHECKED
            case WorkingWithChildrenStatusDTO.APPLICATION_IN_PROGRESS:
                return WorkingWithChildrenStatus.APPLICATION_IN_PROGRESS
            case WorkingWithChildrenStatusDTO.CLEARED:
                return WorkingWithChildrenStatus.CLEARED
            case WorkingWithChildrenStatusDTO.BARRED:
                return WorkingWithChildrenStatus.BARRED
            case WorkingWithChildrenStatusDTO.INTERIM_BARRED:
                return WorkingWithChildrenStatus.INTERIM_BARRED
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    WorkingWithChildrenStatusDTO fromDbType(WorkingWithChildrenStatus dataType) {
        if(!dataType) {
            return null
        }
        switch (dataType) {
            case WorkingWithChildrenStatus.NOT_CHECKED:
                return WorkingWithChildrenStatusDTO.NOT_CHECKED
            case WorkingWithChildrenStatus.APPLICATION_IN_PROGRESS:
                return WorkingWithChildrenStatusDTO.APPLICATION_IN_PROGRESS
            case WorkingWithChildrenStatus.CLEARED:
                return WorkingWithChildrenStatusDTO.CLEARED
            case WorkingWithChildrenStatus.BARRED:
                return WorkingWithChildrenStatusDTO.BARRED
            case WorkingWithChildrenStatus.INTERIM_BARRED:
                return WorkingWithChildrenStatusDTO.INTERIM_BARRED
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }

}
