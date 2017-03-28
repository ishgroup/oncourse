package ish.oncourse.function;

import ish.math.Money;
import ish.oncourse.model.CourseClass;
import ish.oncourse.ui.components.CourseItem;
import ish.oncourse.ui.pages.prerender.CourseClassItemContainer;
import ish.oncourse.ui.utils.CourseContext;
import ish.oncourse.util.IPageRenderer;

import java.util.HashMap;
import java.util.Map;

public class GetCourseClassRenderedHtml implements IGet<String> {

	public static final String CONTAINER_NAME = "prerender/CourseClassItemContainer";

	private IPageRenderer pageRenderer;
	private Map<String, Object> parameters = new HashMap<>();

	private CourseClass courseClass;
	private boolean linkToLocationsMap;
	private boolean isList;
	private boolean allowByApplication;
	private Money feeOverride;

	private CourseContext courseContext;

	private GetCourseClassRenderedHtml() {
	}

	public static GetCourseClassRenderedHtml valueOf(IPageRenderer pageRenderer,
	                                                 CourseClass courseClass,
	                                                 boolean linkToLocationsMap,
	                                                 boolean isList,
	                                                 boolean allowByApplication,
	                                                 Money feeOverride,
	                                                 CourseContext courseContext) {
		GetCourseClassRenderedHtml getCourseClassRenderedHtml = new GetCourseClassRenderedHtml();
		getCourseClassRenderedHtml.pageRenderer = pageRenderer;

		getCourseClassRenderedHtml.courseClass = courseClass;
		getCourseClassRenderedHtml.linkToLocationsMap = linkToLocationsMap;
		getCourseClassRenderedHtml.isList = isList;
		getCourseClassRenderedHtml.allowByApplication = allowByApplication;
		getCourseClassRenderedHtml.feeOverride = feeOverride;

		getCourseClassRenderedHtml.courseContext = courseContext;

		return getCourseClassRenderedHtml;
	}

	private void prepareParameters() {
		parameters.put(CourseClassItemContainer.COURSE_CLASS, courseClass);
		parameters.put(CourseClassItemContainer.LINK_TO_LOCATIONS_MAP, linkToLocationsMap);
		parameters.put(CourseClassItemContainer.IS_LIST, isList);
		parameters.put(CourseClassItemContainer.ALLOW_BY_APPLICATION, allowByApplication);
		parameters.put(CourseClassItemContainer.FEE_OVERRIDE, feeOverride);

		parameters.put(CourseItem.COURSE_CONTEXT, courseContext);
	}

	public String get() {
		prepareParameters();

		return pageRenderer.renderPage(CONTAINER_NAME, parameters);
	}
}