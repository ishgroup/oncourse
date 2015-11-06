package ish.oncourse.ui.pages;

import ish.oncourse.components.ISHCommon;
import ish.oncourse.model.Course;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.html.IFacebookMetaProvider;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.search.SearchParams;
import ish.oncourse.services.search.SearchParamsParser;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.ui.utils.CourseItemModel;
import ish.oncourse.util.HTMLUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class CourseDetails extends ISHCommon {
	@Inject
	private ICourseService courseService;

	@Inject
	private ISearchService searchService;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ITagService tagService;

	@Inject
	private Request request;

	@Inject
	private IFacebookMetaProvider facebookMetaProvider;

	@Inject
	private ICourseClassService courseClassService;

	@Property
	private Course course;

	private SearchParams searchParams;


	void beginRender() {
		SearchParamsParser paramsParser = SearchParamsParser.valueOf(request, searchService, tagService, webSiteService.getTimezone());
		paramsParser.parse();
		searchParams = paramsParser.getSearchParams();
		course = (Course) request.getAttribute(Course.class.getSimpleName());
	}

	public CourseItemModel getCourseItemModel() {
		return CourseItemModel.valueOf(course, searchParams, courseService, courseClassService);
	}

	public String getCanonicalLinkPath() {
		return HTMLUtils.getCanonicalLinkPathFor(course, request);
	}

	public String getCanonicalRelativeLinkPath() {
		return HTMLUtils.getCanonicalRelativeLinkPath(course, request);
	}

	public String getCourseDetailsTitle() {
		if (course == null) {
			return "Course Not Found";
		}
		return course.getName();
	}

	public String getMetaDescription() {
		return facebookMetaProvider.getDescriptionContent(course);
	}
}
