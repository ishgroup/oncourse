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
import ish.imports.ImportResult
import ish.oncourse.server.api.v1.model.ProcessResultDTO
import ish.oncourse.server.api.v1.model.ProcessStatusDTO
import static ish.oncourse.server.api.v1.model.ProcessStatusDTO.*
import ish.oncourse.server.api.v1.service.ControlApi
import ish.oncourse.server.concurrent.ExecutorManager
import ish.print.PrintResult
import ish.scripting.ScriptResult

import static ish.scripting.ScriptResult.ResultType.FAILURE
import static org.apache.commons.lang3.StringUtils.isNotBlank

class ControlApiImpl implements ControlApi {

    @Inject private ExecutorManager executorManager

    @Override
    ProcessResultDTO getStatus(String processId) {
        ProcessStatusDTO status = executorManager.getStatus(processId)
        ProcessResultDTO processResult = new ProcessResultDTO()

        if (status in [FINISHED, FAILED]) {

            Object result = executorManager.getResult(processId)

            switch (result.class) {
                case PrintResult:
                    PrintResult printResult = result as PrintResult
                    if (isNotBlank(printResult.error)) {
                        processResult.status = FAILED
                        processResult.message = printResult.error
                        return processResult
                    }
                    break
                case ImportResult:
                    ImportResult importResult = result as ImportResult
                    if (isNotBlank(importResult.errorMessage)) {
                        processResult.status = FAILED
                        processResult.message = importResult.errorMessage
                        return processResult
                    }
                    break
                case ScriptResult:
                    ScriptResult scriptResult = result as ScriptResult
                    if (scriptResult.type == FAILURE) {
                        processResult.status = FAILED
                        processResult.message = scriptResult.error
                        return processResult

                    }
                    break
            }
        }

        processResult.status = executorManager.getStatus(processId)
        processResult.message = executorManager.getMessage(processId)
        return processResult
    }

    @Override
    void interrupt(String processId) {
        executorManager.interrupt(processId)
    }
}
