package ish.oncourse.portal.components;

import ish.oncourse.portal.access.IAuthenticationService;

import org.apache.tapestry5.ioc.annotations.Inject;

public class LeftBlock {

	@Inject
	private IAuthenticationService authService;
	
	public String getUserCalendarFilename(){
		
		String fileName = authService.getUser().getUniqueCode() + ".ics";
		
		return fileName;
	}
}
