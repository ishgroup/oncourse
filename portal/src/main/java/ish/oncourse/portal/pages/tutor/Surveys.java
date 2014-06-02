package ish.oncourse.portal.pages.tutor;

import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Survey;
import ish.oncourse.model.TutorRole;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.annotations.UserRole;
import ish.oncourse.portal.pages.PageNotFound;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;

@UserRole("tutor")
public class Surveys {
	
	@Property
	private CourseClass courseClass;

	@Property
	private Contact tutorContact;

	@Property
	private TutorRole tutorRole;

	@Inject
	private ICourseClassService courseClassService;
	
	@Inject
	private ICayenneService cayenneService;

	@InjectPage
	private PageNotFound pageNotFound;
	
	@Property
	private Survey survey;

	@Property
	private List<Survey> surveys;

	@Inject
	private Request request;

	@Inject
	private IAuthenticationService authService;

    @Inject
    private IPortalService portalService;
	
	private double courseAverage;
	
	private double tutorAverage;
	
	private double venueAverage;

	private double totalAverage;

	Object onActivate() {
		if (courseClass == null)
		{
			tutorContact = portalService.getContact();
			this.surveys = courseClassService.getSurveysFor(tutorContact.getTutor());
		}
		return null;
	}

	Object onActivate(String id) {
		if (id != null && id.length() > 0 && id.matches("\\d+")) {
			long idLong = Long.parseLong(id);
			List<CourseClass> list = courseClassService.loadByIds(idLong);
			if (list.isEmpty())
				return pageNotFound;
			this.courseClass =  list.get(0);
			
			this.surveys = courseClassService.getSurveysFor(this.courseClass);
			
			calculateAverage();
			
			return null;
		} else {
			return pageNotFound;
		}
	}
	
	private void calculateAverage() {
		double courseRating = 0;
		double tutorRating = 0;
		double venueRating = 0;
		
		for (Survey survey : surveys) {
			courseRating += survey.getCourseScore();
			tutorRating += survey.getTutorScore();
			venueRating += survey.getVenueScore();
		}
		
		courseAverage = courseRating / surveys.size();
		tutorAverage = tutorRating / surveys.size();
		venueAverage = venueRating / surveys.size();

		totalAverage = (courseAverage + tutorRating + venueRating)/3;
	}
	
	public String getCourseAverage() {
		return Double.isNaN(courseAverage) ? "" : String.format("%.1f", courseAverage);
	}
	
	public String getTutorAverage() {
		return Double.isNaN(tutorAverage) ? "" : String.format("%.1f", tutorAverage);
	}
	
	public String getVenueAverage() {
		return Double.isNaN(venueAverage) ? "" : String.format("%.1f", venueAverage);
	}

	public String getTotalAverage() {
		return Double.isNaN(totalAverage) ? "" : String.format("%.1f", totalAverage);
	}


	public Long getRoundedCourseAverage() {
		return Math.round(courseAverage);
	}
	
	public Long getRoundedTutorAverage() {
		return Math.round(tutorAverage);
	}
	
	public Long getRoundedVenueAverage() {
		return Math.round(venueAverage);
	}

	public String getCourseName() {
		return courseClass.getCourse().getName();
	}

	public String getCourseClassCode() {
		return "(" + courseClass.getCourse().getCode() + "-" + courseClass.getCode() + ")";
	}

	public String getRoom() {
		return PortalUtils.getClassPlaceBy(courseClass);
	}

	public List<TutorRole> getTutorRoles() {
		return courseClass.getTutorRoles();
	}

	public String getContextPath() {
		return request.getContextPath();
	}

	public String getTutorRoleName() {
		return tutorRole.getTutor().getContact().getFullName();
	}

}
