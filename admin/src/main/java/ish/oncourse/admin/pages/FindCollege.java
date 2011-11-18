package ish.oncourse.admin.pages;

import ish.oncourse.model.College;
import ish.oncourse.services.system.ICollegeService;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

public class FindCollege {

	@Inject
	private Request request;
	
	@Inject
	private ICollegeService collegeService;
	
	public StreamResponse onActivate() {
		String serviceKey = request.getParameter("key");
		
		College college = collegeService.findBySecurityCode(serviceKey);
		if (college != null) {
			if (college.getBillingCode() != null) {
				return new TextStreamResponse("text/html", "College already active.");
			}
			else {
				String billingLink = "<a href=\"" + request.getContextPath() + "/college/billing/" + college.getId() + "\">Set up billing.</a>";
				return new TextStreamResponse("text/html", "Found. " + billingLink);
			}
		}
		
		return new TextStreamResponse("text/html", "Could not find key.");
	}
}
