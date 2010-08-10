package ish.oncourse.services.textile.renderer;

import ish.oncourse.model.WebBlock;
import ish.oncourse.model.WebTagged;
import ish.oncourse.services.block.IWebBlockService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.validator.BlockTextileValidator;
import ish.oncourse.util.ValidationErrors;

public class BlockTextileRenderer extends AbstractRenderer {

	public BlockTextileRenderer() {
		validator = new BlockTextileValidator();
	}

	public String render(String tag, ValidationErrors errors, Object dataService) {
		IWebBlockService webBlockDataService = (IWebBlockService) dataService;
		tag = super.render(tag, errors, webBlockDataService);
		if (!errors.hasFailures()) {
			WebBlock webBlock = null;
			if (tag.matches(TextileUtil.BLOCK_NAME_REGEXP)) {
				String name = TextileUtil.getValueInFirstQuots(tag);
				webBlock = webBlockDataService.getWebBlock(
						WebBlock.NAME_PROPERTY, name);
			} else if (tag.matches(TextileUtil.BLOCK_TAG_REGEXP)) {
				String tagName = TextileUtil.getValueInFirstQuots(tag);
				webBlock = webBlockDataService.getWebBlock(
						WebTagged.TAG_PROPERTY, tagName);
			}

			tag = webBlock.getContent();
		}
		return tag;
	}

}
