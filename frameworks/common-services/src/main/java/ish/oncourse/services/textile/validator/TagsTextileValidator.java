package ish.oncourse.services.textile.validator;

import ish.oncourse.services.tag.ITagService;
import ish.oncourse.util.ValidationErrors;

public class TagsTextileValidator implements IValidator {

	private ITagService tagService;
	
	public TagsTextileValidator(ITagService tagService) {
		this.tagService = tagService;
	}

	public void validate(String tag, ValidationErrors errors) {
		// TODO Auto-generated method stub

	}

}
