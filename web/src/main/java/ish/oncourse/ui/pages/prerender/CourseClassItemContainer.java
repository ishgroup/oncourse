package ish.oncourse.ui.pages.prerender;

import ish.math.Money;
import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.model.CourseClass;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

public class CourseClassItemContainer extends ISHCommon {
	
	public static final String COURSE_CLASS = "courseClass";
	public static final String LINK_TO_LOCATIONS_MAP = "linkToLocationsMap";
	public static final String IS_LIST = "isList";
	public static final String ALLOW_BY_APPLICATION = "allowByApplication";
	public static final String FEE_OVERRIDE = "feeOverride";

	
	@Property
	private CourseClass courseClass;

	@Property
	private boolean linkToLocationsMap;

	@Property
	private boolean isList;

	@Property
	private boolean allowByAplication;

	@Property
	private Money feeOverride;

	@SetupRender
	public void beforeRender() {
		courseClass = (CourseClass) request.getAttribute(COURSE_CLASS);
		linkToLocationsMap = (Boolean) request.getAttribute(LINK_TO_LOCATIONS_MAP);
		isList = (Boolean) request.getAttribute(IS_LIST);
		allowByAplication = (Boolean) request.getAttribute(ALLOW_BY_APPLICATION);
		feeOverride = (Money) request.getAttribute(FEE_OVERRIDE);
	}
}
