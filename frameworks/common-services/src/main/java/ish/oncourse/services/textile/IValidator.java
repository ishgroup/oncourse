package ish.oncourse.services.textile;

import org.apache.cayenne.ObjectContext;

import ish.oncourse.model.College;
import ish.oncourse.util.ValidationErrors;

public interface IValidator {
	void validate(String tag, ValidationErrors errors, ObjectContext context, College currentCollege);
}
