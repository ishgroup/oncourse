package ish.oncourse.services.textile.validator;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;

public class ImageTextileValidator implements IValidator {

	public void validate(String tag, ValidationErrors errors,
			Object dataService) {
		if (getImageBinaryInfoByTag(tag, errors, dataService) == null) {
			errors
					.addFailure("The image tag '"
							+ tag
							+ "' doesn't match nor {image id:\"id\"}, nor {image name:\"name\"}");
		}
	}

	public BinaryInfo getImageBinaryInfoByTag(String tag,
			ValidationErrors errors, Object dataService) {
		IBinaryDataService binaryDataService = (IBinaryDataService) dataService;
		BinaryInfo result = null;
		if (tag.matches(TextileUtil.IMAGE_ID_REGEXP)) {
			String id = TextileUtil.getValueInFirstQuots(tag);
			result = binaryDataService.getBinaryInfo(BinaryInfo.ID_PK_COLUMN,
					Long.valueOf(id));
			if (result == null) {
				errors.addFailure("There's no image with the id: " + id);
			}
		} else if (tag.matches(TextileUtil.IMAGE_NAME_REGEXP)) {
			String name = TextileUtil.getValueInFirstQuots(tag);
			result = binaryDataService.getBinaryInfo(BinaryInfo.NAME_PROPERTY,
					name);
			if (result == null) {
				errors.addFailure("There's no image with the name: " + name);
			}
		}
		return result;
	}

}
