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

import static ish.common.types.SurveyVisibility.NOT_TESTIMONIAL
import static ish.common.types.SurveyVisibility.REVIEW
import static ish.common.types.SurveyVisibility.STUDENT_HIDDEN
import static ish.common.types.SurveyVisibility.TESTIMONIAL
import ish.oncourse.server.api.BidiMap
import ish.oncourse.server.api.v1.model.SurveyVisibilityDTO

class StudentFeedbackFunctions {

    static final BidiMap<ish.common.types.SurveyVisibility, SurveyVisibilityDTO> VISIBILITY_MAP = new BidiMap<ish.common.types.SurveyVisibility, SurveyVisibilityDTO>() {{
        put(REVIEW, SurveyVisibilityDTO.WAITING_REVIEW)
        put(TESTIMONIAL, SurveyVisibilityDTO.PUBLIC_TESTIMONIAL)
        put(NOT_TESTIMONIAL, SurveyVisibilityDTO.NOT_TESTIMONIAL)
        put(STUDENT_HIDDEN, SurveyVisibilityDTO.HIDDEN_BY_STUDENT)
    }}
}
