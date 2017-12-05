package ish.oncourse.ui.components;

import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.courseclass.CheckClassAge;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.ui.utils.CourseContext;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.DecimalFormat;
import java.text.Format;

public class CourseClassPlacesAvailable extends ISHCommon {

	@Inject
	private PreferenceController preferenceController;

	private CourseContext context;
	
	private int placesAvailable;

	@Parameter
	@Property
	private CourseClass courseClass;

	private Format format = new DecimalFormat("#,###,##0");

	public Format getFormat() {
		return format;
	}
	
	@SetupRender
	public void beforeRender() {
		placesAvailable = courseClass.getAvailableEnrolmentPlaces();
	 	context = (CourseContext) request.getAttribute(CourseItem.COURSE_CONTEXT);
	}

	public boolean isHasCommonAvailable() {
		return placesAvailable > 5;
	}

	public boolean isHasManyPlaces() {
		return placesAvailable > 1;
	}

	public boolean hasAvailableEnrolmentPlaces() {
		return placesAvailable > 0 && (context != null || new CheckClassAge().courseClass(courseClass).classAge(preferenceController.getStopWebEnrolmentsAge()).check());
	}
}
