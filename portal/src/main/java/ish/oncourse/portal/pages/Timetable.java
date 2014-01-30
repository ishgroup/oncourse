package ish.oncourse.portal.pages;

import ish.oncourse.model.Contact;
import ish.oncourse.portal.access.IAuthenticationService;

import ish.oncourse.services.cookies.ICookiesService;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class Timetable {

	@Inject
    @Property
	private IAuthenticationService authService;
    @Inject
    private ICookiesService   cookieService;

	@Property
	private Contact contact;
	
	@Property
	private String timetableMonthUrl;
	
	@Inject
    @Property
	private Request request;

	@SetupRender
	void setupRender() {
		this.contact = authService.getUser();
		this.timetableMonthUrl = getContextPath() + "/timetableJson";
	}
	
	public String getMonthPageName() {
		return "timetable";
	}
	
	public String getContextPath() {
		return request.getContextPath();
	}
	
	public String getListPageName() {
		return "timetableList";
	}
}
