package ish.oncourse.util.contact;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ioc.Messages;

import static ish.oncourse.components.ContactDetailStrings.KEY_ERROR_MESSAGE_suburbHasDigit;

/**
 * Created by anarut on 2/3/17.
 */
public class ValidateSuburb {

	private String suburb;
	private Messages errorMessages;
	
	private ValidateSuburb() {
		
	}
	
	public static ValidateSuburb valueOf(String suburb, Messages errorMessages) {
		ValidateSuburb validateSuburb = new ValidateSuburb();
		validateSuburb.suburb = suburb;
		validateSuburb.errorMessages = errorMessages;
		return validateSuburb;
	}

	public String getErrorMessage() {
		if (StringUtils.isNotBlank(suburb)) {
			if (suburb.split("\\d").length != 1) {
				return errorMessages.get(KEY_ERROR_MESSAGE_suburbHasDigit);
			}
		}

		return null;
	}
}
