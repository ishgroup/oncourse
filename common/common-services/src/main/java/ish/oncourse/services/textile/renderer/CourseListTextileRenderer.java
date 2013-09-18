package ish.oncourse.services.textile.renderer;

import ish.oncourse.model.Course;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.CourseListSortValue;
import ish.oncourse.services.textile.attrs.CourseListTextileAttributes;
import ish.oncourse.services.textile.attrs.CourseStyle;
import ish.oncourse.services.textile.validator.CourseListTextileValidator;
import ish.oncourse.util.IPageRenderer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Displays the list of courses. common-ui/TextileCourseList.tml
 * <p/>
 * <pre>
 * Syntax:
 * {courses tag:"/tag" limit:"integer" sort:"date|alphabetical|availability" order:"asc|desc" style:"titles|details"
 *	limit - max amount of courses which will be shown.
 *		default - unlimited
 *	sort - what fields we use to sort this courses:
 *		alphabetical:  course.name.
 *		date:  course.startDate
 *		availability: course.availableEnrolmentPlaces
 *		default - alphabetical
 *	order - asc or desc ordering
 *		default - asc
 *	style - which predefined template will be used to show every course
 *		"titles" shows only name of course
 *		"details" shows full information about this course
 *		default - details
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
	protected String internalRender(String tag) {
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
		String style = tagParams.get(CourseListTextileAttributes.COURSE_LIST_PARAM_STYLE
				.getValue());

		Boolean isAscending = order == null || "asc".equalsIgnoreCase(order);
		Integer intLimit = limit == null ? null : Integer.valueOf(limit);
		CourseListSortValue sortOrder = CourseListSortValue.getByName(sort);
		if (sortOrder == null) {
			//if nothing specified use default
			sortOrder = CourseListSortValue.ALPHABETICAL;
		}
		List<Course> courses = courseService.getCourses(tagName, sortOrder, isAscending, intLimit);

		if (courses.isEmpty()) {
			tag = null;
		} else {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put(TextileUtil.TEXTILE_COURSE_LIST_PAGE_PARAM, courses);
			if (style != null)
				parameters.put(TextileUtil.TEXTILE_COURSE_STYLE_PARAM, CourseStyle.valueOf(style));
			tag = pageRenderer.renderPage(TextileUtil.TEXTILE_COURSE_LIST_PAGE, parameters);
		}
		return tag;
	}

}
