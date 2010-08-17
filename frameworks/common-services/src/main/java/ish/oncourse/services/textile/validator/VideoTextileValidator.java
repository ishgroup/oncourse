package ish.oncourse.services.textile.validator;

import java.util.Map;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;

public class VideoTextileValidator implements IValidator {

	public void validate(String tag, ValidationErrors errors, Object dataService) {
		boolean valid = true;
		if (!tag.matches(TextileUtil.VIDEO_TEMPLATE_EXP)) {
			errors
					.addFailure("The tag: "
							+ tag
							+ " doesn't match pattern {video id:\"digit_number\" height:\"digit_number\" width:\"digit_number\"}");
			valid = false;
		}
		if (tag.split("id:").length != 2) {
			errors.addFailure("The tag: " + tag
					+ " doesn't have the required unique \"id\" attribute");
			valid = false;
		}
		if (tag.split("width:").length > 2) {
			errors.addFailure("The tag: " + tag
					+ " can't contain more than one \"width\" attribute");
			valid = false;
		}
		if (tag.split("height:").length > 2) {
			errors.addFailure("The tag: " + tag
					+ " can't contain more than one \"height\" attribute");
			valid = false;
		}
		if (valid) {
			Map<String, String> tagParams = TextileUtil.getTagParams(tag, "id",
					"width", "height");
			Long id = Long.valueOf(tagParams.get("id"));
			BinaryInfo video = ((IBinaryDataService) dataService)
					.getBinaryInfo(BinaryInfo.ID_PK_COLUMN, id);
			if (video == null) {
				errors.addFailure("There's no video with such an id: " + id);
			}
		}

	}

}
