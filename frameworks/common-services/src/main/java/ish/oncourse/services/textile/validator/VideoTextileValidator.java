package ish.oncourse.services.textile.validator;

import java.util.Map;

import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;

public class VideoTextileValidator implements IValidator {

	public void validate(String tag, ValidationErrors errors) {
		if (!tag.matches(TextileUtil.VIDEO_REGEXP)) {
			errors.addFailure(getFormatErrorMessage(tag));
		}
		TextileUtil.checkRequiredParams(tag, errors, TextileUtil.PARAM_ID,
				TextileUtil.VIDEO_PARAM_TYPE);
		TextileUtil.checkParamsUniquence(tag, errors, TextileUtil.PARAM_ID,
				TextileUtil.VIDEO_PARAM_TYPE, TextileUtil.PARAM_WIDTH,
				TextileUtil.PARAM_HEIGHT);
		Map<String, String> tagParams = TextileUtil.getTagParams(tag,
				TextileUtil.VIDEO_PARAM_TYPE);
		String type = tagParams.get(TextileUtil.VIDEO_PARAM_TYPE);

		if (!"youtube".equals(type)) {
			errors
					.addFailure("The video of type \"youtube\" is supported only: "
							+ type);
		}

	}

	public String getFormatErrorMessage(String tag) {
		return "The tag: "
				+ tag
				+ " doesn't match pattern {video type:\"youtube\" id:\"youtube_id\" "
				+ "height:\"digit_number\" width:\"digit_number\"}";
	}
}
