package ish.oncourse.services.textile;

import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.block.IWebBlockService;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.textile.renderer.BlockTextileRenderer;
import ish.oncourse.services.textile.renderer.CourseTextileRenderer;
import ish.oncourse.services.textile.renderer.IRenderer;
import ish.oncourse.services.textile.renderer.ImageTextileRenderer;
import ish.oncourse.services.textile.renderer.VideoTextileRenderer;
import ish.oncourse.util.IPageResponseRenderer;
import ish.oncourse.util.ValidationErrors;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tapestry5.internal.services.RequestPageCache;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;

public class TextileConverter implements ITextileConverter {

	@Inject
	private IBinaryDataService binaryDataService;

	@Inject
	private IWebBlockService webBlockService;
	
	@Inject
	private ICourseService courseService;
	
	@Inject
	private RequestPageCache cache;
	
	@Inject
	private RequestGlobals requestGlobals;
	
	@Inject
	private IPageResponseRenderer pageResponseRenderer;
	
	private Map<TextileType, IRenderer> renderers = new HashMap<TextileType, IRenderer>();

	public String convert(String content, ValidationErrors errors) {
		Pattern pattern = Pattern.compile(TextileUtil.TEXTILE_REGEXP);
		Matcher matcher = pattern.matcher(content);
		String result = content;

		while (matcher.find()) {
			String tag = matcher.group();
			IRenderer renderer = getRendererForTag(tag);
			if (renderer != null) {
				String replacement = renderer.render(tag, errors);
				if (!errors.hasFailures() && replacement != null) {
					result = result.replace(tag, replacement);
				}
			}
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
			return new ImageTextileRenderer(binaryDataService);
		case BLOCK:
			return new BlockTextileRenderer(webBlockService, this);
		case VIDEO:
			return new VideoTextileRenderer();
		case COURSE:
			return new CourseTextileRenderer(courseService, cache, requestGlobals,  pageResponseRenderer);
		}
		return null;
	}

}
