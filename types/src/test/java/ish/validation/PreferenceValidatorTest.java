package ish.validation;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by anarut on 7/5/16.
 */
public class PreferenceValidatorTest {

	@Test
	public void testAccountInvoiceTermsValidation() {
		assertTrue(PreferenceValidator.isValidAccountInvoiceTerms(0));
		assertTrue(PreferenceValidator.isValidAccountInvoiceTerms(7));
		assertTrue(PreferenceValidator.isValidAccountInvoiceTerms(100));
		assertFalse(PreferenceValidator.isValidAccountInvoiceTerms(null));
		assertFalse(PreferenceValidator.isValidAccountInvoiceTerms(-30));
	}
}
