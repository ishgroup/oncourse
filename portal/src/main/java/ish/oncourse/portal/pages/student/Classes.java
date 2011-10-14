package ish.oncourse.portal.pages.student;

import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.TutorRole;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.annotations.UserRole;
import ish.oncourse.services.courseclass.ICourseClassService;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

@UserRole("student")
public class Classes {

	private final static Logger LOGGER = Logger.getLogger(Classes.class);

	@Parameter
	@Property
	private CourseClass courseClass;

	@Persist
	private List<CourseClass> classes;

	@Parameter
	@Property
	private TutorRole tutorRole;

	private List<TutorRole> tutorRoles;

	@Inject
	private ICourseClassService courseClassService;

	@Inject
	private IAuthenticationService authenticationService;

	@SetupRender
	void beforeRender() {
		Contact contact = authenticationService.getUser();
		if (contact != null) {
			this.classes = courseClassService.getContactCourseClasses(contact);
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(String.format("Number of found classes is %s.",
						this.classes.size()));
			}
		}
	}

	public boolean isCurrentContactStudent() {
		return !authenticationService.isTutor();
	}

	public List<CourseClass> getClasses() {
		return classes;
	}

	public String getCourseName() {
		return courseClass.getCourse().getName();
	}

	public String getRoom() {
		return (courseClass.getRoom() != null) ? courseClass.getRoom()
				.getName() : "";
	}

	public String getClassSessionsInfo() {
		DecimalFormat hoursFormat = new DecimalFormat("0.#");
		int numberOfSession = courseClass.getSessions().size();
		String key = (numberOfSession > 1) ? " %s sessions, %s hours total"
				: " %s session, %s hours total";
		return String.format(key, numberOfSession, hoursFormat
				.format(courseClass.getTotalDurationHours().doubleValue()));
	}

	public String getClassIntervalInfo() {
		Date start = courseClass.getStartDate();
		Date end = courseClass.getEndDate();
		DateFormat formatter = new SimpleDateFormat("MMM. dd ");
		String key = "%s - %s ";
		if (start == null && end == null) {
			return "";
		} 

		return String.format(key, formatter.format(start),
				formatter.format(end));
	}

	public List<TutorRole> getTutorRoles() {
		return courseClass.getTutorRoles();
	}

	public String getTutorRoleName() {
		return tutorRole.getTutor().getContact().getFullName();
	}

	public String getClassPageName() {
		return authenticationService.isTutor() ? "tutor/class"
				: "student/class";
	}

}
