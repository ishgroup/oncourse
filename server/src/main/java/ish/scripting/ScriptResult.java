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
package ish.scripting;

import ish.common.types.TaskResultType;
import ish.oncourse.server.cluster.TaskResult;

import java.io.Serializable;

public class ScriptResult extends TaskResult implements Serializable {

	private Object localResult;

	public ScriptResult(TaskResultType resultType) {
		super(resultType);
	}

	public static ScriptResult success(Object resultValue) {
		ScriptResult result = new ScriptResult(TaskResultType.SUCCESS);
		if(resultValue instanceof byte[]) {
			result.setData((byte[]) resultValue);
		} else if(resultValue instanceof String) {
			result.setData(((String) resultValue).getBytes());
		} else if(resultValue != null) {
			// TODO process all other result types
			result.setData(resultValue.toString().getBytes());
		}
		result.localResult = resultValue;
		return result;
	}

	public static ScriptResult failure(String error) {
		ScriptResult result = new ScriptResult(TaskResultType.FAILURE);
		result.setError(error);
		return result;
	}

	public Object getLocalResult() {
		return localResult;
	}
}
