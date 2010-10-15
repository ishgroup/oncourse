package ish.oncourse.services.textile.validator;

import ish.oncourse.model.WebContent;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;

import java.util.Map;

public class BlockTextileValidator implements IValidator {

	private IWebContentService webBlockDataService;

	public BlockTextileValidator(IWebContentService webBlockDataService) {
		this.webBlockDataService = webBlockDataService;
	}

	public void validate(String tag, ValidationErrors errors) {
		WebContent result = null;

		if (!tag.matches(TextileUtil.BLOCK_REGEXP)) {
			errors.addFailure("The tag: " + tag
					+ " doesn't match pattern {block name:\"My Block\"}");
		}
		TextileUtil.checkParamsUniquence(tag, errors, TextileUtil.PARAM_NAME);

		Map<String, String> tagParams = TextileUtil.getTagParams(tag,
				TextileUtil.PARAM_NAME);

		String name = tagParams.get(TextileUtil.PARAM_NAME);
		
		if (name != null) {
			result = webBlockDataService.getWebContent(
					WebContent.NAME_PROPERTY, name);
			if (result == null) {
				errors.addFailure("There's no block with the name: " + name);
			}
		}
	}

}
