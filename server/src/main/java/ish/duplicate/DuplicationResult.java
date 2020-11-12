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
package ish.duplicate;



import org.apache.cayenne.validation.ValidationFailure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DuplicationResult implements Serializable {

	private List<Long> newIds = new ArrayList<>();
	private ValidationFailure failure;
	private boolean failed = false;

	public ValidationFailure getFailure() {
		return failure;
	}

	public void setFailure(ValidationFailure failure) {
		this.failure = failure;
	}

	public boolean isFailed() {
		return failed;
	}

	public void setFailed(boolean failed) {
		this.failed = failed;
	}

	public List<Long> getNewIds() {
		return newIds;
	}

	public void setNewIds(List<Long> newIds) {
		this.newIds = newIds;
	}

	public void addNewId(Long id) {
		this.newIds.add(id);
	}
}
