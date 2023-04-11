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
package ish.print;

import java.io.Serializable;

import ish.common.types.TaskResultType;
import ish.oncourse.server.cluster.TaskResult;
import ish.oncourse.types.OutputType;

public class PrintResult extends TaskResult implements Serializable {

	private static final long serialVersionUID = 1L;

	public PrintResult(TaskResultType resultType) {
		super(resultType);
		setResultOutputType(OutputType.PDF);
	}

	@Override
	public String toString() {
		return "PrintResult{" +
				"error='" + getError() + '\'' +
				", result size=" + getData().length +
				", resultType=" + getType() +
				'}';
	}
}
