package ish.oncourse.services.textile.renderer;

import ish.oncourse.model.Course;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.CourseTextileAttributes;
import ish.oncourse.services.textile.validator.CourseTextileValidator;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.ValidationErrors;

import java.util.HashMap;
import java.util.Map;

/**
 * Displays a single course. common-ui/TextileCourse.tml
 * 
 * <pre>
 * Example: 
 * 
 * {course code:"cmp21" tag:"/subjects/arts" showclasses:"true"} 
 * 
 * The parameters are as follows: 
 * 
 * <br/>Tag - Optional. Defines a path to a tag.
 * <br/>Code - Optional. Specifies a particular course code to display. If
 * this option is not defined, a random course will be shown.
 * <br/>Showclasses - [true, false] Optional. A short list of classes
 * available for this course will also be displayed.
 * </pre>
 */
public class CourseTextileRenderer extends AbstractRenderer {

	private ICourseService courseService;

	private IPageRenderer pageRenderer;

	public CourseTextileRenderer(ICourseService courseService, IPageRenderer pageRenderer,
			ITagService tagService) {
		this.courseService = courseService;
		this.pageRenderer = pageRenderer;
		validator = new CourseTextileValidator(courseService, tagService);
	}

	@Override
	public String render(String tag, ValidationErrors errors) {
		tag = super.render(tag, errors);
		if (!errors.hasFailures()) {

			Map<String, String> tagParams = TextileUtil.getTagParams(tag,
					CourseTextileAttributes.getAttrValues());
			String code = tagParams.get(CourseTextileAttributes.COURSE_PARAM_CODE.getValue());
			String showClasses = tagParams.get(CourseTextileAttributes.COURSE_PARAM_SHOW_CLASSES
					.getValue());
			String tagName = tagParams.get(CourseTextileAttributes.COURSE_PARAM_TAG.getValue());
			Course course = null;
			if (code != null) {
				course = courseService.getCourse(Course.CODE_PROPERTY, code);
			} else {
				course = courseService.getCourse(tagName);
			}
			if (course != null) {
				Map<String, Object> parameters = new HashMap<>();
				parameters.put(TextileUtil.TEXTILE_COURSE_PAGE_PARAM, course);
				parameters.put(TextileUtil.TEXTILE_COURSE_SHOW_CLASSES_PARAM, showClasses);
				tag = pageRenderer.renderPage(TextileUtil.TEXTILE_COURSE_PAGE, parameters);
			}
		}
		return tag;

	}
}
