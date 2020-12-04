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

package ish.oncourse.server.api.v1.function

import ish.common.types.CourseEnrolmentType
import ish.oncourse.server.api.BidiMap
import ish.oncourse.server.api.v1.model.CourseEnrolmentTypeDTO

import static ish.common.types.CourseEnrolmentType.ENROLMENT_BY_APPLICATION
import static ish.common.types.CourseEnrolmentType.OPEN_FOR_ENROLMENT

class CourseFunctions {

    public static final BidiMap<CourseEnrolmentType, CourseEnrolmentTypeDTO> ENROLMENT_TYPE_MAP = new BidiMap<CourseEnrolmentType, CourseEnrolmentTypeDTO>() {{
        put(OPEN_FOR_ENROLMENT, CourseEnrolmentTypeDTO.OPEN_ENROLMENT)
        put(ENROLMENT_BY_APPLICATION, CourseEnrolmentTypeDTO.ENROLMENT_BY_APPLICATION)
    }}
}
