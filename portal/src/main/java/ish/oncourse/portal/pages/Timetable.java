package ish.oncourse.portal.pages;

import ish.oncourse.model.Contact;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.cookies.ICookiesService;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class Timetable {

    @Inject
    private ICookiesService   cookieService;

	@Property
	private Contact contact;
	
	@Property
	private String timetableMonthUrl;
	
	@Inject
    @Property
	private Request request;

    @Inject
    private IPortalService portalService;

	@SetupRender
	void setupRender() {
		this.contact = portalService.getContact();
		this.timetableMonthUrl = getContextPath() + "/timetableJson";
	}

	public String getContextPath() {
		return request.getContextPath();
	}
}
