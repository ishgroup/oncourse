package ish.oncourse.portal.components;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class BodyFooter {
	
	@Inject
	private Request request;
	
	public String getContextPath() {
		return request.getContextPath();
	}
}