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

package ish.imports;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import ish.common.types.TaskResultType;
import ish.oncourse.server.cluster.TaskResult;

public class ImportResult extends TaskResult implements Serializable {

	private Map<String, List<Long>> createdRecords;
	private Map<String, List<Long>> modifiedRecords;

	public ImportResult() {
		super(TaskResultType.SUCCESS);
	}

	/**
	 * @return list of created records' ids mapped to their entity names
	 */
	public Map<String, List<Long>> getCreatedRecords() {
		return createdRecords;
	}

	/**
	 * Set map of entity names to ids for created records.
	 *
	 * @param createdRecords map of entity names to ids for created records
	 */
	public void setCreatedRecords(Map<String, List<Long>> createdRecords) {
		this.createdRecords = createdRecords;
	}

	/**
	 * @return list of modified records' ids mapped to their entity names
	 */
	public Map<String, List<Long>> getModifiedRecords() {
		return modifiedRecords;
	}

	/**
	 * Set map of entity names to ids for modified records.
	 *
	 * @param modifiedRecords map of entity names to ids for modified records
	 */
	public void setModifiedRecords(Map<String, List<Long>> modifiedRecords) {
		this.modifiedRecords = modifiedRecords;
	}

	/**
	 * @return error message returned by import
	 */
	public String getErrorMessage() {
		return getError();
	}

	/**
	 * Set error message for import.
	 *
	 * @param errorMessage error message
	 */
	public void setErrorMessage(String errorMessage) {
		setError(errorMessage);
	}
}
