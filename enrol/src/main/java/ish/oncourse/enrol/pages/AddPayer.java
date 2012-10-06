package ish.oncourse.enrol.pages;

import ish.oncourse.enrol.services.student.IStudentService;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class AddPayer {

	@Inject
    private Request request;
	
	@Inject
    private IStudentService studentService;
		
	public boolean isPayerSelected() {
		return !studentService.getStudentsFromShortList().isEmpty();
	}
	
	public String getNextPage() {
		return Checkout.class.getSimpleName().toLowerCase();
	}
	
	public String getNextPageURL() {
		return (request.isSecure() ? "https://" : "http://") + request.getServerName() + request.getContextPath() + "/" + getNextPage();
	}
	
	/**
     * Create checkSession StreamResponse.
     * @param session - session for check.
     * @return Text stream response.
     */
    public StreamResponse onActionFromCheckSession() {
    	return Checkout.checkSession(request.getSession(false));
    }
}
