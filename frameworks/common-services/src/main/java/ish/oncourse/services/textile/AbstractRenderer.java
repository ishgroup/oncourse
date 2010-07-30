package ish.oncourse.services.textile;

import org.apache.cayenne.ObjectContext;

import ish.oncourse.model.College;
import ish.oncourse.util.ValidationErrors;

public abstract class AbstractRenderer implements IRenderer {
	
	protected IValidator validator;

	public String render(String tag, ValidationErrors errors, ObjectContext context, College currentCollege) {
		validator.validate(tag, errors, context, currentCollege);
		return tag;
	}
	
}
