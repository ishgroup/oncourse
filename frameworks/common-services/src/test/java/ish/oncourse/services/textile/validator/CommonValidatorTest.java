package ish.oncourse.services.textile.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;

import org.junit.Test;

public abstract class CommonValidatorTest {

	/**
	 * service under the test
	 */
	protected IValidator validator;

	protected ValidationErrors errors;

	/**
	 * Provide the initialization of service to test and auxiliary fields
	 */
	public abstract void init();

	/**
	 * Emulates the situation when the textile is validated normally; Errors
	 * should be empty.
	 */
	@Test
	public void smokeTextileValidatorTest() {
		validator.validate(getTextileForSmokeTest(), errors);
		assertFalse(errors.hasFailures());
	}

	protected abstract String getTextileForSmokeTest();

	/**
	 * Emulates the situations when the attribute of the textile is used more
	 * than once
	 */
	@Test
	public void uniquenceAttributesErrorTest() {
		Map<String, String> testData = getDataForUniquenceTest();
		for (String key : testData.keySet()) {
			String tag = testData.get(key);
			validator.validate(tag, errors);
			assertTrue(errors.hasFailures());
			assertEquals(TextileUtil.getDoubledParamErrorMessage(tag, key),
					errors.toString());
			errors.clear();
		}
	}

	protected abstract Map<String, String> getDataForUniquenceTest();
}
