package ish.oncourse.services.textile.renderer;

import ish.oncourse.services.textile.validator.IValidator;
import ish.oncourse.util.ValidationErrors;

public abstract class AbstractRenderer implements IRenderer {
	
	protected IValidator validator;

	public String render(String tag, ValidationErrors errors) {
		validator.validate(tag, errors);
		return tag;
	}
}
