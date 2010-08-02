package ish.oncourse.services.textile;

import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.util.ValidationErrors;

public abstract class AbstractRenderer implements IRenderer {
	
	protected IValidator validator;

	public String render(String tag, ValidationErrors errors, IBinaryDataService binaryDataService) {
		validator.validate(tag, errors, binaryDataService);
		return tag;
	}
	
}
