package ish.oncourse.admin.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class PageHeader {
	@Property
	@Parameter
	private String title;
	
	@Inject
	private Request request;
	
	public String getContextPath() {
		return request.getContextPath();
	}
}
