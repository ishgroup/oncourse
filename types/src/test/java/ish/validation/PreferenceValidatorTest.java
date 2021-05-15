package ish.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;




/**
 * Created by anarut on 7/5/16.
 */
public class PreferenceValidatorTest {

	@Test
	public void testAccountInvoiceTermsValidation() {
		Assertions.assertTrue(PreferenceValidator.isValidAccountInvoiceTerms(0));
		Assertions.assertTrue(PreferenceValidator.isValidAccountInvoiceTerms(7));
		Assertions.assertTrue(PreferenceValidator.isValidAccountInvoiceTerms(100));
		Assertions.assertFalse(PreferenceValidator.isValidAccountInvoiceTerms(null));
		Assertions.assertFalse(PreferenceValidator.isValidAccountInvoiceTerms(-30));
	}
}
