package ish.oncourse.services.textile.validator;

import ish.oncourse.model.WebBlock;
import ish.oncourse.services.block.IWebBlockService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;

public class BlockTextileValidator implements IValidator {

	public void validate(String tag, ValidationErrors errors, Object dataService) {
		if (getWebBlockByTag(tag, errors, dataService) == null) {
			errors
					.addFailure("The block tag '"
							+ tag
							+ "' doesn't match nor {block name:\"name\"}, nor {block tag:\"tag\"}");
		}
	}

	public WebBlock getWebBlockByTag(String tag, ValidationErrors errors,
			Object dataService) {
		IWebBlockService webBlockDataService = (IWebBlockService) dataService;
		WebBlock result = null;
		if (tag.matches(TextileUtil.BLOCK_NAME_REGEXP)) {
			String name = TextileUtil.getValueInFirstQuots(tag);
			result = webBlockDataService.getWebBlock(WebBlock.NAME_PROPERTY,
					name);
			if (result == null) {
				errors.addFailure("There's no block with the name: " + name);
			}
		}
		return result;
	}

}
