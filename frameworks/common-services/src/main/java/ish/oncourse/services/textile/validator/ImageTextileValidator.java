package ish.oncourse.services.textile.validator;

import java.util.List;
import java.util.Map;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.ImageTextileAttributes;
import ish.oncourse.util.ValidationErrors;

public class ImageTextileValidator implements IValidator {

	private IBinaryDataService binaryDataService;

	public ImageTextileValidator(IBinaryDataService binaryDataService) {
		this.binaryDataService = binaryDataService;
	}

	public void validate(String tag, ValidationErrors errors) {
		BinaryInfo result = null;
		if (!tag.matches(TextileUtil.IMAGE_REGEXP)) {
			errors.addFailure(getFormatErrorMessage(tag));
		}
		if (!TextileUtil.hasRequiredParam(tag,
				ImageTextileAttributes.IMAGE_PARAM_ID.getValue())
				&& !TextileUtil.hasRequiredParam(tag,
						ImageTextileAttributes.IMAGE_PARAM_NAME.getValue())) {
			errors
					.addFailure(getRequiredAttrsMessage(tag));
		}
		List<String> attrValues = ImageTextileAttributes.getAttrValues();
		TextileUtil.checkParamsUniquence(tag, errors, attrValues);
		Map<String, String> tagParams = TextileUtil.getTagParams(tag,
				attrValues);
		String id = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_ID
				.getValue());
		String name = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_NAME
				.getValue());
		if (id != null) {
			result = binaryDataService.getBinaryInfo(
					BinaryInfo.REFERENCE_NUMBER_PROPERTY, Integer.valueOf(id));
			if (result == null) {
				errors.addFailure("There's no image with the id: " + id);
			}

		} else if (name != null) {
			result = binaryDataService.getBinaryInfo(BinaryInfo.NAME_PROPERTY,
					name);
			if (result == null) {
				errors.addFailure("There's no image with the name: " + name);
			}
		}
		if (result != null && binaryDataService.getBinaryData(result) == null) {
			errors.addFailure("This image's content is missed");
		}
	}

	/**
	 * @param tag
	 * @return
	 */
	public String getRequiredAttrsMessage(String tag) {
		return "The image: "
				+ tag
				+ " must contain at least one of the required attributes: name or id";
	}

	public String getFormatErrorMessage(String tag) {
		return "The image tag '"
				+ tag
				+ "' doesn't match {image id:\"id\" name:\"name\" align:\"right|left|center\" "
				+ "caption:\"your caption\" alt:\"your alt\" link:\"link\" "
				+ "title:\"title\" width:\"digit\" height:\"digit\" class:\"cssClass\"}";
	}

}
