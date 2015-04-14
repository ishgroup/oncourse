package ish.oncourse.services.textile.validator;

import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class CommonValidatorTest {

	/**
	 * service under the test
	 */
	protected IValidator validator;

	protected ValidationErrors errors;

	/**
	 * Provide the initialization of service to test and auxiliary fields
	 */
	@Before
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
			assertTrue(errors.toString().contains(TextileUtil.getDoubledParamErrorMessage(tag, key)));
			errors.clear();
		}
	}

	protected abstract Map<String, String> getDataForUniquenceTest();

	/**
	 * Emulates the situation when the textile format is incorrect, errors
	 * should contain warning.
	 */
	@Test
	public void incorrectFormatTest() {
		String tag = getIncorrectFormatTextile();
		validator.validate(tag, errors);
		assertTrue(errors.hasFailures());
		assertTrue(errors.contains(validator.getFormatErrorMessage(tag)));
	}

	protected abstract String getIncorrectFormatTextile();

}
