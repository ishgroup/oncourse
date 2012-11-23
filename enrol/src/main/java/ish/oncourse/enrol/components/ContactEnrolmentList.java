package ish.oncourse.enrol.components;

import ish.oncourse.enrol.pages.EnrolCourses;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import java.util.List;

@Deprecated
public class ContactEnrolmentList {

    @Inject
    private Request request;

    private static final String SEPARATOR = "_";

    private static final String DIGIT_PATTERN = "(\\d)+";

    @SuppressWarnings("all")
	@Parameter
    @Property
    private List<CourseClass> classes;

    @SuppressWarnings("all")
    @Parameter
    @Property
    private int studentIndex;

    @SuppressWarnings("all")
    @Property
    private CourseClass courseClass;

    @SuppressWarnings("all")
    @Property
    private int courseClassIndex;

    @Property
    private int sIndex;

    @Property
    private int cCIndex;

    @InjectPage
    private EnrolCourses enrolCourses;

    public StreamResponse onActionFromTick() {
    	boolean isTimeouted = false;
        if (request.getSession(false) == null) {
        	isTimeouted = true;
        }
        if (!isTimeouted) {
        	String data = request.getParameter("data");
        	if (StringUtils.trimToNull(data) == null) {
        		isTimeouted = true;
        	} else {
        		String[] indexes = data.split(SEPARATOR);
        		if (indexes.length < 2) {
        			isTimeouted = true;
        		} else {
        			String sIndexStr = indexes[0];
        	        String cCIndexStr = indexes[1];
        	        if (sIndexStr.matches(DIGIT_PATTERN) && cCIndexStr.matches(DIGIT_PATTERN)) {
        	            sIndex = Integer.parseInt(sIndexStr);
        	            cCIndex = Integer.parseInt(cCIndexStr);
        	            setEnrolmentSelected(Boolean.parseBoolean(indexes[2]));
        	        }
        		}
        	}
        }
        return new TextStreamResponse("text/html", isTimeouted ? "session timeout" : "succeed");
    }
    
    /**
     * @see ish.oncourse.enrol.pages.EnrolCourses#handleUnexpectedException(Throwable)
     */
	Object onException(Throwable cause) {
		return enrolCourses.handleUnexpectedException(cause);
	}

    public void setEnrolmentSelected(boolean value) {
        if (value) {
            getEnrolment().setInvoiceLine(getInvoiceLine());
        } else {
            /**
             * The enrolment will be deleted on 'Make Payment' in EnrolmentPaymentEntry.getEnrolmentsToPersist()
             * Correspondent invoice and invoiceLine will be deleted in EnrolmentPaymentEntry.getInvoiceLinesToPersist()
             */
            getEnrolment().setInvoiceLine(null);
        }
    }

    public Enrolment getEnrolment() {
        return enrolCourses.getController().getModel().getEnrolments()[sIndex][cCIndex];
    }

    public InvoiceLine getInvoiceLine() {
        return enrolCourses.getController().getModel().getInvoiceLines()[sIndex][cCIndex];
    }

}
