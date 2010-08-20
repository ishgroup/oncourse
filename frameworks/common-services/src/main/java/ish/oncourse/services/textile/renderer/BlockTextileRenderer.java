package ish.oncourse.services.textile.renderer;

import ish.oncourse.model.WebBlock;
import ish.oncourse.services.block.IWebBlockService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.validator.BlockTextileValidator;
import ish.oncourse.util.ValidationErrors;

public class BlockTextileRenderer extends AbstractRenderer {

	private IWebBlockService webBlockDataService;

	public BlockTextileRenderer(IWebBlockService webBlockDataService) {
		validator = new BlockTextileValidator(webBlockDataService);
	}

	public String render(String tag, ValidationErrors errors) {
		tag = super.render(tag, errors);
		if (!errors.hasFailures()) {
			WebBlock webBlock = null;
			if (tag.matches(TextileUtil.BLOCK_NAME_REGEXP)) {
				String name = TextileUtil.getValueInFirstQuots(tag);
				webBlock = webBlockDataService.getWebBlock(
						WebBlock.NAME_PROPERTY, name);
			}

			tag = webBlock.getContent();
		}
		return tag;
	}
}
