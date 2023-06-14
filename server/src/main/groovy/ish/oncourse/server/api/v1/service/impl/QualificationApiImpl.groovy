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

import com.google.inject.Inject
import ish.oncourse.server.api.service.QualificationApiService
import ish.oncourse.server.api.v1.model.QualificationDTO
import ish.oncourse.server.api.v1.service.QualificationApi

class QualificationApiImpl implements QualificationApi {

    @Inject
    private QualificationApiService qualificationApiService

    @Override
    void create(QualificationDTO qualification) {
        qualificationApiService.create(qualification)
    }

    @Override
    QualificationDTO get(Long id) {
        qualificationApiService.get(id)
    }

    @Override
    void update(Long id, QualificationDTO model) {
        qualificationApiService.update(id, model)
    }

    @Override
    void remove(Long id) {
        qualificationApiService.remove(id)
    }
}
