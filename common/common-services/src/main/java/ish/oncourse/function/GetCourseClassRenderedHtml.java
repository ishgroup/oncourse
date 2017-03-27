package ish.oncourse.function;

import ish.math.Money;
import ish.oncourse.model.CourseClass;
import ish.oncourse.ui.pages.prerender.CourseClassItemContainer;
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
	private boolean allowByAplication;
	private Money feeOverride;

	private GetCourseClassRenderedHtml() {
	}

	public static GetCourseClassRenderedHtml valueOf(IPageRenderer pageRenderer, CourseClass courseClass, boolean linkToLocationsMap, boolean isList, boolean allowByAplication, Money feeOverride) {
		GetCourseClassRenderedHtml getCourseClassRenderedHtml = new GetCourseClassRenderedHtml();
		getCourseClassRenderedHtml.pageRenderer = pageRenderer;
		
		getCourseClassRenderedHtml.courseClass = courseClass;
		getCourseClassRenderedHtml.linkToLocationsMap = linkToLocationsMap;
		getCourseClassRenderedHtml.isList = isList;
		getCourseClassRenderedHtml.allowByAplication = allowByAplication;
		getCourseClassRenderedHtml.feeOverride = feeOverride;
		return getCourseClassRenderedHtml;
	}
	
	private void prepareParameters() {
		parameters.put(CourseClassItemContainer.COURSE_CLASS, courseClass);
		parameters.put(CourseClassItemContainer.LINK_TO_LOCATIONS_MAP, linkToLocationsMap);
		parameters.put(CourseClassItemContainer.IS_LIST, isList);
		parameters.put(CourseClassItemContainer.ALLOW_BY_APPLICATION, allowByAplication);
		parameters.put(CourseClassItemContainer.FEE_OVERRIDE, feeOverride);
	}
	
	public String get() {
		prepareParameters();
		
		return pageRenderer.renderPage(CONTAINER_NAME, parameters);
	}
}