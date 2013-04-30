package ish.oncourse.services.textile;

import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.filestorage.IFileStorageAssetService;
import ish.oncourse.services.html.IPlainTextExtractor;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.renderer.*;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.ValidationErrors;
import net.java.textilej.parser.MarkupParser;
import net.java.textilej.parser.builder.HtmlDocumentBuilder;
import net.java.textilej.parser.markup.textile.TextileDialect;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextileConverter implements ITextileConverter {

	private final static Logger LOGGER = Logger.getLogger(TextileConverter.class);

	@Inject
	private IBinaryDataService binaryDataService;

	@Inject
	private IWebContentService webContentService;

	@Inject
	private ICourseService courseService;

	@Inject
	private IWebNodeService webNodeService;

	@Inject
	private IPageRenderer pageRenderer;

	@Inject
	private ITagService tagService;

    @Inject
    private IFileStorageAssetService fileStorageAssetService;

	private Map<TextileType, IRenderer> renderers = new HashMap<>();

	@SuppressWarnings("all")
	@Inject
	private IPlainTextExtractor extractor;

	public TextileConverter() {
	}

	public TextileConverter(IPlainTextExtractor extractor) {
		this.extractor = extractor;
	}

    /**
     *This constructor is used only for test
     */
	TextileConverter(IBinaryDataService binaryDataService, IWebContentService webContentService,
			ICourseService courseService, IPageRenderer pageRenderer, IWebNodeService webNodeService,
			ITagService tagService, IFileStorageAssetService fileStorageAssetService) {
		this.binaryDataService = binaryDataService;
		this.webContentService = webContentService;
		this.courseService = courseService;
		this.pageRenderer = pageRenderer;
		this.webNodeService = webNodeService;
		this.tagService = tagService;
        this.fileStorageAssetService = fileStorageAssetService;
	}

	public String convertCoreTextile(String content) {
		if (content == null) {
			return null;
		}

		// commented as seems to be useless(brake the textile enclosed by html
		// tag) - uncomment when this willl be solved and the extra <br> will
		// spoil the life
		// content = extractor.compactHtmlTags(content);

		StringWriter writer = new StringWriter();

		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
		// avoid the <html> and <body> tags
		builder.setEmitAsDocument(false);

		TextileDialect textileDialect = new TextileDialect();

		MarkupParser parser = new MarkupParser(textileDialect);

		parser.setBuilder(builder);
		parser.parse(content, false);
		String result = writer.toString();
		result = clearGenerated(result);
		return result;
	}

	public String convertCustomTextile(String content, ValidationErrors errors) {
		if (content == null) {
			content = "";
		}
		Pattern pattern = Pattern.compile(TextileUtil.TEXTILE_REGEXP, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(content);
		String result = "";
		ValidationErrors tempErrors = new ValidationErrors();
		while (matcher.find()) {
			String tag = matcher.group();
			int startTag = content.indexOf(tag);
			result += content.substring(0, startTag);
			content = content.substring(startTag + tag.length());
			IRenderer renderer = getRendererForTag(tag);
			if (renderer != null) {
				String replacement = renderer.render(tag, tempErrors);
				// TODO remove the check for renderer when the validation of
				// {form} is needed, now we just pass all the text
				if (!(renderer instanceof FormTextileRenderer)) {
					if (tempErrors.hasSyntaxFailures()) {
						replacement = TextileUtil.getReplacementForSyntaxErrorTag(tag);
					} else if (tempErrors.hasContentNotFoundFailures() || replacement == null) {
						replacement = "<div></div>";
					}
				}
				result += replacement;
			}
			errors.appendErrors(tempErrors);
			tempErrors.clear();
		}
		result += content;
		result = clearGenerated(result);
		if (errors.hasFailures()) {
			LOGGER.debug(errors.toString());
		}
		return result;
	}

	private String clearGenerated(String result) {
		if (result.startsWith("<p>") && result.endsWith("</p>")) {
			String cutted = result.substring(3);
			if (!cutted.contains("<p>") || cutted.contains("<p>") && cutted.indexOf("</p>") > cutted.indexOf("<p>")) {
				result = cutted.substring(0, cutted.lastIndexOf("</p>"));
			}
		}
		return StringEscapeUtils.unescapeHtml(result).replaceAll("(&amp;nbsp;)", " ");
	}

	private IRenderer getRendererForTag(String tag) {
		IRenderer renderer = null;
		for (TextileType type : TextileType.BASE_TYPES) {
			Pattern pattern = Pattern.compile(type.getRegexp(), Pattern.DOTALL);
			Matcher matcher = pattern.matcher(tag);
			if (matcher.find()) {
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
			return new ImageTextileRenderer(binaryDataService, fileStorageAssetService, pageRenderer);
		case BLOCK:
			return new BlockTextileRenderer(webContentService, this);
		case VIDEO:
			return new VideoTextileRenderer(pageRenderer);
		case COURSE:
			return new CourseTextileRenderer(courseService, pageRenderer, tagService);
		case COURSE_LIST:
			return new CourseListTextileRenderer(courseService, pageRenderer, tagService);
		case PAGE:
			return new PageTextileRenderer(webNodeService, pageRenderer);
		case TAGS:
			return new TagsTextileRenderer(tagService, pageRenderer, this);
		case FORM:
			return new FormTextileRenderer(pageRenderer);
		case ATTACHMENT:
			return new AttachmentTextileRenderer(binaryDataService, fileStorageAssetService, pageRenderer);
		}
		return null;
	}

}
