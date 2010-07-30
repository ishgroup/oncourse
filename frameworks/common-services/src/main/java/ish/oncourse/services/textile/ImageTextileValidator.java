package ish.oncourse.services.textile;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.College;
import ish.oncourse.util.ValidationErrors;

import org.apache.cayenne.ObjectContext;

public class ImageTextileValidator implements IValidator {

	public void validate(String tag, ValidationErrors errors,
			ObjectContext context, College currentCollege) {
		tag = tag.replaceAll(" ", "");

		if (getImageBinaryInfoByTag(tag, errors, context, currentCollege) == null) {
			errors
					.addFailure("The image tag '"
							+ tag
							+ "' doesn't match nor {image:id=\"id\"}, nor {image:name=\"name\"}");
		}
	}

	public BinaryInfo getImageBinaryInfoByTag(String tag,
			ValidationErrors errors, ObjectContext context,
			College currentCollege) {
		BinaryInfo result = null;
		if (tag.matches(TextileUtil.IMAGE_ID_REGEXP)) {
			String id = TextileUtil.getValueInFirstQuots(tag);
			result = TextileUtil.getImageBinaryInfo(context,
					BinaryInfo.ID_PK_COLUMN, Long.valueOf(id), currentCollege);
			if (result == null) {
				errors.addFailure("There's no image with the id: " + id);
			}
		} else if (tag.matches(TextileUtil.IMAGE_NAME_REGEXP)) {
			String name = TextileUtil.getValueInFirstQuots(tag);
			result = TextileUtil.getImageBinaryInfo(context,
					BinaryInfo.NAME_PROPERTY, name, currentCollege);
			if (result == null) {
				errors.addFailure("There's no image with the name: " + name);
			}
		}
		return result;
	}

}
