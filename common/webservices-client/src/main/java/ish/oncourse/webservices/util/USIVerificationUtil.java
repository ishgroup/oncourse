/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.util;

import ish.common.types.USIFieldStatus;
import ish.common.types.USIVerificationRequest;
import ish.common.types.USIVerificationResult;
import ish.common.types.USIVerificationStatus;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class USIVerificationUtil {
	
	private static final String USI_BIRTH_DATE_FORMAT = "dd/MM/yyyy";
	
	public static final String USI_FIRST_NAME = "usi_first_name";
	public static final String USI_LAST_NAME = "usi_last_name";
	public static final String USI_BIRTH_DATE = "usi_birth_date";
	public static final String USI_CODE = "usi_code";
	public static final String USI_ORGCODE = "usi_orgcode";
	public static final String ERROR_MESSAGE = "error_message";
	
	public static USIVerificationRequest parseVerificationRequest(GenericParametersMap parametersMap) {
		Map<String, String> usiParams = parseParametersMap(parametersMap);

		USIVerificationRequest request = new USIVerificationRequest();
		
		request.setStudentFirstName(usiParams.get(USI_FIRST_NAME));
		request.setStudentLastName(usiParams.get(USI_LAST_NAME));
		request.setUsiCode(usiParams.get(USI_CODE));
		request.setOrgCode(usiParams.get(USI_ORGCODE));

		DateFormat dateFormat = new SimpleDateFormat(USI_BIRTH_DATE_FORMAT);

		String birthDateStr = usiParams.get(USI_BIRTH_DATE);
		
		try {
			request.setStudentBirthDate(dateFormat.parse(birthDateStr));
		} catch (ParseException e) {
			throw new IllegalArgumentException(String.format("Date format is not dd/MM/yyyy: %s", birthDateStr));
		}
		
		return request;
	}
	
	public static USIVerificationResult parseVerificationResult(GenericParametersMap parametersMap) {
		Map<String, String> usiParams = parseParametersMap(parametersMap);
		
		if (StringUtils.isNotBlank(usiParams.get(ERROR_MESSAGE))) {
			return USIVerificationResult.valueOf(usiParams.get(ERROR_MESSAGE));
		}

		USIVerificationResult result = new USIVerificationResult();
		
		result.setUsiStatus(USIVerificationStatus.valueOf(usiParams.get(USI_CODE)));
		result.setFirstNameStatus(USIFieldStatus.valueOf(usiParams.get(USI_FIRST_NAME)));
		result.setLastNameStatus(USIFieldStatus.valueOf(usiParams.get(USI_LAST_NAME)));
		result.setDateOfBirthStatus(USIFieldStatus.valueOf(usiParams.get(USI_BIRTH_DATE)));
		
		return result;
	}
	
	public static GenericParametersMap createVerificationRequestParametersMap(USIVerificationRequest request, SupportedVersions stubVersion) {
		DateFormat dateFormat = new SimpleDateFormat(USI_BIRTH_DATE_FORMAT);
		
		GenericParametersMap parametersMap = PortHelper.createParametersMap(stubVersion);
		
		parametersMap.getGenericEntry().add(createEntry(USI_FIRST_NAME, request.getStudentFirstName(), stubVersion));
		parametersMap.getGenericEntry().add(createEntry(USI_LAST_NAME, request.getStudentLastName(), stubVersion));
		parametersMap.getGenericEntry().add(createEntry(USI_BIRTH_DATE, dateFormat.format(request.getStudentBirthDate()), stubVersion));
		parametersMap.getGenericEntry().add(createEntry(USI_CODE, request.getUsiCode(), stubVersion));
		parametersMap.getGenericEntry().add(createEntry(USI_ORGCODE, request.getOrgCode(), stubVersion));
		
		return parametersMap;
	}
	
	public static GenericParametersMap createVerificationResultParametersMap(USIVerificationResult result, SupportedVersions stubVersion) {
		GenericParametersMap parametersMap = PortHelper.createParametersMap(stubVersion);
		
		parametersMap.getGenericEntry().add(createEntry(USI_FIRST_NAME, result.getFirstNameStatus().name(), stubVersion));
		parametersMap.getGenericEntry().add(createEntry(USI_LAST_NAME, result.getLastNameStatus().name(), stubVersion));
		parametersMap.getGenericEntry().add(createEntry(USI_BIRTH_DATE, result.getDateOfBirthStatus().name(), stubVersion));
		parametersMap.getGenericEntry().add(createEntry(USI_CODE, result.getUsiStatus().name(), stubVersion));
		if (result.hasError()) {
			parametersMap.getGenericEntry().add(createEntry(ERROR_MESSAGE, result.getErrorMessage(), stubVersion));
		}
		
		return parametersMap;
	}
	
	private static GenericParameterEntry createEntry(String name, String value, SupportedVersions stubVersion) {
		GenericParameterEntry entry = PortHelper.createParameterEntry(stubVersion);
		
		entry.setName(name);
		entry.setValue(value);
		
		return entry;
	}
	
	public static Map<String, String> parseParametersMap(GenericParametersMap parametersMap) {
		Map<String, String> map = new HashMap<>();
		
		for (GenericParameterEntry entry : parametersMap.getGenericEntry()) {
			map.put(entry.getName(), entry.getValue());
		}
		
		return map;
	}
}
