package ish.oncourse.services.textile.renderer;

import java.util.List;
import java.util.Map;

import ish.oncourse.model.Course;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.validator.CourseTextileValidator;
import ish.oncourse.util.GetStrResponseWrapper;
import ish.oncourse.util.IPageResponseRenderer;
import ish.oncourse.util.ValidationErrors;

import org.apache.tapestry5.internal.services.RequestPageCache;
import org.apache.tapestry5.internal.structure.Page;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;

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
	private RequestPageCache cache;
	private RequestGlobals requestGlobals;
	private IPageResponseRenderer pageResponseRenderer;

	public CourseTextileRenderer(ICourseService courseService,
			RequestPageCache cache, RequestGlobals requestGlobals,
			IPageResponseRenderer pageResponseRenderer) {
		this.courseService = courseService;
		this.cache = cache;
		this.requestGlobals = requestGlobals;
		this.pageResponseRenderer = pageResponseRenderer;
		validator = new CourseTextileValidator(courseService);
	}

	@Override
	public String render(String tag, ValidationErrors errors) {
		tag = super.render(tag, errors);
		if (!errors.hasFailures()) {
			Request request = requestGlobals.getRequest();
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
				request.setAttribute("course", course);

				Response response = requestGlobals.getResponse();
				GetStrResponseWrapper wrapper = new GetStrResponseWrapper(
						response);

				requestGlobals.storeRequestResponse(request, wrapper);
				Page page = cache.get("CourseDetails");

				try {
					pageResponseRenderer.renderPageResponse(page);
				} catch (Exception e) {
					e.printStackTrace();
				}

				requestGlobals.storeRequestResponse(request, response);

				tag = wrapper.getResponseString();
			}
		}
		return tag;

	}
}
