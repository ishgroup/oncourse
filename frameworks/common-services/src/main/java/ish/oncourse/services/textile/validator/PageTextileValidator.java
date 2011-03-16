package ish.oncourse.services.textile.validator;

import java.util.List;
import java.util.Map;

import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.PageTextileAttributes;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationFailureType;

public class PageTextileValidator implements IValidator {

	private IWebNodeService webNodeService;

	public PageTextileValidator(IWebNodeService webNodeService) {
		this.webNodeService = webNodeService;
	}

	public void validate(String tag, ValidationErrors errors) {
		if (!tag.matches(TextileUtil.PAGE_REGEXP)) {
			errors.addFailure(getFormatErrorMessage(tag), ValidationFailureType.SYNTAX);
		}

		List<String> attrValues = PageTextileAttributes.getAttrValues();
		TextileUtil.checkParamsUniquence(tag, errors, attrValues);

		Map<String, String> tagParams = TextileUtil.getTagParams(tag, attrValues);
		String code = tagParams.get(PageTextileAttributes.PAGE_CODE_PARAM.getValue());
		WebNode node = null;
		if (code != null && code.matches("(\\d+)")) {
			Integer codeNum = Integer.valueOf(code);
			node = webNodeService.getNodeForNodeNumber(codeNum);
			if (node == null) {
				errors.addFailure(getPageNotFoundByCode(codeNum),
						ValidationFailureType.CONTENT_NOT_FOUND);
			}
		}
	}

	public String getFormatErrorMessage(String tag) {
		return "The page tag '" + tag + "' doesn't match {page code:\"number\"}";
	}

	public String getPageNotFoundByCode(int code) {
		return "There're no page with code '" + code;
	}

}