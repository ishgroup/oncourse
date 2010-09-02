package ish.oncourse.services.textile.validator;

import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.util.ValidationErrors;

public class PageTextileValidator implements IValidator {

	private IWebNodeService webNodeService;
	
	public PageTextileValidator(IWebNodeService webNodeService) {
		this.webNodeService = webNodeService;
	}

	public void validate(String tag, ValidationErrors errors) {
		// TODO Auto-generated method stub

	}

}
