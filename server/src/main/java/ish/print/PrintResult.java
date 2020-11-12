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
import java.util.Arrays;
import java.util.List;

/**
 */
public class PrintResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private byte[] result = null;

	private String error;

	private int progress = 0;

	private ResultType resultType;

	private String reportName;

	public PrintResult(ResultType resultType) {
		this.resultType = resultType;
	}

	/**
	 * @return the pdf result
	 */
	public byte[] getResult() {
		return this.result;
	}

	/**
	 * @param result
	 */
	public void setResult(byte[] result) {
		if (result != null) {
			this.result = result.clone();
		}
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

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public int getProgress() {
		return progress;
	}

	public ResultType getResultType() {
		return resultType;
	}

	public enum ResultType {
		SUCCESS,
		IN_PROGRESS,
		FAILED;

		public static final List<ResultType> RESULTS_FINISHED = Arrays.asList(SUCCESS, FAILED);
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	@Override
	public String toString() {
		return "PrintResult{" +
				"error='" + error + '\'' +
				", result size=" + result.length +
				", progress=" + progress +
				", resultType=" + resultType +
				'}';
	}
}
