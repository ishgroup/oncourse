package ish.oncourse.services.textile.validator;

import static org.junit.Assert.*;
import ish.oncourse.model.WebContent;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BlockTextileValidatorTest {

	private static final String TEST_NAME_NOT_EXIST = "oneName";
	private static final String TEST_BLOCK_NAME = "blockName";
	private String separator = System.getProperty("line.separator");

	/**
	 * service under the test
	 */
	private BlockTextileValidator blockTextileValidator;

	@Mock
	private IWebContentService webContentService;

	private ValidationErrors errors;

	private WebContent webContent;

	@Before
	public void init() {
		webContent = new WebContent();
		when(
				webContentService.getWebContent(WebContent.NAME_PROPERTY,
						TEST_BLOCK_NAME)).thenReturn(webContent);
		when(
				webContentService.getWebContent(WebContent.NAME_PROPERTY,
						TEST_NAME_NOT_EXIST)).thenReturn(null);

		blockTextileValidator = new BlockTextileValidator(webContentService);
		errors = new ValidationErrors();
	}

	/**
	 * Emulates the situation when {block name:"blockName"} is converted, the
	 * webContent with name "blockName" exists. Errors should be empty.
	 */
	@Test
	public void smokeBlockTextileValidatorTest() {
		blockTextileValidator.validate("{block name:\"" + TEST_BLOCK_NAME
				+ "\"}", errors);
		assertFalse(errors.hasFailures());
	}

	/**
	 * Emulates the situation when the textile format is incorrect, errors
	 * should contain warning.
	 */
	@Test
	public void incorrectBlockFormatTest() {
		String tag = "{block nameeeee:\"" + TEST_BLOCK_NAME + "\"}";
		blockTextileValidator.validate(tag, errors);
		assertTrue(errors.hasFailures());
		assertEquals(blockTextileValidator.getFormatErrorMessage(tag), errors
				.toString());
	}

	/**
	 * Emulates the situation when the textile contains two "name" attributes;
	 * errors should contain warning.
	 */
	@Test
	public void doubleNamedTest() {
		String tag = "{block name:\"" + TEST_BLOCK_NAME
				+ "\" name:\"anotherName\"}";
		blockTextileValidator.validate(tag, errors);
		assertTrue(errors.hasFailures());
		assertEquals(TextileUtil.getDoubledParamErrorMessage(tag,
				TextileUtil.PARAM_NAME), errors.toString());
	}

	/**
	 * Emulates the situation when the block with the given name doesn't exist;
	 * errors should contain warning.
	 */
	@Test
	public void blockNotExistTest() {
		String tag = "{block name:\"" + TEST_NAME_NOT_EXIST + "\"}";

		blockTextileValidator.validate(tag, errors);
		assertTrue(errors.hasFailures());
		assertEquals(blockTextileValidator
				.existErrorMessage(TEST_NAME_NOT_EXIST), errors.toString());
	}

	/**
	 * Emulates the situation when the textile contains error in format, two
	 * "name" attributes and the block with the first name doesn't exist; errors
	 * should contain warnings for every point.
	 */
	@Test
	public void allErrorsTest() {
		String tag = "{block name:\"" + TEST_NAME_NOT_EXIST
				+ "\" name:\"anotherName\" zzzzz}";

		blockTextileValidator.validate(tag, errors);
		assertTrue(errors.hasFailures());
		assertEquals(blockTextileValidator.getFormatErrorMessage(tag)
				+ separator
				+ TextileUtil.getDoubledParamErrorMessage(tag,
						TextileUtil.PARAM_NAME) + separator
				+ blockTextileValidator.existErrorMessage(TEST_NAME_NOT_EXIST),
				errors.toString());
	}

}
