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
import ish.oncourse.server.api.v1.model.ProcessResultDTO
import ish.oncourse.server.api.v1.model.ProcessStatusDTO
import ish.oncourse.server.cluster.ClusteredExecutorManager

import ish.oncourse.server.api.v1.service.ControlApi

class ControlApiImpl implements ControlApi {

    @Inject private ClusteredExecutorManager clusteredExecutorManager

    @Override
    ProcessResultDTO getStatus(String processId) {
        ProcessStatusDTO status = clusteredExecutorManager.getStatus(processId)
        ProcessResultDTO processResult = new ProcessResultDTO()

        processResult.status = status
        if(status == ProcessStatusDTO.FAILED) {
            processResult.message = clusteredExecutorManager.getResult(processId).getError()
        }
        return processResult
    }

    @Override
    void interrupt(String processId) {
        clusteredExecutorManager.interrupt(processId)
    }
}
