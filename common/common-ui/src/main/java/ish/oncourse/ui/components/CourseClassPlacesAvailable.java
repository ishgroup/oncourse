package ish.oncourse.ui.components;

import ish.oncourse.components.ISHCommon;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.courseclass.CheckClassAge;
import ish.oncourse.services.preference.PreferenceController;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.DecimalFormat;
import java.text.Format;

public class CourseClassPlacesAvailable extends ISHCommon {

	@Inject
	private PreferenceController preferenceController;

	@Parameter
	@Property
	private CourseClass courseClass;

	private Format format = new DecimalFormat("#,###,##0");

	public Format getFormat() {
		return format;
	}

	public boolean isHasCommonAvailable() {
		return courseClass.getAvailableEnrolmentPlaces() > 5;
	}

	public boolean isHasManyPlaces() {
		return courseClass.getAvailableEnrolmentPlaces() > 1;
	}

	public boolean hasAvailableEnrolmentPlaces() {
		return courseClass.getAvailableEnrolmentPlaces() > 0
				&& new CheckClassAge().courseClass(courseClass).classAge(preferenceController.getStopWebEnrolmentsAge()).check();
	}

}
