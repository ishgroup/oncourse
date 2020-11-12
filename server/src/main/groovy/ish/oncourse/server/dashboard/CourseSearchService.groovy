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

package ish.oncourse.server.dashboard

import ish.common.types.KeyCode
import ish.oncourse.server.api.v1.model.SearchItemDTO
import ish.oncourse.server.cayenne.Course

class CourseSearchService extends EntitySearchService<Course> {

    @Override
    Class<Course> getEntityClass() {
        Course
    }

    @Override
    KeyCode getKeyCode() {
        return KeyCode.COURSE
    }

    @Override
    SearchItemDTO toSearchItem(Course obj) {
        toSearchItem(obj.id,
                "$obj.name $obj.code"
        )
    }
}
