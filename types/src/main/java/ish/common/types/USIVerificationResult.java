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
package ish.common.types;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class USIVerificationResult implements Serializable {

	private USIVerificationStatus usiStatus;

	private USIFieldStatus firstNameStatus;
	private USIFieldStatus lastNameStatus;
	private USIFieldStatus dateOfBirthStatus;
	
	private String errorMessage;
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setUsiStatus(USIVerificationStatus usiStatus) {
		this.usiStatus = usiStatus;
	}

	public USIVerificationStatus getUsiStatus() {
		return usiStatus;
	}

	public void setFirstNameStatus(USIFieldStatus firstNameStatus) {
		this.firstNameStatus = firstNameStatus;
	}

	public USIFieldStatus getFirstNameStatus() {
		return firstNameStatus;
	}

	public void setLastNameStatus(USIFieldStatus lastNameStatus) {
		this.lastNameStatus = lastNameStatus;
	}

	public USIFieldStatus getLastNameStatus() {
		return lastNameStatus;
	}

	public void setDateOfBirthStatus(USIFieldStatus dateOfBirthStatus) {
		this.dateOfBirthStatus = dateOfBirthStatus;
	}

	public USIFieldStatus getDateOfBirthStatus() {
		return dateOfBirthStatus;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public boolean hasError() {
		return StringUtils.isNotBlank(errorMessage);
	}
	
	

	public USIVerificationResult() {
		this.errorMessage = null;
		this.usiStatus = USIVerificationStatus.INVALID;
		this.dateOfBirthStatus = USIFieldStatus.NO_MATCH;
		this.firstNameStatus = USIFieldStatus.NO_MATCH;
		this.lastNameStatus = USIFieldStatus.NO_MATCH;
	}
}
