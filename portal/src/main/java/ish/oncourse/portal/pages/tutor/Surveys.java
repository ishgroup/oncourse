package ish.oncourse.portal.pages.tutor;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Survey;
import ish.oncourse.model.TutorRole;
import ish.oncourse.portal.annotations.UserRole;
import ish.oncourse.portal.pages.PageNotFound;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;

@UserRole("tutor")
public class Surveys {
	
	@Property
	private CourseClass courseClass;

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
	
	private double courseAverage;
	
	private double tutorAverage;
	
	private double venueAverage;

	Object onActivate(String id) {
		if (id != null && id.length() > 0 && id.matches("\\d+")) {
			long idLong = Long.parseLong(id);
			List<CourseClass> list = courseClassService.loadByIds(idLong);
			if (list.isEmpty())
				return pageNotFound;
			this.courseClass =  list.get(0);
			
			this.surveys = getSurveysForClass(courseClass);
			
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

	private List<Survey> getSurveysForClass(CourseClass courseClass) {
		Expression surveyExp = ExpressionFactory.matchExp(Survey.ENROLMENT_PROPERTY + "." + Enrolment.COURSE_CLASS_PROPERTY, courseClass);
		SelectQuery query = new SelectQuery(Survey.class, surveyExp);
		
		return cayenneService.sharedContext().performQuery(query);
	}
}
