package ish.oncourse.services.textile.validator;

import java.util.Map;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;

public class ImageTextileValidator implements IValidator {

	private IBinaryDataService binaryDataService;

	public ImageTextileValidator(IBinaryDataService binaryDataService) {
		this.binaryDataService = binaryDataService;
	}

	public void validate(String tag, ValidationErrors errors) {
		BinaryInfo result = null;
		if (!tag.matches(TextileUtil.IMAGE_REGEXP)) {
			errors
					.addFailure("The image tag '"
							+ tag
							+ "' doesn't match {image id:\"id\" name:\"name\" align:\"right|left|center\" "
							+ "caption:\"your caption\" link:\"link\" title:\"title\" width:\"digit\" height:\"digit\" class:\"cssClass\"}");
		}
		if (!TextileUtil.hasRequiredParam(tag, TextileUtil.PARAM_ID)
				&& !TextileUtil.hasRequiredParam(tag, TextileUtil.PARAM_NAME)) {
			errors
					.addFailure("The image: "
							+ tag
							+ " must contain at least one of the required attributes: name or id");
		}
		TextileUtil.checkParamsUniquence(tag, errors, TextileUtil.PARAM_ID,
				TextileUtil.PARAM_NAME, TextileUtil.IMAGE_PARAM_ALIGH,
				TextileUtil.IMAGE_PARAM_CAPTION, TextileUtil.IMAGE_PARAM_LINK,
				TextileUtil.IMAGE_PARAM_TITLE, TextileUtil.PARAM_WIDTH,
				TextileUtil.PARAM_HEIGHT, TextileUtil.IMAGE_PARAM_CLASS);
		Map<String, String> tagParams = TextileUtil.getTagParams(tag,
				TextileUtil.PARAM_ID, TextileUtil.PARAM_NAME);
		String id = tagParams.get(TextileUtil.PARAM_ID);
		String name = tagParams.get(TextileUtil.PARAM_NAME);
		if (id != null) {
			result = binaryDataService.getBinaryInfo(BinaryInfo.ID_PK_COLUMN,
					Long.valueOf(id));
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

}
