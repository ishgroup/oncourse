package ish.oncourse.services.textile.courseList;

import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.renderer.AbstractRenderer;
import ish.oncourse.util.IPageRenderer;

import java.util.HashMap;
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
 *	showTags: - if the parameter is true we should show tags. current implementation shows only the first layer of tags.
 *				defaul - false
 */
public class CourseListTextileRenderer extends AbstractRenderer {

	private ICourseService courseService;
	private IPageRenderer pageRenderer;
	private ITagService tagService;

	public CourseListTextileRenderer(ICourseService courseService, IPageRenderer pageRenderer,
									 ITagService tagService) {
		this.courseService = courseService;
		this.pageRenderer = pageRenderer;
		this.tagService = tagService;
		validator = new CourseListTextileValidator(tagService);
	}

	@Override
	protected String internalRender(String tag) {
		Map<String, String> params = TextileUtil.getTagParams(tag,
				TextileAttrs.getAttrValues());
		TagParams tagParams = new TagParams();
		tagParams.setTagParams(params);
		tagParams.setTagService(tagService);
		tagParams.parse();

		PageModel pageModel = new PageModel();
		pageModel.setTagParams(tagParams);
		pageModel.setCourseService(courseService);
		pageModel.setTagService(tagService);
		pageModel.init();

		if (pageModel.isEmpty())
			return null;
		Map<String,Object> pageParams = new HashMap<>();
		pageParams.put(TextileUtil.TEXTILE_COURSELIST_MODEL_PARAM, pageModel);

		tag = pageRenderer.renderPage(TextileUtil.TEXTILE_COURSE_LIST_PAGE, pageParams);
		return tag;
	}
}
