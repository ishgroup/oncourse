package ish.oncourse.services;

import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.converter.CoreConverter;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.ConvertCoreTextile;
import ish.oncourse.services.textile.TextileType;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.courseList.CourseListTextileRenderer;
import ish.oncourse.services.textile.renderer.*;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationFailureType;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RichtextConverter implements IRichtextConverter {

    private final static Logger logger = LogManager.getLogger();

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
    private ISearchService searchService;

    @Inject
    private ICayenneService cayenneService;

    public RichtextConverter() {
    }

    /**
     * This constructor is used only for test
     */
    public RichtextConverter(IBinaryDataService binaryDataService, IWebContentService webContentService,
                     ICourseService courseService, IPageRenderer pageRenderer, IWebNodeService webNodeService,
                     ITagService tagService) {
        this.binaryDataService = binaryDataService;
        this.webContentService = webContentService;
        this.courseService = courseService;
        this.pageRenderer = pageRenderer;
        this.webNodeService = webNodeService;
        this.tagService = tagService;
    }

    public String convertCoreText(String content) {
        return CoreConverter.convert(content);
    }

    public String convertCustomText(String content, ValidationErrors errors) {
        content = StringUtils.trimToEmpty(content);

        Pattern pattern = Pattern.compile(TextileUtil.TEXTILE_REGEXP, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        String result = StringUtils.EMPTY;
        while (matcher.find()) {
            String tag = matcher.group();
            int startTag = content.indexOf(tag);
            result += content.substring(0, startTag);
            content = content.substring(startTag + tag.length());
            String replacement = null;
            try {
                IRenderer renderer = getRendererForTag(tag);
                replacement = renderer.render(tag);
                errors.appendErrors(renderer.getErrors());
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
                errors.addFailure(e, ValidationFailureType.SYNTAX);
            }
            result += replacement;
        }
        result += content;
        result = ConvertCoreTextile.clearGenerated(result);
        if (errors.hasFailures()) {
            logger.debug(errors);
        }
        return result;
    }


    private IRenderer getRendererForTag(String tag) {
        for (TextileType type : TextileType.BASE_TYPES) {
            Pattern pattern = Pattern.compile(type.getRegexp(), Pattern.DOTALL);
            Matcher matcher = pattern.matcher(tag);
            if (matcher.find()) {
                return createRendererForType(type);
            }
        }
        return null;
    }

    /**
     * @param type
     * @return
     */
    private IRenderer createRendererForType(TextileType type) {
        switch (type) {
            case IMAGE:
                return new ImageTextileRenderer(binaryDataService, pageRenderer);
            case BLOCK:
                return new BlockTextileRenderer(webContentService, this);
            case VIDEO:
                return new VideoTextileRenderer(pageRenderer);
            case COURSE:
                return new CourseTextileRenderer(courseService, pageRenderer, tagService);
            case COURSE_LIST:
                return new CourseListTextileRenderer(courseService, pageRenderer, tagService, searchService);
            case PAGE:
                return new PageTextileRenderer(webNodeService, pageRenderer);
            case TAGS:
                return new TagsTextileRenderer(tagService, pageRenderer, this);
            case FORM:
                return new FormTextileRenderer(pageRenderer);
            case ATTACHMENT:
                return new AttachmentTextileRenderer(binaryDataService, pageRenderer);
            case LOCATION:
                return new LocationTextileRenderer(pageRenderer, cayenneService);
            case TUTORS:
                return new TutorsTextileRenderer(pageRenderer);
            case DURATION:
                return new DurationTextileRenderer(pageRenderer);
            default:
                throw new IllegalArgumentException(String.format("Type $s is not supported", type));
        }

    }

}
