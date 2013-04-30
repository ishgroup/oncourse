package ish.oncourse.services.textile.validator;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import ish.oncourse.model.WebContent;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.BlockTextileAttributes;
import ish.oncourse.util.ValidationErrors;

import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BlockTextileValidatorTest extends CommonValidatorTest {

	private static final String TEST_NAME_NOT_EXIST = "oneName";
	private static final String TEST_BLOCK_NAME = "blockName";
	private static final String TEST_ILLEGAL_BLOCK_NAME = "illegal's block Name";

	@Mock
	private IWebContentService webContentService;

	private WebContent webContent;

	public void init() {
		webContent = new WebContent();
		when(webContentService.getWebContent(WebContent.NAME_PROPERTY, TEST_BLOCK_NAME)).thenReturn(webContent);
		when(webContentService.getWebContent(WebContent.NAME_PROPERTY, TEST_NAME_NOT_EXIST)).thenReturn(null);

		validator = new BlockTextileValidator(webContentService);
		errors = new ValidationErrors();
	}

	/**
	 * Emulates the situation when the block with the given name doesn't exist;
	 * errors should contain warning.
	 */
	@Test
	public void blockNotExistTest() {
		String tag = "{block name:\"" + TEST_NAME_NOT_EXIST + "\"}";

		validator.validate(tag, errors);
		assertTrue(errors.hasFailures());
		assertTrue(errors.toString().contains(
				((BlockTextileValidator) validator).getBlockNotFoundErrorMessage(TEST_NAME_NOT_EXIST)));
	}
	
	/**
	 * Emulates the situation when there is a new line in {block}, shouldn't be any errors.
	 */
	@Test
	public void blockWithNewLineTest() {
		String tag = "{block \n  name:\"" + TEST_BLOCK_NAME + "\"}";
		validator.validate(tag, errors);
		assertFalse(errors.hasFailures());
	}
	
	@Test
	public void blockWithIllegalNameTest() {
		String tag = "{block \n  name:\"" + TEST_ILLEGAL_BLOCK_NAME + "\"}";
		validator.validate(tag, errors);
		assertTrue(errors.hasFailures());
		assertTrue(errors.toString().contains(((AbstractTextileValidator) validator).getSyntaxErrorMessage(TEST_ILLEGAL_BLOCK_NAME)));
	}

	/**
	 * Emulates the situation when the textile contains error in format, two
	 * "name" attributes and the block with the first name doesn't exist; errors
	 * should contain warnings for every point.
	 */
	@Test
	public void allErrorsTest() {
		String tag = "{block name:\"" + TEST_NAME_NOT_EXIST + "\" name:\"anotherName\" zzzzz}";

		validator.validate(tag, errors);
		assertTrue(errors.hasFailures());
		assertEquals(3, errors.getSize());
		assertTrue(errors.contains(validator.getFormatErrorMessage(tag)));
		assertTrue(errors.contains(TextileUtil.getDoubledParamErrorMessage(tag,
				BlockTextileAttributes.BLOCK_PARAM_NAME.getValue())));
		assertTrue(errors.contains(((BlockTextileValidator) validator)
				.getBlockNotFoundErrorMessage(TEST_NAME_NOT_EXIST)));
	}

	@Override
	protected String getTextileForSmokeTest() {
		return "{block name:\"" + TEST_BLOCK_NAME + "\"}";
	}

	@Override
	protected Map<String, String> getDataForUniquenceTest() {
		Map<String, String> data = new HashMap<>();
		for (BlockTextileAttributes attr : BlockTextileAttributes.values()) {
			switch (attr) {
			case BLOCK_PARAM_NAME:
				data.put(BlockTextileAttributes.BLOCK_PARAM_NAME.getValue(), "{block name:\"" + TEST_BLOCK_NAME
						+ "\" name:\"anotherName\"}");
				break;
			}
		}
		return data;
	}

	@Override
	protected String getIncorrectFormatTextile() {
		return "{block nameeeee:\"" + TEST_BLOCK_NAME + "\"}";
	}

}
