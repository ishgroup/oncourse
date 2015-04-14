package ish.oncourse.ui.components;

import ish.oncourse.model.CourseClass;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import java.text.DecimalFormat;
import java.text.Format;

public class CourseClassPlacesAvailable {

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
}
