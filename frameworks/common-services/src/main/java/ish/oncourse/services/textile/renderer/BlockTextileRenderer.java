package ish.oncourse.services.textile.renderer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ish.oncourse.model.WebBlock;
import ish.oncourse.services.block.IWebBlockService;
import ish.oncourse.services.textile.ITextileConverter;
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
 * {block name:&quot;My Block&quot;} 
 * 
 * The parameters are as follows: 
 * 
 * name: the name of the WebBlock to display.
 * Otherwise, a random WebBlock will be displayed.
 * 
 * </pre>
 */
public class BlockTextileRenderer extends AbstractRenderer {

	private IWebBlockService webBlockDataService;

	private ITextileConverter converter;

	public BlockTextileRenderer(IWebBlockService webBlockDataService,
			ITextileConverter converter) {
		validator = new BlockTextileValidator(webBlockDataService);
		this.webBlockDataService = webBlockDataService;
		this.converter = converter;
	}

	public String render(String tag, ValidationErrors errors) {
		tag = super.render(tag, errors);
		if (!errors.hasFailures()) {
			WebBlock webBlock = null;
			Map<String, String> tagParams = TextileUtil.getTagParams(tag,
					TextileUtil.PARAM_NAME);

			String name = tagParams.get(TextileUtil.PARAM_NAME);
			if (name != null) {
				webBlock = webBlockDataService.getWebBlock(
						WebBlock.NAME_PROPERTY, name);
			} else {
				webBlock = webBlockDataService.getWebBlock(null, null);
			}

			if (webBlock != null) {
				String result = webBlock.getContent();

				Pattern pattern = Pattern.compile(TextileUtil.TEXTILE_REGEXP);
				Matcher matcher = pattern.matcher(result);
				if (matcher.find() && !errors.hasFailures()) {
					tag = converter.convert(result, errors);
				} else {
					tag = result;
				}
			} else {
				tag = "";
			}
		}
		return tag;
	}
}
