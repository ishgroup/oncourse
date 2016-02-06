package ish.oncourse.portal.usi.handler;

import ish.oncourse.components.AvetmissStrings;
import ish.oncourse.model.Country;
import ish.oncourse.model.Student;
import ish.oncourse.portal.usi.Result;
import ish.oncourse.portal.usi.UsiController;
import ish.oncourse.portal.usi.Value;
import ish.oncourse.util.MessagesNamingConvention;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.internal.util.MessagesImpl;

import java.util.Map;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class CountryOfBirthHandler extends AbstractValueHandler<Student> {

	private Messages avetmissMessages = MessagesImpl.forClass(AvetmissStrings.class);

	private UsiController usiController;

	public void handle() {
		try {
			Value value = inputValues.get(key);
			if (value == null || value.getValue() == null) {
				result.addValue(Value.valueOf(key, null, usiController.getMessages().format("message-fieldRequired")));
				result.setHasErrors(true);
				return;
			}
			Country country = usiController.getCountryService().getCountryByName((String) value.getValue());
			if (country != null) {
				entity.setCountryOfBirth(entity.getObjectContext().localObject(country));
				result.addValue(value);
			} else {
				result.addValue(Value.valueOf(key, value.getValue(), avetmissMessages.format(String.format(MessagesNamingConvention.MESSAGE_KEY_TEMPLATE, key))));
				result.setHasErrors(true);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}


	public static CountryOfBirthHandler valueOf(Student student, String key, Map<String, Value> inputValues, Result result, UsiController usiController) {
		CountryOfBirthHandler handler = new CountryOfBirthHandler();
		handler.entity = student;
		handler.key = key;
		handler.inputValues = inputValues;
		handler.result = result;
		handler.usiController = usiController;
		return handler;
	}
}
