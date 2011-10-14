package ish.oncourse.portal.components;

import ish.oncourse.portal.access.IAuthenticationService;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class LeftBlock {

	@Inject
	private IAuthenticationService authService;
	
	@Inject
	private Request request;
	
	public String getUserCalendarFilename(){
		
		String fileName = request.getServerName() + request.getContextPath() + "/calendar/" + authService.getUser().getUniqueCode() + ".ics";
		
		return fileName;
	}
}
