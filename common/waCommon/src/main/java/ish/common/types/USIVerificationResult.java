/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
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
	
	public static USIVerificationResult valueOf(String errorMessage) {
		USIVerificationResult result = new USIVerificationResult();
		result.errorMessage = errorMessage;
		result.usiStatus = USIVerificationStatus.INVALID;
		result.dateOfBirthStatus = USIFieldStatus.NO_MATCH;
		result.firstNameStatus = USIFieldStatus.NO_MATCH;
		result.lastNameStatus = USIFieldStatus.NO_MATCH;
		return result;
	}
}
