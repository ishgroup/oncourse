package ish.oncourse.services.textile.validator;

import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.textile.TextileType;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.PageTextileAttributes;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationFailureType;

import java.util.Map;

public class PageTextileValidator extends AbstractTextileValidator {

	private IWebNodeService webNodeService;

	public PageTextileValidator(IWebNodeService webNodeService) {
		this.webNodeService = webNodeService;
	}

	@Override
	protected void initValidator() {
		textileType = TextileType.PAGE;
	}

	@Override
	protected void specificTextileValidate(String tag, ValidationErrors errors) {
		Map<String, String> tagParams = TextileUtil.getTagParams(tag, textileType.getAttributes());
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