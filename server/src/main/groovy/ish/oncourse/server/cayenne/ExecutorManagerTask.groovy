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


package ish.oncourse.server.cayenne

import ish.oncourse.server.cayenne.glue._ExecutorManagerTask
import ish.oncourse.server.cluster.TaskResult

import java.util.concurrent.Future

class ExecutorManagerTask extends _ExecutorManagerTask {

    transient Future<?> localTask

    TaskResult getTaskResult() {
        def result = new TaskResult(getStatus())
        result.setData(getResult())
        result.setError(getErrorMessage())
        result.setStatusMessage(getStatusMessage())
        result.setName(getTaskName())
        result.setResultOutputType(getOutputType())
        result
    }
}



