/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.service.impl

import javax.inject.Inject
import ish.oncourse.server.api.service.LogsApiService
import ish.oncourse.server.api.v1.model.DatesIntervalDTO
import ish.oncourse.server.api.v1.model.LogFileDTO
import ish.oncourse.server.api.v1.service.LogsApi

class LogsApiImpl implements LogsApi {
    @Inject
    private LogsApiService logsApiService

    @Override
    LogFileDTO getLogs(DatesIntervalDTO intervalDTO) {
        logsApiService.getCompressedLogsForPeriod(intervalDTO)
    }
}
