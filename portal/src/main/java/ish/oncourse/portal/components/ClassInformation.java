package ish.oncourse.portal.components;

import static ish.oncourse.util.FormatUtils.getDateFormat;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Room;
import ish.oncourse.model.Site;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.portal.access.IAuthenticationService;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.ocpsoft.pretty.time.PrettyTime;

public class ClassInformation {

	@Parameter
	@Property
	private CourseClass courseClass;

	@Property
	private int enrolmentsCount;

	@Property
	private int maleEnrolmentsCount;

	@Property
	private int femaleEnrolmentsCount;

	@Property
	private String lastEnrolled;

	@Property
	private List<Tutor> tutors;

	@Property
	private Tutor tutor;

	@Property
	private Room room;

	@Property
	private Site site;
	
	@Property
	private int sessionsCount;
	
	@Inject
	@Property
	private IAuthenticationService authService;

	@SetupRender
	boolean setupRender() {
		if (courseClass == null) {
			return false;
		}
		List<Enrolment> enrolments = courseClass.getValidEnrolments();

		enrolmentsCount = enrolments.size();
		Date latestEnrol = null;
		for (Enrolment e : enrolments) {
			if (Boolean.TRUE.equals(e.getStudent().getContact().getIsMale())) {
				maleEnrolmentsCount++;
			} else {
				femaleEnrolmentsCount++;
			}

			Date created = e.getCreated();
			if (latestEnrol == null || created.after(latestEnrol)) {
				latestEnrol = created;
			}
		}
		if (latestEnrol != null) {
			lastEnrolled = new PrettyTime().format(latestEnrol);
		}

		tutors = new ArrayList<>();
		List<TutorRole> tutorRoles = courseClass.getTutorRoles();
		for (TutorRole tutorRole : tutorRoles) {
			tutors.add(tutorRole.getTutor());
		}

		room = courseClass.getRoom();
		if (room != null) {
			site = room.getSite();
		}
		sessionsCount = courseClass.getSessions().size();

		return true;
	}
	
	public boolean getIsNotDistantLearning() {
		return Boolean.FALSE.equals(courseClass.getIsDistantLearningCourse());
	}
	
	public String getClassTime() {
        DateFormat dateFormat = getDateFormat("dd MMMM yyyy", courseClass.getTimeZone());
        return String.format("%s - %s", dateFormat.format(courseClass.getStartDate()), dateFormat.format(courseClass.getEndDate()));
    }

	public boolean isHasEnrolments() {
		return enrolmentsCount > 0;
	}
	
	public boolean getIsTutor() {
		return authService.isTutor();
	}

	public String getMarkerTitle() {
		return String.format("%s, %s", StringEscapeUtils.escapeJavaScript(room.getName()),
				StringEscapeUtils.escapeJavaScript(site.getName()));
	}

	public String getInfoWindowContent() {

		String[] params = new String[] { room.getName(), site.getName(), site.getSuburb(), site.getStreet(),
				site.getPublicTransportDirections(), site.getDrivingDirections(), site.getSpecialInstructions(), room.getDirections() };

		List<String> escaptedParams = new ArrayList<>(params.length);

		for (String p : params) {
			escaptedParams.add(StringEscapeUtils.escapeJavaScript(p));
		}

		String infoWindowContent = String
				.format("<h1><b>%s, %s</b>, %s %s </h1><br/> <p><b>Public Transport Directions:</b> %s</p><p><b>Driving Directions:</b> %s</p> <p><b>Special Instructions:</b> %s</p><p><b>Room:</b> %s</p>",
						escaptedParams.toArray());

		return infoWindowContent;
	}
}
