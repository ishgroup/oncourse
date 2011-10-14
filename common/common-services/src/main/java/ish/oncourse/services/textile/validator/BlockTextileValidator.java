package ish.oncourse.services.textile.validator;

import ish.oncourse.model.WebContent;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.textile.TextileType;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.BlockTextileAttributes;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationFailureType;

import java.util.Map;

public class BlockTextileValidator extends AbstractTextileValidator {

	private IWebContentService webContentService;

	public BlockTextileValidator(IWebContentService webContentService) {
		this.webContentService = webContentService;
	}

	@Override
	protected void initValidator() {
		textileType = TextileType.BLOCK;
	}

	@Override
	protected void specificTextileValidate(String tag, ValidationErrors errors) {
		WebContent result = null;
		Map<String, String> tagParams = TextileUtil.getTagParams(tag, textileType.getAttributes());

		String name = tagParams.get(BlockTextileAttributes.BLOCK_PARAM_NAME.getValue());

		if (name != null) {
			result = webContentService.getWebContent(WebContent.NAME_PROPERTY, name);
			if (result == null) {
				errors.addFailure(getBlockNotFoundErrorMessage(name),
						ValidationFailureType.CONTENT_NOT_FOUND);
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
		return "The rich text: " + tag + " doesn't match pattern {block name:\"My Block\"}";
	}

}
