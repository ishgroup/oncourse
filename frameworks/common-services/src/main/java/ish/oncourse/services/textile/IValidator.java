package ish.oncourse.services.textile;

import ish.oncourse.util.ValidationErrors;


public interface IValidator {
	void validate(String tag, ValidationErrors errors,
			Object dataService);
}
