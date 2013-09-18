package ish.oncourse.services.textile.renderer;

import ish.oncourse.model.WebContent;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.BlockTextileAttributes;
import ish.oncourse.services.textile.validator.BlockTextileValidator;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.util.ValidationErrors;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private IWebContentService webBlockDataService;

	private ITextileConverter converter;

	public BlockTextileRenderer(IWebContentService webBlockDataService, ITextileConverter converter) {
		validator = new BlockTextileValidator(webBlockDataService);
		this.webBlockDataService = webBlockDataService;
		this.converter = converter;
	}

	@Override
	protected String internalRender(String tag) {
		WebContent webBlock = null;
		Map<String, String> tagParams = TextileUtil.getTagParams(tag,
				BlockTextileAttributes.getAttrValues());

		String name = tagParams.get(BlockTextileAttributes.BLOCK_PARAM_NAME.getValue());
		if (name != null) {
			webBlock = webBlockDataService.getWebContent(WebContent.NAME_PROPERTY, name);
		} else {
			webBlock = webBlockDataService.getWebContent(null, null);
		}

		if (webBlock != null) {
			String result = webBlock.getContent();
			if (result == null)
				result = FormatUtils.EMPTY_STRING;

			Pattern pattern = Pattern.compile(TextileUtil.TEXTILE_REGEXP, Pattern.DOTALL);
			Matcher matcher = pattern.matcher(result);
			if (matcher.find()) {
				ValidationErrors errors = new ValidationErrors();
				tag = converter.convertCustomTextile(result, errors);
			} else {
				tag = result;
			}
		} else {
			tag = null;
		}
		return tag;
	}
}
