/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cluster;

import ish.common.types.TaskResultType;
import ish.oncourse.server.api.v1.model.ProcessStatusDTO;

public class ClusteredExecutorUtils {
    public static ProcessStatusDTO statusFrom(TaskResultType taskStatus) {
        if (taskStatus == null) {
            return ProcessStatusDTO.NOT_FOUND;
        }

        switch (taskStatus) {
            case SUCCESS:
                return ProcessStatusDTO.FINISHED;
            case FAILURE:
                return ProcessStatusDTO.FAILED;
            case IN_PROGRESS:
                return ProcessStatusDTO.IN_PROGRESS;
        }

        return ProcessStatusDTO.NOT_FOUND;
    }
}
