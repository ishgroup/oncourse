package ish.oncourse.portal.pages;

import ish.oncourse.model.Session;
import ish.oncourse.services.contact.IContactService;
import ish.oncourse.services.courseclass.ICourseClassService;

import java.io.StringWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;

import org.apache.log4j.Logger;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.util.TextStreamResponse;

/*
 URL:/contact/6giVOgEbBEBhAWjZ.ics, where 6giVOgEbBEBhAWjZ is uniqueCode of contact
 */
public class Contact {

	private static final Logger logger = Logger.getLogger(Contact.class);

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

					Calendar icsCalendar = new Calendar();

					icsCalendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
					icsCalendar.getProperties().add(Version.VERSION_2_0);
					icsCalendar.getProperties().add(CalScale.GREGORIAN);

					List<Session> sessions = courseClassService.getContactSessions(contact);

					for (Session s : sessions) {
						
						StringBuilder sessionInformation = new StringBuilder(s.getCourseClass().getCourse().getName());
						sessionInformation.append("\n");
						sessionInformation.append(s.getCollege().getName());
						sessionInformation.append("\n");
						sessionInformation.append(s.getRoom().getName());
						sessionInformation.append("\n");
						sessionInformation.append(s.getRoom().getSite().getName());
						sessionInformation.append("\n");
						sessionInformation.append(s.getRoom().getSite().getStreet());
						sessionInformation.append(" ");
						sessionInformation.append(s.getRoom().getSite().getSuburb());
						sessionInformation.append(" ");
						sessionInformation.append(s.getRoom().getSite().getState());
						sessionInformation.append(" ");
						sessionInformation.append(s.getRoom().getSite().getPostcode());
						
						Dur dur = new Dur(s.getStartDate(), s.getEndDate());
						
						VEvent event = new VEvent(new net.fortuna.ical4j.model.Date(s.getStartDate()), dur, sessionInformation.toString());

						UidGenerator ug = new UidGenerator("uidGen");
						Uid uid = ug.generateUid();
						event.getProperties().add(uid);

						icsCalendar.getComponents().add(event);
					}

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
