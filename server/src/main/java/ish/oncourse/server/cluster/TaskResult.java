/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cluster;

import ish.common.types.TaskResultType;
import ish.oncourse.types.OutputType;

public class TaskResult {

    private final TaskResultType resultType;

    private byte[] data = null;

    private String error;
    private String name;
    private OutputType resultOutputType;
    private String statusMessage;

    public TaskResult(TaskResultType resultType) {
        this.resultType = resultType;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setError(String errorString) {
        this.error = errorString;
    }

    public boolean hasErrors() {
        return !error.isEmpty();
    }

    public String getError() {
        return error;
    }

    public TaskResultType getType() {
        return resultType;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setResultOutputType(OutputType resultOutputType) {
        this.resultOutputType = resultOutputType;
    }

    public OutputType getResultOutputType() {
        return resultOutputType;
    }
}
