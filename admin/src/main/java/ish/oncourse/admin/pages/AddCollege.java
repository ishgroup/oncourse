package ish.oncourse.admin.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;

public class AddCollege {
	
	@Property
	private String findCollegeUrl;
	
	@Inject
	private Request request;
	
	@Inject
	private Response response;
	
	@SetupRender
	void setupRender() {
		this.findCollegeUrl = response.encodeURL(request.getContextPath() + "/findCollege");
	}
}
