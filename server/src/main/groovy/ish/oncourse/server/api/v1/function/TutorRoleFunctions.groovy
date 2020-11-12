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

import ish.common.types.ClassCostRepetitionType
import ish.oncourse.server.api.BidiMap
import ish.oncourse.server.api.v1.model.RepetitionTypeDTO

class TutorRoleFunctions {

    static final BidiMap<ClassCostRepetitionType, RepetitionTypeDTO> REPETITION_TYPE_MAP = new BidiMap<ClassCostRepetitionType, RepetitionTypeDTO>() {{
        put(ClassCostRepetitionType.FIXED, RepetitionTypeDTO.FIXED)
        put(ClassCostRepetitionType.PER_SESSION, RepetitionTypeDTO.PER_SESSION)
        put(ClassCostRepetitionType.PER_ENROLMENT, RepetitionTypeDTO.PER_ENROLMENT)
        put(ClassCostRepetitionType.PER_UNIT, RepetitionTypeDTO.PER_UNIT)
        put(ClassCostRepetitionType.DISCOUNT, RepetitionTypeDTO.DISCOUNT)
        put(ClassCostRepetitionType.PER_TIMETABLED_HOUR, RepetitionTypeDTO.PER_TIMETABLED_HOUR)
        put(ClassCostRepetitionType.PER_STUDENT_CONTACT_HOUR, RepetitionTypeDTO.PER_STUDENT_CONTACT_HOUR)
    }}
}
