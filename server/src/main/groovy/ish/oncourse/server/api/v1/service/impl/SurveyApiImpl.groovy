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

package ish.oncourse.server.api.v1.service.impl

import javax.inject.Inject
import ish.oncourse.server.api.service.SurveyApiService
import ish.oncourse.server.api.v1.model.SurveyItemDTO
import ish.oncourse.server.api.v1.service.SurveyApi

class SurveyApiImpl implements SurveyApi {

    @Inject
    private SurveyApiService service

    @Override
    SurveyItemDTO get(Long id) {
        service.get(id)
    }

    @Override
    void update(Long id, SurveyItemDTO studentFeedback) {
        service.update(id, studentFeedback)
    }
}
