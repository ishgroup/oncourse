package ish.oncourse.services.textile.validator;

import ish.oncourse.model.WebContent;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;

import java.util.Map;

public class BlockTextileValidator implements IValidator {

	private IWebContentService webContentService;

	public BlockTextileValidator(IWebContentService webContentService) {
		this.webContentService = webContentService;
	}

	public void validate(String tag, ValidationErrors errors) {
		WebContent result = null;

		if (!tag.matches(TextileUtil.BLOCK_REGEXP)) {
			errors.addFailure(getFormatErrorMessage(tag));
		}
		TextileUtil.checkParamsUniquence(tag, errors, TextileUtil.PARAM_NAME);

		Map<String, String> tagParams = TextileUtil.getTagParams(tag,
				TextileUtil.PARAM_NAME);

		String name = tagParams.get(TextileUtil.PARAM_NAME);
		
		if (name != null) {
			result = webContentService.getWebContent(
					WebContent.NAME_PROPERTY, name);
			if (result == null) {
				errors.addFailure(getBlockNotFoundErrorMessage(name));
			}
		}
	}

	/**
	 * @param name
	 * @return
	 */
	public String getBlockNotFoundErrorMessage(String name) {
		return "There's no block with the name: " + name;
	}

	/**
	 * @param tag
	 * @return
	 */
	public String getFormatErrorMessage(String tag) {
		return "The tag: " + tag
				+ " doesn't match pattern {block name:\"My Block\"}";
	}

}
