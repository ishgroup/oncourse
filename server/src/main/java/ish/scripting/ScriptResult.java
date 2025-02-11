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

import ish.oncourse.types.OutputType;

import java.io.Serializable;

public class ScriptResult implements Serializable {

	private ResultType resultType;

	private String name;
	private String error;
	private Object resultValue;
	private OutputType resultOutputType;

	public static ScriptResult success(Object resultValue) {
		ScriptResult result = new ScriptResult(ResultType.SUCCESS);
		result.setResultValue(resultValue);

		return result;
	}

	public static ScriptResult failure(String error) {
		ScriptResult result = new ScriptResult(ResultType.FAILURE);
		result.setError(error);

		return result;
	}

	private ScriptResult(ResultType type) {
		this.resultType = type;
	}

	private void setError(String error) {
		this.error = error;
	}

	private void setResultValue(Object resultValue) {
		this.resultValue = resultValue;
	}

	public void setResultOutputType(OutputType resultOutputType) {
		this.resultOutputType = resultOutputType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ResultType getType() {
		return resultType;
	}

	public Object getResultValue() {
		return resultValue;
	}

	public OutputType getResultOutputType() {
		return resultOutputType;
	}

	public String getError() {
		return error;
	}

	public enum ResultType {
		SUCCESS,
		FAILURE
	}
}
