package ish.oncourse.ui.pages;

import ish.oncourse.model.Course;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.html.IFacebookMetaProvider;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.search.SearchParamsParser;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.solr.query.SearchParams;
import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.ui.utils.CourseItemModel;
import ish.oncourse.util.HTMLUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

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
	private IFacebookMetaProvider facebookMetaProvider;

	@Inject
	private ICourseClassService courseClassService;

	@Property
	private Course course;

	private SearchParams searchParams;

	private CourseItemModel courseItemModel;


	void beginRender() {
		SearchParamsParser paramsParser = SearchParamsParser.valueOf(request, webSiteService.getCurrentCollege(),
				searchService, tagService, webSiteService.getTimezone());
		paramsParser.parse();
		searchParams = paramsParser.getSearchParams();
		course = (Course) request.getAttribute(Course.class.getSimpleName());
		courseItemModel = CourseItemModel.valueOf(course, searchParams);
	}

	public CourseItemModel getCourseItemModel() {
		return courseItemModel;
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
