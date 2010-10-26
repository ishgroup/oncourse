package ish.oncourse.services.textile.validator;

import java.util.Map;

import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;

public class PageTextileValidator implements IValidator {

	private IWebNodeService webNodeService;

	public PageTextileValidator(IWebNodeService webNodeService) {
		this.webNodeService = webNodeService;
	}

	public void validate(String tag, ValidationErrors errors) {
		if (!tag.matches(TextileUtil.PAGE_REGEXP)) {
			errors.addFailure(getFormatErrorMessage(tag));
		}

		TextileUtil.checkParamsUniquence(tag, errors,
				TextileUtil.PAGE_CODE_PARAM);

		Map<String, String> tagParams = TextileUtil.getTagParams(tag,
				TextileUtil.PAGE_CODE_PARAM);
		String code = tagParams.get(TextileUtil.PAGE_CODE_PARAM);
		WebNode node = null;
		if (code != null && code.matches("(\\d+)")) {
			node = webNodeService.getNode(WebNode.NODE_NUMBER_PROPERTY, Integer
					.valueOf(code));
			if (node == null) {
				errors.addFailure("There're no page with code '" + code);
			}
		}
	}

	public String getFormatErrorMessage(String tag) {
		return "The page tag '" + tag
				+ "' doesn't match {page code:\"number\"}";
	}

}
