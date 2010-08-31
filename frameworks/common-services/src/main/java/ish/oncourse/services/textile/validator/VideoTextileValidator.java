package ish.oncourse.services.textile.validator;

import java.util.Map;

import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;

public class VideoTextileValidator implements IValidator {

	public VideoTextileValidator(IBinaryDataService binaryDataService) {
		super();
	}

	public void validate(String tag, ValidationErrors errors) {
		boolean valid = true;
		if (!tag.matches(TextileUtil.VIDEO_TEMPLATE_EXP)) {
			errors
					.addFailure("The tag: "
							+ tag
							+ " doesn't match pattern {video type:\"youtube\" id:\"youtube_id\" height:\"digit_number\" width:\"digit_number\"}");
			valid = false;
		}
		if (tag.split(TextileUtil.PARAM_ID).length != 2) {
			errors.addFailure("The tag: " + tag
					+ " doesn't have the required unique \"id\" attribute");
			valid = false;
		}
		if (tag.split(TextileUtil.VIDEO_PARAM_TYPE).length != 2) {
			errors.addFailure("The \"type\" of video: " + tag + " is missing");
			valid = false;
		}
		if (tag.split(TextileUtil.PARAM_WIDTH).length > 2) {
			errors.addFailure("The tag: " + tag
					+ " can't contain more than one \"width\" attribute");
			valid = false;
		}
		if (tag.split(TextileUtil.PARAM_HEIGHT).length > 2) {
			errors.addFailure("The tag: " + tag
					+ " can't contain more than one \"height\" attribute");
			valid = false;
		}
		if (valid) {
			Map<String, String> tagParams = TextileUtil.getTagParams(tag,
					TextileUtil.VIDEO_PARAM_TYPE);
			String type = tagParams.get(TextileUtil.VIDEO_PARAM_TYPE);

			if (!"youtube".equals(type)) {
				errors
						.addFailure("The video of type \"youtube\" is supported only: "
								+ type);
			}
		}
	}
}
