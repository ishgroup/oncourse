package ish.oncourse.services.textile.renderer;

import java.util.Map;

import ish.oncourse.model.WebBlock;
import ish.oncourse.services.block.IWebBlockService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.validator.BlockTextileValidator;
import ish.oncourse.util.ValidationErrors;
/**
 * Displays content from a single WebBlock, either by name or randomly from a
 * tag
 * 
 * <pre>
 * Example: 
 * 
 * {block name:&quot;My Block&quot; tag:&quot;tag name&quot; } 
 * 
 * The parameters are as follows: 
 * 
 * name: the name of the WebBlock to display.
 * Otherwise, a random WebBlock will be displayed. 
 * 
 * tag: if specified, the random choice is restricted to WebBlocks with this tag.
 * 
 * </pre>
 */
public class BlockTextileRenderer extends AbstractRenderer {

	private IWebBlockService webBlockDataService;

	public BlockTextileRenderer(IWebBlockService webBlockDataService) {
		validator = new BlockTextileValidator(webBlockDataService);
		this.webBlockDataService = webBlockDataService;
	}

	public String render(String tag, ValidationErrors errors) {
		tag = super.render(tag, errors);
		if (!errors.hasFailures()) {
			WebBlock webBlock = null;
			Map<String, String> tagParams = TextileUtil.getTagParams(tag,
					TextileUtil.PARAM_NAME, TextileUtil.PARAM_TAG);

			String name = tagParams.get(TextileUtil.PARAM_NAME);
			String tagParam = tagParams.get(TextileUtil.PARAM_TAG);
			if (name != null) {
				webBlock = webBlockDataService.getWebBlock(WebBlock.NAME_PROPERTY,
						name);
			} else {
				if (tagParam != null) {
					/*
					 * webBlock =
					 * webBlockDataService.getWebBlock(WebBlock.TAG_PROPERTY,
					 * tagParam); 
					 */
				}else{
					webBlock = webBlockDataService.getWebBlock(null,null);
				}
			}

			tag = webBlock.getContent();
		}
		return tag;
	}
}
