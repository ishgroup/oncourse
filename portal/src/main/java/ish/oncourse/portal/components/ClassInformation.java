package ish.oncourse.portal.components;

import com.ocpsoft.pretty.time.PrettyTime;
import ish.oncourse.model.*;
import ish.oncourse.portal.services.IPortalService;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ish.oncourse.util.FormatUtils.getDateFormat;

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
    private IPortalService portalService;
	

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
            /**
             * we added enrolment.getCreated() != null condition to exlude NPE when some old enrolment has null value in create field
             * TODO The condition should be deleted after 21309 will be closed
             */
            if (created != null && (latestEnrol == null || created.after(latestEnrol))) {
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
		return portalService.getContact().getTutor() != null;
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
