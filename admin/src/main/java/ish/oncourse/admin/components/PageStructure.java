package ish.oncourse.admin.components;

import ish.oncourse.admin.pages.Index;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

public class PageStructure {
	
	@Property
	@Parameter
	private String title;
	
	@InjectPage
	private Index index;
	
	@Inject
	private Request request;
	
	public Object onActionFromLogout() throws Exception {
		Session session = request.getSession(false);
		session.invalidate();
		return index;
	}
}
