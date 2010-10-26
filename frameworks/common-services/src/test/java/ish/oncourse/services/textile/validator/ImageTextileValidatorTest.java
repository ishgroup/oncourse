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
	@Mock
	private IBinaryDataService binaryDataService;
	private BinaryData binaryData;
	private BinaryInfo binaryInfo;

	@Override
	protected Map<String, String> getDataForUniquenceTest() {
		Map<String, String> data = new HashMap<String, String>();
		for (ImageTextileAttributes attr : ImageTextileAttributes.values()) {
			switch (attr) {
			case IMAGE_PARAM_ALIGH:
				data.put(ImageTextileAttributes.IMAGE_PARAM_ALIGH.getValue(),
						"{image align:\"right\" align:\"left\"}");
				break;

			case IMAGE_PARAM_ALT:
				data.put(ImageTextileAttributes.IMAGE_PARAM_ALT.getValue(),
						"{image alt:\"altText1\" alt:\"altText2\"}");
				break;

			case IMAGE_PARAM_CAPTION:
				data.put(ImageTextileAttributes.IMAGE_PARAM_CAPTION.getValue(),
						"{image caption:\"Text1\" caption:\"Text2\"}");
				break;
			case IMAGE_PARAM_CLASS:
				data.put(ImageTextileAttributes.IMAGE_PARAM_CLASS.getValue(),
						"{image class:\"Text1\" class:\"Text2\"}");
				break;
			case IMAGE_PARAM_HEIGHT:
				data.put(ImageTextileAttributes.IMAGE_PARAM_HEIGHT.getValue(),
						"{image height:\"30\" height:\"50\"}");
				break;
			case IMAGE_PARAM_ID:
				data.put(ImageTextileAttributes.IMAGE_PARAM_ID.getValue(),
						"{image id:\"30\" id:\"50\"}");
				break;
			case IMAGE_PARAM_LINK:
				data.put(ImageTextileAttributes.IMAGE_PARAM_LINK.getValue(),
						"{image link:\"link1\" link:\"link2\"}");
				break;
			case IMAGE_PARAM_NAME:
				data.put(ImageTextileAttributes.IMAGE_PARAM_NAME.getValue(),
						"{image name:\"name1\" name:\"name2\"}");
				break;
			case IMAGE_PARAM_TITLE:
				data.put(ImageTextileAttributes.IMAGE_PARAM_TITLE.getValue(),
						"{image title:\"title1\" title:\"title2\"}");
				break;
			case IMAGE_PARAM_WIDTH:
				data.put(ImageTextileAttributes.IMAGE_PARAM_WIDTH.getValue(),
						"{image width:\"100\" width:\"200\"}");
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
		binaryInfo = new BinaryInfo();
		binaryInfo.setReferenceNumber(TEST_BINARYINFO_REFERENCE_NUMBER);
		when(
				binaryDataService.getBinaryInfo(
						BinaryInfo.REFERENCE_NUMBER_PROPERTY,
						TEST_BINARYINFO_REFERENCE_NUMBER)).thenReturn(
				binaryInfo);
		when(binaryDataService.getBinaryData(binaryInfo))
				.thenReturn(binaryData);
	}

	/**
	 * Image textile should have at least one of the required parameters: name
	 * or id.
	 */
	@Test
	public void requiredAttrsTest() {
		String tag = "{image alt:\"altText\"}";
		validator.validate(tag, errors);
		assertTrue(errors.hasFailures());
		assertTrue(errors.contains(((ImageTextileValidator) validator)
				.getRequiredAttrsMessage(tag)));
	}
}
