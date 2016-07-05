package ish.validation;

/**
 * Created by anarut on 7/5/16.
 */
public class PreferenceValidator {

	public static boolean isValidAccountInvoiceTerms(Integer value) {
		return value != null && value >= 0;
	}
}
