package ish.oncourse.enrol.components.checkout;

import ish.oncourse.model.Enrolment;
import ish.oncourse.util.FormatUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

import java.text.DateFormat;


public class EnrolmentItem {
	static final Logger LOGGER = Logger.getLogger(EnrolmentItem.class);

	@Property
	@Parameter(required = true)
	private Enrolment enrolment;

	@Property
	private DateFormat dateFormat;
	
	@SetupRender
	void beforeRender() {
		dateFormat = FormatUtils.getDateFormat("EEE d MMM yy h:mm a", enrolment.getCourseClass().getTimeZone());
	}
}
