/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.traits

import ish.common.types.CourseClassType
import ish.oncourse.server.api.v1.model.CourseClassTypeDTO

trait CourseClassTypeDTOTrait {

    CourseClassType getDbType() {
        switch (this as CourseClassTypeDTO) {
            case CourseClassTypeDTO.WITH_SESSIONS:
                return CourseClassType.WITH_SESSIONS
            case CourseClassTypeDTO.DISTANT_LEARNING:
                return CourseClassType.DISTANT_LEARNING
            case CourseClassTypeDTO.HYBRID:
                return CourseClassType.HYBRID
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    CourseClassTypeDTO fromDbType(CourseClassType dataType) {
        if(!dataType) {
            return null
        }
        switch(dataType) {
            case CourseClassType.WITH_SESSIONS:
                return CourseClassTypeDTO.WITH_SESSIONS
            case CourseClassType.DISTANT_LEARNING:
                return CourseClassTypeDTO.DISTANT_LEARNING
            case CourseClassType.HYBRID:
                return CourseClassTypeDTO.HYBRID
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }

}