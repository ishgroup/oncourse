package ish.oncourse.admin.pages;

import ish.oncourse.model.College;
import ish.oncourse.services.system.ICollegeService;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

public class FindCollege {

	@Inject
	private Request request;
	
	@Inject
	private ICollegeService collegeService;
	
	public StreamResponse onActivate() {
		String serviceKey = request.getParameter("key");
		JSONObject response = new JSONObject();
		
		College college = collegeService.findBySecurityCodeLastChars(serviceKey);
		if (college != null) {
			if (college.getBillingCode() != null) {
				response.put("status", "ACTIVE");
				response.put("message", "College already active.");
				
			}
			else {
				response.put("status", "SETUP");
				response.put("message", "Found. Please fill following fields, then press Next to proceed to billing setup.");
				response.put("id", college.getId());
			}
		}
		else {
			response.put("status", "NOT FOUND");
			response.put("message", "Could not find key.");
		}
		
		return new TextStreamResponse("text/json", response.toString());
	}
}
