

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
import ish.oncourse.server.api.service.SessionApiService
import ish.oncourse.server.api.v1.model.SessionDTO
import ish.oncourse.server.api.v1.model.SessionWarningDTO
import ish.oncourse.server.api.v1.service.SessionApi

class SessionApiImpl implements SessionApi {

    public static final Long CLASS_TEMP_ID = -1

    @Inject
    private SessionApiService sessionApiService
    
    @Override
    List<SessionDTO> get(Long classId) {
        return sessionApiService.getList(classId)
    }

    @Override
    List<SessionWarningDTO> update(Long classId, List<SessionDTO> sessions) {
        return sessionApiService.update(classId, sessions)
    }

}
