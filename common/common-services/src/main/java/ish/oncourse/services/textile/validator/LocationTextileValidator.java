package ish.oncourse.services.textile.validator;

import ish.oncourse.model.Document;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.textile.TextileType;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.ImageTextileAttributes;
import ish.oncourse.services.textile.attrs.LocationTextileAttribute;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationFailureType;

import java.util.Map;

public class LocationTextileValidator extends AbstractTextileValidator {

	@Override
	protected void initValidator() {
		textileType = TextileType.LOCATION;
	}

	@Override
	protected void specificTextileValidate(String tag, ValidationErrors errors) {
		Map<String, String> tagParams = TextileUtil.getTagParams(tag, textileType.getAttributes());
		String name = tagParams.get(LocationTextileAttribute.NAME.getValue());
		if (name == null) {
			errors.addFailure(getRequiredAttrsMessage(tag), ValidationFailureType.SYNTAX);
		}
	}

	/**
	 * @param tag
	 * @return
	 */
	public String getRequiredAttrsMessage(String tag) {
		return "The location: " + tag + " must contain the required attribute: name";
	}

	public String getFormatErrorMessage(String tag) {
		return "The location tag '" + tag + "' doesn't match {location name:\"name\"}";
	}
}
