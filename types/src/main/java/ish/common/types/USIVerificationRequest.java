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
import java.util.Date;

public class USIVerificationRequest implements Serializable{

	private String studentFirstName;
	private String studentLastName;
	private Date studentBirthDate;
	private String usiCode;
	private String orgCode;

	public void setStudentFirstName(String studentFirstName) {
		if (StringUtils.trimToNull(studentFirstName) == null) {
			throw new IllegalArgumentException("Student first name must not be empty.");
		}

		this.studentFirstName = studentFirstName;
	}

	public String getStudentFirstName() {
		return studentFirstName;
	}

	public void setStudentLastName(String studentLastName) {
		if (StringUtils.trimToNull(studentLastName) == null) {
			throw new IllegalArgumentException("Student last name must not be empty.");
		}

		this.studentLastName = studentLastName;
	}

	public String getStudentLastName() {
		return studentLastName;
	}

	public void setStudentBirthDate(Date studentBirthDate) {
		if (studentBirthDate == null) {
			throw new IllegalArgumentException("Student's date of birth name must not be empty.");
		}

		this.studentBirthDate = studentBirthDate;
	}

	public Date getStudentBirthDate() {
		return studentBirthDate;
	}

	public void setUsiCode(String usiCode) {
		if (StringUtils.trimToNull(usiCode) == null) {
			throw new IllegalArgumentException("USI code must not be empty.");
		}

		this.usiCode = usiCode;
	}

	public String getUsiCode() {
		return usiCode;
	}

	public void setOrgCode(String orgCode) {
		if (StringUtils.trimToNull(orgCode) == null) {
			throw new IllegalArgumentException("Organisation code must not be empty.");
		}

		this.orgCode = orgCode;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public boolean isValid() {
		return studentFirstName != null && studentLastName != null && studentBirthDate != null && usiCode != null;
	}
}
