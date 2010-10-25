package ish.oncourse.services.textile.renderer;

import ish.oncourse.model.Course;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.validator.CourseTextileValidator;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.ValidationErrors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Displays a single course, using either the defined template or the default
 * CourseListItem.
 * 
 * <pre>
 * Example: 
 * 
 * {course template:&quot;Course with image&quot; code:&quot;abc&quot; tag:&quot;tag name&quot; enrollable:&quot;true&quot; currentsearch:&quot;false&quot; } 
 * 
 * The parameters are as follows: 
 * 
 * template: the name of the Template used to display the course.
 * 
 * code: if specified, only the course with that code will be displayed.
 * Otherwise, a random course will be displayed. 
 * 
 * tag: if specified, the random choice is restricted to courses with this tag. 
 * 
 * enrollable: if true, the random choice is restricted to courses that have
 * classes that are not full and still enrollable.
 * 
 * currentsearch: if true, the random choice is restricted to courses in the
 * current advanced search results. If there is no current search, or there
 * are no advanced search parameters, this will be ignored.
 * </pre>
 */
// TODO deal with the template, tag and currentsSearch attributes (left
// not implemented)
public class CourseTextileRenderer extends AbstractRenderer {

	private ICourseService courseService;
	
	private IPageRenderer pageRenderer;
	
	public CourseTextileRenderer(ICourseService courseService, IPageRenderer pageRenderer) {
		this.courseService = courseService;
		this.pageRenderer = pageRenderer;
		validator = new CourseTextileValidator(courseService);
	}

	@Override
	public String render(String tag, ValidationErrors errors) {
		tag = super.render(tag, errors);
		if (!errors.hasFailures()) {
			
			Map<String, String> tagParams = TextileUtil.getTagParams(tag,
					TextileUtil.COURSE_PARAM_CODE,
					TextileUtil.COURSE_PARAM_ENROLLABLE);
			String code = tagParams.get(TextileUtil.COURSE_PARAM_CODE);
			String enrollable = tagParams
					.get(TextileUtil.COURSE_PARAM_ENROLLABLE);
			Course course = null;
			if (code != null) {
				course = courseService.getCourse(Course.CODE_PROPERTY, code);
			} else if (enrollable != null) {
				Boolean enrollableValue = Boolean.valueOf(enrollable);
				List<Course> courses = courseService
						.getCourses(enrollableValue);
				if (!courses.isEmpty()) {
					course = courses.get(0);
				}
			} else {
				course = courseService.getCourse(null, null);
			}
			if (course != null) {
				Map<String, Object> parameters=new HashMap<String, Object>();
				parameters.put(TextileUtil.TEXTILE_COURSE_PAGE_PARAM, course);
				tag = pageRenderer.renderPage(TextileUtil.TEXTILE_COURSE_PAGE, parameters);
			}
		}
		return tag;

	}
}
