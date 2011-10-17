package ish.oncourse.portal.pages;

import ish.oncourse.model.Session;
import ish.oncourse.services.contact.IContactService;
import ish.oncourse.services.courseclass.ICourseClassService;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Name;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.model.property.XProperty;
import net.fortuna.ical4j.util.UidGenerator;

import org.apache.log4j.Logger;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.util.TextStreamResponse;

/*
 URL:/contact/6giVOgEbBEBhAWjZ.ics, where 6giVOgEbBEBhAWjZ is uniqueCode of contact
 */
public class Calendar {

	private static final Logger logger = Logger.getLogger(Calendar.class);

	private static final Pattern NAME_PATTERN = Pattern.compile("(.*)\\.ics");

	@Inject
	private IContactService contactService;

	@Inject
	private ICourseClassService courseClassService;

	public StreamResponse onActivate(final String calendarFileName) {

		StringWriter iCalWriter = new StringWriter();

		try {

			Matcher m = NAME_PATTERN.matcher(calendarFileName);
			boolean matchFound = m.find();

			if (matchFound) {
				String uniqueCode = m.group(1);

				ish.oncourse.model.Contact contact = contactService.findByUniqueCode(uniqueCode);

				if (contact != null) {

					net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();

					icsCalendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
					icsCalendar.getProperties().add(Version.VERSION_2_0);
					icsCalendar.getProperties().add(CalScale.GREGORIAN);
					icsCalendar.getProperties().add(new XProperty("X-WR-CALNAME", contact.getCollege().getName()));
					//icsCalendar.getProperties().add(new XProperty("X-WR-CALDESC",description));					

					List<Session> sessions = courseClassService.getContactSessions(contact);

					List<VEvent> events = new ArrayList<VEvent>();
					
					for (Session s : sessions) {
						
						StringBuilder sessionInformation = new StringBuilder();
						sessionInformation.append(s.getCollege().getName());
						sessionInformation.append("\n");
						sessionInformation.append(s.getRoom().getName());
						sessionInformation.append(", ");
						sessionInformation.append(s.getRoom().getSite().getName());
						sessionInformation.append("\n");
						sessionInformation.append(s.getRoom().getSite().getStreet());
						sessionInformation.append(" ");
						sessionInformation.append(s.getRoom().getSite().getSuburb());
						sessionInformation.append(" ");
						sessionInformation.append(s.getRoom().getSite().getState());
						sessionInformation.append(" ");
						sessionInformation.append(s.getRoom().getSite().getPostcode());
						
						StringBuilder courseInformation = new StringBuilder();
						courseInformation.append(s.getCourseClass().getCourse().getName());
						courseInformation.append(" (");
						courseInformation.append(s.getCourseClass().getCourse().getCode());
						courseInformation.append("-");
						courseInformation.append(s.getCourseClass().getCode());	
						courseInformation.append(")");
						
						Dur dur = new Dur(s.getStartDate(), s.getEndDate());
						
						VEvent event = new VEvent(new net.fortuna.ical4j.model.DateTime(s.getStartDate()), dur, courseInformation.toString());
						event.getProperties().add(new Description(sessionInformation.toString()));
						
						UidGenerator ug = new UidGenerator("uidGen");
						Uid uid = ug.generateUid();
						event.getProperties().add(uid);

						events.add(event);

					}
					
					icsCalendar.getComponents().addAll(events);

					CalendarOutputter iCalOutputter = new CalendarOutputter();
					iCalOutputter.output(icsCalendar, iCalWriter);
				}
			}

		} catch (Exception e) {
			logger.error("Failed to generate iCal.", e);
		}

		return new TextStreamResponse("text/calendar", iCalWriter.toString());
	}
}
