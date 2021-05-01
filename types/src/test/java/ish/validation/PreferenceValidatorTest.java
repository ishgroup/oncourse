package ish.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;




/**
 * Created by anarut on 7/5/16.
 */
public class PreferenceValidatorTest {

	@Test
	public void testAccountInvoiceTermsValidation() {
		Assertions.Assertions.assertTrue(PreferenceValidator.isValidAccountInvoiceTerms(0));
		Assertions.Assertions.assertTrue(PreferenceValidator.isValidAccountInvoiceTerms(7));
		Assertions.Assertions.assertTrue(PreferenceValidator.isValidAccountInvoiceTerms(100));
		Assertions.Assertions.assertFalse(PreferenceValidator.isValidAccountInvoiceTerms(null));
		Assertions.Assertions.assertFalse(PreferenceValidator.isValidAccountInvoiceTerms(-30));
	}
}
