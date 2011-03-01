package ish.oncourse.services.textile.renderer;

import ish.oncourse.model.Course;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.CourseListSortValue;
import ish.oncourse.services.textile.attrs.CourseListTextileAttributes;
import ish.oncourse.services.textile.validator.CourseListTextileValidator;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.ValidationErrors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Displays the list of courses. common-ui/TextileCourseList.tml
 * 
 * <pre>
 * Example: 
 * 
 * {courses tag:"/tag" limit:"5" sort:"date" order:"desc"} 
 * 
 * The parameters are as follows: 
 * 
 * <br/>Tag - Optional. Defines the path to a tag.
 * <br/>Limit - specifies the number of the courses to display.
 * <br/>Sort - [date, alphabetical, availability] Optional. Defines the
 * sort criteria. Default behaviour is alphabetical.
 * <br/>Order - [asc, desc] Optional. Default is descending.
 * </pre>
 */
public class CourseListTextileRenderer extends AbstractRenderer {

	private ICourseService courseService;

	private IPageRenderer pageRenderer;

	public CourseListTextileRenderer(ICourseService courseService, IPageRenderer pageRenderer,
			ITagService tagService) {
		this.courseService = courseService;
		this.pageRenderer = pageRenderer;
		validator = new CourseListTextileValidator(tagService);
	}

	@Override
	public String render(String tag, ValidationErrors errors) {
		tag = super.render(tag, errors);
		if (!errors.hasFailures()) {

			Map<String, String> tagParams = TextileUtil.getTagParams(tag,
					CourseListTextileAttributes.getAttrValues());
			String limit = tagParams.get(CourseListTextileAttributes.COURSE_LIST_PARAM_LIMIT
					.getValue());
			String sort = tagParams.get(CourseListTextileAttributes.COURSE_LIST_PARAM_SORT
					.getValue());
			String order = tagParams.get(CourseListTextileAttributes.COURSE_LIST_PARAM_ORDER
					.getValue());
			String tagName = tagParams.get(CourseListTextileAttributes.COURSE_LIST_PARAM_TAG
					.getValue());
			tagName = tagName != null ? tagName.substring(tagName.lastIndexOf("/") + 1) : null;

			List<Course> courses = courseService.getCourses(tagName, CourseListSortValue
					.getByName(sort), Boolean.valueOf(order),
					limit == null ? null : Integer.valueOf(limit));

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put(TextileUtil.TEXTILE_COURSE_LIST_PAGE_PARAM, courses);
			tag = pageRenderer.renderPage(TextileUtil.TEXTILE_COURSE_LIST_PAGE, parameters);

		}
		return tag;

	}

}
