/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.usi;

public class USIVerificationResult {
	
	private USIStatus usiStatus;
	
	private USIFieldStatus firstNameStatus;
	private USIFieldStatus lastNameStatus;
	private USIFieldStatus dateOfBirthStatus;
	
	public USIVerificationResult(USIStatus usiStatus, 
								 USIFieldStatus firstNameStatus, 
								 USIFieldStatus lastNameStatus, 
								 USIFieldStatus dateOfBirthStatus) {
		this.usiStatus = usiStatus;
		this.firstNameStatus = firstNameStatus;
		this.lastNameStatus = lastNameStatus;
		this.dateOfBirthStatus = dateOfBirthStatus;
	}
	
	public USIStatus getUsiStatus() {
		return usiStatus;
	}
	
	public USIFieldStatus getFirstNameStatus() {
		return firstNameStatus;
	}
	
	public USIFieldStatus getLastNameStatus() {
		return lastNameStatus;
	}
	
	public USIFieldStatus getDateOfBirthStatus() {
		return dateOfBirthStatus;
	}
}
