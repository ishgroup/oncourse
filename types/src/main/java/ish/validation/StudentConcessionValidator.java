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

package ish.validation;

import ish.oncourse.cayenne.StudentConcessionInterface;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anarut on 9/13/16.
 */
public class StudentConcessionValidator implements Validator<StudentConcessionErrorCode> {

	private StudentConcessionInterface studentConcession;
	private Map<String, StudentConcessionErrorCode> result;

	private StudentConcessionValidator() {

	}

	public static StudentConcessionValidator valueOf(StudentConcessionInterface studentConcession) {
		StudentConcessionValidator studentConcessionValidator = new StudentConcessionValidator();
		studentConcessionValidator.studentConcession = studentConcession;
		studentConcessionValidator.result = new HashMap<>();
		return studentConcessionValidator;
	}


	@Override
	public Map<String, StudentConcessionErrorCode> validate() {
		validateConcessionType();

		validateConcessionNumber();

		validateExpiryDate();

		return result;
	}

	private void validateConcessionType() {
		if (studentConcession.getConcessionType() == null) {
			result.put(StudentConcessionInterface.CONCESSION_TYPE_PROPERTY, StudentConcessionErrorCode.concessionTypeNeedToBeProvided);
		}
	}

	private void validateConcessionNumber() {
		if (studentConcession.getConcessionType() != null && Boolean.TRUE.equals(studentConcession.getConcessionType().getHasConcessionNumber())) {
			if (StringUtils.isBlank(studentConcession.getConcessionNumber())) {
				result.put(StudentConcessionInterface.CONCESSION_NUMBER_PROPERTY, StudentConcessionErrorCode.concessionNumberNeedToBeProvided);
			}
		}
	}

	private void validateExpiryDate() {
		if (studentConcession.getConcessionType() != null && Boolean.TRUE.equals(studentConcession.getConcessionType().getHasExpiryDate())) {
			if (studentConcession.getExpiresOn() == null) {
				result.put(StudentConcessionInterface.EXPIRES_ON_PROPERTY, StudentConcessionErrorCode.expiryDateNeedToBeProvided);
			}
		}
	}
}
