package ish.oncourse.services.textile;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationException;

public class TextileConverter implements ITextileConverter {

	private Map<TextileType, IRenderer> renderers = new HashMap<TextileType, IRenderer>();

	public String convert(String content) throws ValidationException {
		String regex = "[{]((block)|(course)|(tags)|(page)|(video)|(image))([^}]*)[}]";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);
		ValidationErrors errors = new ValidationErrors();

		while (matcher.find()) {
			String tag = matcher.group();
			IRenderer renderer = getRendererForTag(tag);
			if (renderer != null) {
				String replacement = renderer.render(tag, errors);
				if (!errors.hasFailures() && replacement != null) {
					content = content.replaceFirst(regex, replacement);
				}
			}
		}
		if (errors.hasFailures()) {
			throw new ValidationException(errors);
		}
		return content;
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
			return new ImageTextileRenderer();
		}
		return null;
	}

}
