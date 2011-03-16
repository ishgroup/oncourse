package ish.oncourse.services.textile.validator;

import ish.oncourse.services.textile.TextileType;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.VideoTextileAttributes;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationFailureType;

import java.util.Map;

public class VideoTextileValidator extends AbstractTextileValidator {

	@Override
	protected void initValidator() {
		textileType = TextileType.VIDEO;
	}

	@Override
	protected void specificTextileValidate(String tag, ValidationErrors errors) {
		TextileUtil.checkRequiredParams(tag, errors,
				VideoTextileAttributes.VIDEO_PARAM_ID.getValue(),
				VideoTextileAttributes.VIDEO_PARAM_TYPE.getValue());
		Map<String, String> tagParams = TextileUtil.getTagParams(tag, textileType.getAttributes());
		String type = tagParams.get(VideoTextileAttributes.VIDEO_PARAM_TYPE.getValue());

		if (!"youtube".equals(type)) {
			errors.addFailure(getIncorrectTypeMessage(type),
					ValidationFailureType.CONTENT_NOT_FOUND);
		}

	}

	public String getFormatErrorMessage(String tag) {
		return "The tag: " + tag
				+ " doesn't match pattern {video type:\"youtube\" id:\"youtube_id\" "
				+ "height:\"digit_number\" width:\"digit_number\"}";
	}

	public String getIncorrectTypeMessage(String type) {
		return "The video of type \"youtube\" is supported only: " + type;
	}
}
