package ish.oncourse.services.textile.validator;

import java.util.List;
import java.util.Map;

import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.VideoTextileAttributes;
import ish.oncourse.util.ValidationErrors;

public class VideoTextileValidator implements IValidator {

	public void validate(String tag, ValidationErrors errors) {
		if (!tag.matches(TextileUtil.VIDEO_REGEXP)) {
			errors.addFailure(getFormatErrorMessage(tag));
		}
		TextileUtil.checkRequiredParams(tag, errors,
				VideoTextileAttributes.VIDEO_PARAM_ID.getValue(),
				VideoTextileAttributes.VIDEO_PARAM_TYPE.getValue());
		List<String> attrValues = VideoTextileAttributes.getAttrValues();
		TextileUtil.checkParamsUniquence(tag, errors, attrValues);
		Map<String, String> tagParams = TextileUtil.getTagParams(tag,
				attrValues);
		String type = tagParams.get(VideoTextileAttributes.VIDEO_PARAM_TYPE
				.getValue());

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
