package ish.oncourse.services.textile.validator;

import java.util.Map;

import ish.oncourse.model.WebBlock;
import ish.oncourse.services.block.IWebBlockService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;

public class BlockTextileValidator implements IValidator {

	private IWebBlockService webBlockDataService;

	public BlockTextileValidator(IWebBlockService webBlockDataService) {
		this.webBlockDataService = webBlockDataService;
	}

	public void validate(String tag, ValidationErrors errors) {
		WebBlock result = null;
		if (!tag.matches(TextileUtil.BLOCK_REGEXP)) {
			errors
					.addFailure("The tag: "
							+ tag
							+ " doesn't match pattern {block name:\"My Block\" tag:\"tag name\"}");
		}
		TextileUtil.checkParamsUniquence(tag, errors,TextileUtil.PARAM_NAME, TextileUtil.BLOCK_PARAM_TAG);

		Map<String, String> tagParams = TextileUtil.getTagParams(tag,
				TextileUtil.PARAM_NAME, TextileUtil.BLOCK_PARAM_TAG);

		String name = tagParams.get(TextileUtil.PARAM_NAME);
		String tagParam = tagParams.get(TextileUtil.BLOCK_PARAM_TAG);
		if (name != null) {
			result = webBlockDataService.getWebBlock(WebBlock.NAME_PROPERTY,
					name);
			if (result == null) {
				errors.addFailure("There's no block with the name: " + name);
			}
		} else {
			if (tagParam != null) {
				/*
				 * result =
				 * webBlockDataService.getWebBlock(WebBlock.TAG_PROPERTY,
				 * tagParam); if (result == null) {
				 * errors.addFailure("There's no block tagged with the tag: " +
				 * tagParam); }
				 */
			}
		}
	}

}
