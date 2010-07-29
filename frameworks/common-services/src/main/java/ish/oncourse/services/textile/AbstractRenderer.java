package ish.oncourse.services.textile;

import ish.oncourse.util.ValidationErrors;

public abstract class AbstractRenderer implements IRenderer {
	
	protected IValidator validator;

	public String render(String tag, ValidationErrors errors) {
		validator.validate(tag, errors);
		return tag;
	}
	
}
