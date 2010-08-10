package ish.oncourse.services.textile;

import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.block.IWebBlockService;
import ish.oncourse.services.textile.renderer.BlockTextileRenderer;
import ish.oncourse.services.textile.renderer.IRenderer;
import ish.oncourse.services.textile.renderer.ImageTextileRenderer;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tapestry5.ioc.annotations.Inject;

public class TextileConverter implements ITextileConverter {

	@Inject
	private IBinaryDataService binaryDataService;

	@Inject
	private IWebBlockService webBlockService;

	private Object dataService;

	private Map<TextileType, IRenderer> renderers = new HashMap<TextileType, IRenderer>();

	public String convert(String content) throws ValidationException {
		String regex = "[{]((block)|(course)|(tags)|(page)|(video)|(image))([^}]*)[}]";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);
		String result = content;
		ValidationErrors errors = new ValidationErrors();

		while (matcher.find()) {
			String tag = matcher.group();
			IRenderer renderer = getRendererForTag(tag);
			if (renderer != null) {
				String replacement = renderer.render(tag, errors, dataService);
				if (!errors.hasFailures() && replacement != null) {
					result = result.replace(tag, replacement);
				}
			}
		}
		if (errors.hasFailures()) {
			throw new ValidationException(errors);
		}
		return result;
	}

	private IRenderer getRendererForTag(String tag) {
		IRenderer renderer = null;
		for (TextileType type : TextileType.values()) {
			if (tag.matches(type.getRegexp())) {
				renderer = renderers.get(type);
				if (renderer == null) {
					renderer = createRendererForType(type);
					renderers.put(type, renderer);
				}
			}
		}
		return renderer;
	}

	/**
	 * @param type
	 * @return
	 */
	private IRenderer createRendererForType(TextileType type) {
		switch (type) {
		case IMAGE:
			dataService = binaryDataService;
			return new ImageTextileRenderer();
		case BLOCK:
			dataService = webBlockService;
			return new BlockTextileRenderer();
		}
		return null;
	}

}
