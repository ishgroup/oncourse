package ish.oncourse.portal.components.timetable;


import ish.oncourse.model.Session;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.contact.IContactService;
import ish.oncourse.services.courseclass.ICourseClassService;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.UidGenerator;
import org.apache.log4j.Logger;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: artem
 * Date: 10/14/13
 * Time: 1:27 PM
 */
public class Calendar {

    @Inject
	@Property
    private IAuthenticationService authService;


    @Inject
    private IPortalService portalService;

	@Inject
	@Property
	private Request request;

    @OnEvent(value = "getCalendarEvents")
	public StreamResponse getCalendarEvents() throws IOException {

       return new TextStreamResponse("text/json", portalService.getCalendarEvents(authService.getUser()).toString());
	}
}
