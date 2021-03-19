package ish.oncourse.ui.components;

import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.ui.utils.CourseItemSkeletonModel;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class CourseClassItemPlaceholder extends ISHCommon {

	@Parameter
	@Property
	private CourseItemSkeletonModel.CourseClassProjection courseClass;

}
