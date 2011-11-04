package ish.oncourse.services.textile.validator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import ish.oncourse.model.BinaryData;
import ish.oncourse.model.BinaryInfo;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.textile.attrs.ImageTextileAttributes;
import ish.oncourse.util.ValidationErrors;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ImageTextileValidatorTest extends CommonValidatorTest {

	private static final Integer TEST_BINARYINFO_REFERENCE_NUMBER = 100;
	private static final String TEST_BINARYINFO_NAME = "image name";

	private static final Integer NOT_EXISTING_REFERENCE_NUMBER = 200;
	private static final String NOT_EXISTING_NAME = "another name";

	private static final Integer REF_NUM_OF_EMPTY_BINARY_INFO = 0;
	@Mock
	private IBinaryDataService binaryDataService;
	private BinaryData binaryData;
	@Mock
	private BinaryInfo binaryInfo;
	@Mock
	private BinaryInfo emptyBinaryInfo;

	@Override
	protected Map<String, String> getDataForUniquenceTest() {
		Map<String, String> data = new HashMap<String, String>();
		for (ImageTextileAttributes attr : ImageTextileAttributes.values()) {
			switch (attr) {
			case IMAGE_PARAM_ALIGH:
				data.put(ImageTextileAttributes.IMAGE_PARAM_ALIGH.getValue(), "{image align:\"right\" align:\"left\"}");
				break;

			case IMAGE_PARAM_ALT:
				data.put(ImageTextileAttributes.IMAGE_PARAM_ALT.getValue(), "{image alt:\"altText1\" alt:\"altText2\"}");
				break;

			case IMAGE_PARAM_CAPTION:
				data.put(ImageTextileAttributes.IMAGE_PARAM_CAPTION.getValue(),
						"{image caption:\"Text1\" caption:\"Text2\"}");
				break;
			case IMAGE_PARAM_CLASS:
				data.put(ImageTextileAttributes.IMAGE_PARAM_CLASS.getValue(), "{image class:\"Text1\" class:\"Text2\"}");
				break;
			case IMAGE_PARAM_HEIGHT:
				data.put(ImageTextileAttributes.IMAGE_PARAM_HEIGHT.getValue(), "{image height:\"30\" height:\"50\"}");
				break;
			case IMAGE_PARAM_ID:
				data.put(ImageTextileAttributes.IMAGE_PARAM_ID.getValue(), "{image id:\"30\" id:\"50\"}");
				break;
			case IMAGE_PARAM_LINK:
				data.put(ImageTextileAttributes.IMAGE_PARAM_LINK.getValue(), "{image link:\"link1\" link:\"link2\"}");
				break;
			case IMAGE_PARAM_NAME:
				data.put(ImageTextileAttributes.IMAGE_PARAM_NAME.getValue(), "{image name:\"name1\" name:\"name2\"}");
				break;
			case IMAGE_PARAM_TITLE:
				data.put(ImageTextileAttributes.IMAGE_PARAM_TITLE.getValue(),
						"{image title:\"title1\" title:\"title2\"}");
				break;
			case IMAGE_PARAM_WIDTH:
				data.put(ImageTextileAttributes.IMAGE_PARAM_WIDTH.getValue(), "{image width:\"100\" width:\"200\"}");
				break;
			}
		}
		return data;
	}

	@Override
	protected String getIncorrectFormatTextile() {
		return "{image id=\"200\"}";
	}

	@Override
	protected String getTextileForSmokeTest() {
		return "{image id:\"" + TEST_BINARYINFO_REFERENCE_NUMBER + "\"}";
	}

	@Override
	public void init() {
		validator = new ImageTextileValidator(binaryDataService);
		errors = new ValidationErrors();
		binaryData = new BinaryData();
		when(binaryDataService.getBinaryInfoByReferenceNumber(TEST_BINARYINFO_REFERENCE_NUMBER)).thenReturn(binaryInfo);
		when(binaryDataService.getBinaryInfo(BinaryInfo.NAME_PROPERTY, TEST_BINARYINFO_NAME)).thenReturn(binaryInfo);
		when(binaryDataService.getBinaryInfoByReferenceNumber(REF_NUM_OF_EMPTY_BINARY_INFO))
				.thenReturn(emptyBinaryInfo);
		when(binaryDataService.getBinaryInfoByReferenceNumber(NOT_EXISTING_REFERENCE_NUMBER)).thenReturn(null);
		when(binaryDataService.getBinaryInfo(BinaryInfo.NAME_PROPERTY, NOT_EXISTING_NAME)).thenReturn(null);
		when(binaryInfo.getBinaryData()).thenReturn(binaryData);
		when(binaryInfo.getReferenceNumber()).thenReturn(TEST_BINARYINFO_REFERENCE_NUMBER);

		when(emptyBinaryInfo.getBinaryData()).thenReturn(null);
		when(emptyBinaryInfo.getReferenceNumber()).thenReturn(REF_NUM_OF_EMPTY_BINARY_INFO);

	}

	/**
	 * Image textile should have at least one of the required parameters: name
	 * or id.
	 */
	@Test
	public void requiredAttrsTest() {
		String tag = "{image id:\"" + TEST_BINARYINFO_REFERENCE_NUMBER + "\"}";
		validator.validate(tag, errors);
		assertFalse(errors.hasFailures());

		tag = "{image name:\"" + TEST_BINARYINFO_NAME + "\"}";
		validator.validate(tag, errors);
		assertFalse(errors.hasFailures());

		tag = "{image alt:\"altText\"}";
		validator.validate(tag, errors);
		assertFalse(errors.hasFailures());
	}

	/**
	 * Emulates the situation when the binaryInfo with given ref number or name
	 * doesn't exist
	 */
	@Test
	public void notFoundBinaryInfoTest() {
		String tag = "{image id:\"" + NOT_EXISTING_REFERENCE_NUMBER + "\"}";
		validator.validate(tag, errors);
		assertTrue(errors.hasFailures());
		assertTrue(errors.contains(((ImageTextileValidator) validator)
				.getNotFoundByIdMessage(NOT_EXISTING_REFERENCE_NUMBER)));

		tag = "{image name:\"" + NOT_EXISTING_NAME + "\"}";
		validator.validate(tag, errors);
		assertTrue(errors.hasFailures());
		assertTrue(errors.contains(((ImageTextileValidator) validator).getNotFoundByNameMessage(NOT_EXISTING_NAME)));
	}

	/**
	 * Emulates the situation when the binaryInfo exists but the corresponded
	 * binary data doesn't
	 */
	@Test
	public void notFoundBinaryDataTest() {
		String tag = "{image id:\"" + REF_NUM_OF_EMPTY_BINARY_INFO + "\"}";
		validator.validate(tag, errors);
		assertTrue(errors.hasFailures());
		assertTrue(errors.contains(((ImageTextileValidator) validator).getNotFoundContentMessage()));
	}
	
	/**
	 * Emulates the situation when there is a new line in {image}, shouldn't be any errors.
	 */
	@Test
	public void imageWithNewLineTest() {
		String tag = "{image \n name:\"" + TEST_BINARYINFO_NAME + "\"}";
		validator.validate(tag, errors);
		assertFalse(errors.hasFailures());
	}
}
