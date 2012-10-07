package ish.oncourse.enrol.components;

import ish.oncourse.enrol.pages.Checkout;
import ish.oncourse.enrol.utils.PurchaseController;
import ish.oncourse.enrol.utils.PurchaseController.Action;
import ish.oncourse.enrol.utils.PurchaseController.ActionParameter;
import ish.oncourse.enrol.utils.PurchaseModel;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import java.util.List;

public class CheckoutContactEnrolmentList {
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
    private Checkout checkoutPage;
    
    private PurchaseModel getModel() {
		return getController().getModel();
	}
	
	private PurchaseController getController() {
		return checkoutPage.getController();
	}
    
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
        	            boolean ticked = Boolean.parseBoolean(indexes[2]);
        	            ActionParameter actionParameter = new ActionParameter(ticked ? Action.ENABLE_ENROLMENT : Action.DISABLE_ENROLMENT);
        	    		actionParameter.setValue(getCurrentEnrolment());
        	    		getController().performAction(actionParameter);
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
		return checkoutPage.handleUnexpectedException(cause);
	}
	
	private Enrolment getCurrentEnrolment() {
		Contact currentStudent = getModel().getContacts().get(sIndex);
		CourseClass courseClass = getModel().getClasses().get(cCIndex);
		return getModel().getEnrolmentByCourseClass(currentStudent, courseClass);
	}
}
