package ish.oncourse.enrol.components.checkout.concession;

import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

import java.text.DateFormat;

public class ConcessionList {

	@Parameter (required = true)
	@Property
	private Student student;

	@Property
	private StudentConcession concession;

	@Property
	private DateFormat dateFormat;


	@SetupRender
	void beforeRender() {
		dateFormat = FormatUtils.getDateFormat(student.getCollege().getTimeZone());
	}

}
