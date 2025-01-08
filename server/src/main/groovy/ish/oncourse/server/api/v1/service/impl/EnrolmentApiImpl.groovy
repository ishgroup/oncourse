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
import ish.oncourse.server.api.service.EnrolmentApiService
import ish.oncourse.server.api.v1.model.CancelEnrolmentDTO
import ish.oncourse.server.api.v1.model.EnrolmentDTO
import ish.oncourse.server.api.v1.service.EnrolmentApi

class EnrolmentApiImpl implements EnrolmentApi {

    @Inject
    private EnrolmentApiService service


    @Override
    void cancel(CancelEnrolmentDTO cancelEnrolmentDTO) {
        service.cancelEnrolment(cancelEnrolmentDTO)
    }

    @Override
    EnrolmentDTO get(Long id) {
        service.get(id)
    }

    @Override
    void update(Long id, EnrolmentDTO enrolment) {
        service.update(id, enrolment)
    }
}
