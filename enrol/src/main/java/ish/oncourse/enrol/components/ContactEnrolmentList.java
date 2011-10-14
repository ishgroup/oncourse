package ish.oncourse.enrol.components;

import ish.oncourse.enrol.pages.EnrolCourses;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;

import java.util.List;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

public class ContactEnrolmentList {

	@Inject
	private Request request;

	private static final String SEPARATOR = "_";

	private static final String DIGIT_PATTERN = "(\\d)+";

	@Parameter
	@Property
	private List<CourseClass> classes;

	@Parameter
	@Property
	private int studentIndex;

	@Property
	private CourseClass courseClass;

	@Property
	private int courseClassIndex;

	@Property
	private int sIndex;

	@Property
	private int cCIndex;

	@InjectPage
	private EnrolCourses enrolCourses;

	public StreamResponse onActionFromTick() {
		if(request.getSession(false)==null){
			return new TextStreamResponse("text/html", "session timeout");
		}
		String data = request.getParameter("data");
		String[] indexes = data.split(SEPARATOR);
		String sIndexStr = indexes[0];
		String cCIndexStr = indexes[1];
		if (sIndexStr.matches(DIGIT_PATTERN) && cCIndexStr.matches(DIGIT_PATTERN)) {
			sIndex = Integer.parseInt(sIndexStr);
			cCIndex = Integer.parseInt(cCIndexStr);
			setEnrolmentSelected(Boolean.parseBoolean(indexes[2]));
		}
		
		return new TextStreamResponse("text/html", "succeed");
	}

	public void setEnrolmentSelected(boolean value) {

		if (value) {
			getEnrolment().setInvoiceLine(getInvoiceLine());
		} else {
			getEnrolment().setInvoiceLine(null);
		}

	}

	public Enrolment getEnrolment() {
		return enrolCourses.getEnrolments()[sIndex][cCIndex];
	}

	public InvoiceLine getInvoiceLine() {
		return enrolCourses.getInvoiceLines()[sIndex][cCIndex];
	}

}
