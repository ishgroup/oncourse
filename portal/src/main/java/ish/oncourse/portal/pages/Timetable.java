package ish.oncourse.portal.pages;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import ish.oncourse.model.Tutor;
import ish.oncourse.portal.services.IPortalService;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Timetable {
	
	@Inject
    @Property
	private Request request;

    @Inject
	@Property
    private IPortalService portalService;
	
	@Inject
	private Block yourClasses;

	@Inject
	private Block sessionsSideBar;

	@Property
	private Date month;

	@Property
	private int pageSize;

	@Property
	private int offset;

	@Property
	private Student student;

	@Property
	private Tutor tutor;

	@Property
	private Contact contact;

	@Property
	private boolean showTeam;
	
	private String MONTH_FORMAT = "MMMMM-yyyy";

	@SetupRender
	void setupRender() {
		showTeam = portalService.getChildContacts().size() > 1;
	}
	
	@OnEvent(value = "getCalendarEvents")
	public StreamResponse getCalendarEvents(boolean showTeam, String selectedMonth) throws IOException, ParseException {

		Date month = new SimpleDateFormat(MONTH_FORMAT).parse(selectedMonth);
		
		return new TextStreamResponse("text/json", portalService.getCalendarEvents(month, showTeam).toString());
	}

	@OnEvent(value = "getYourClasses")
	public Block getYourClasses(String selectedMonth) throws ParseException {
		this.student = portalService.getContact().getStudent();
		this.tutor = portalService.getContact().getTutor();
		this.month = new SimpleDateFormat(MONTH_FORMAT).parse(selectedMonth);
		return yourClasses;
	}

	@OnEvent(value = "getSessions")
	public Block getSessions(boolean showTeam, String selectedMonth /**, int pageSize, int offset**/) throws ParseException {
		this.contact = portalService.getContact();
		this.showTeam = showTeam;
		this.month = new SimpleDateFormat(MONTH_FORMAT).parse(selectedMonth);
//		this.pageSize = pageSize;
//		this.offset = offset;
		return sessionsSideBar;
	}
}
