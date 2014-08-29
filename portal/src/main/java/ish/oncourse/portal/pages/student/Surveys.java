package ish.oncourse.portal.pages.student;

import ish.common.types.EnrolmentStatus;
import ish.oncourse.model.*;
import ish.oncourse.portal.annotations.UserRole;
import ish.oncourse.portal.pages.PageNotFound;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.portal.services.ValueChangeDelegate;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.util.SecurityUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;

@UserRole("student")
public class Surveys {

	@Property
	@Persist
	private CourseClass courseClass;

	@Property
	private TutorRole tutorRole;
	
	@Inject
	private ICourseClassService courseClassService;
	
	@Inject
	private ICayenneService cayenneService;
	
	@InjectPage
	private PageNotFound pageNotFound;
	
	@InjectComponent
	private Form surveyForm;

	@Persist
	private Survey survey;
	
	@Persist
	@Property
	private ObjectContext context;

	@Inject
	private Request request;

    @Inject
    private IPortalService portalService;

	Object onActivate()
	{
		 if (courseClass == null)
			 return pageNotFound;
		else
			 return null;
	}

	Object onActivate(String id) {
		if (id != null && id.length() > 0 && id.matches("\\d+")) {
			long idLong = Long.parseLong(id);
			List<CourseClass> list = courseClassService.loadByIds(idLong);
			if (list.isEmpty())
				return pageNotFound;
			this.courseClass =  list.get(0);
			
			if (context == null) {
				this.context = cayenneService.newContext();
			}
			
			if (survey == null) {
				Student student = context.localObject(portalService.getContact().getStudent());
				this.survey = getSurveyForStudentAndClass(student, courseClass);
				Enrolment studentEnrolment = getEnrolmentForStudentAndClass(student, courseClass);
				if (survey == null && studentEnrolment != null) {
					this.survey = context.newObject(Survey.class);
					survey.setCollege(student.getCollege());
					survey.setEnrolment(studentEnrolment);
					survey.setUniqueCode(SecurityUtil.generateRandomPassword(8));
				}
			}
			
			return null;
		} else {
			return pageNotFound;
		}
	}
	
	@OnEvent(component = "surveyForm", value = "success")
	Object submitted() {
		context.commitChanges();
		return this;
	}
	
	@OnEvent(component = "surveyForm", value = "validate")
	void validate() {
		if (survey.getCourseScore() == null) {
			surveyForm.recordError("You must set course score.");
		}
		if (survey.getTutorScore() == null) {
			surveyForm.recordError("You must set tutor score.");
		}
		if (survey.getVenueScore() == null) {
			surveyForm.recordError("You must set venue score.");
		}
		if (survey.getComment() == null) {
			surveyForm.recordError("Comment is required.");
		}
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

	public String getClassSessionsInfo() {
		return PortalUtils.getClassSessionsInfoBy(courseClass);
	}

	public String getClassIntervalInfo() {
		return PortalUtils.getClassIntervalInfoBy(courseClass);
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

	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}
	
	public ValueChangeDelegate<Integer> getCourseRatingDelegate() {
		return new ValueChangeDelegate<Integer>() {
			
			@Override
			public void changeValue(Integer value) {
				survey.setCourseScore(value);
			}
		};
	}
	
	public ValueChangeDelegate<Integer> getTutorRatingDelegate() {
		return new ValueChangeDelegate<Integer>() {
			
			@Override
			public void changeValue(Integer value) {
				survey.setTutorScore(value);
			}
		};
	}
	
	public ValueChangeDelegate<Integer> getVenueRatingDelegate() {
		return new ValueChangeDelegate<Integer>() {
			
			@Override
			public void changeValue(Integer value) {
				survey.setVenueScore(value);
			}
		};
	}
	
	private Enrolment getEnrolmentForStudentAndClass(Student student, CourseClass courseClass) {
		Expression enrolmentExp = ExpressionFactory.matchExp(Enrolment.STUDENT_PROPERTY, student)
			.andExp(ExpressionFactory.matchExp(Enrolment.COURSE_CLASS_PROPERTY, courseClass))
			.andExp(ExpressionFactory.matchExp(Enrolment.STATUS_PROPERTY, EnrolmentStatus.SUCCESS));
		SelectQuery query = new SelectQuery(Enrolment.class, enrolmentExp);
		
		return (Enrolment) Cayenne.objectForQuery(student.getObjectContext(), query);
	}
	
	private Survey getSurveyForStudentAndClass(Student student, CourseClass courseClass) {
		Expression surveyExp = ExpressionFactory.matchExp(Survey.ENROLMENT_PROPERTY + "." + Enrolment.STUDENT_PROPERTY, student)
				.andExp(ExpressionFactory.matchExp(Survey.ENROLMENT_PROPERTY + "." + Enrolment.COURSE_CLASS_PROPERTY, courseClass));
		SelectQuery query = new SelectQuery(Survey.class, surveyExp);
		
		return (Survey) Cayenne.objectForQuery(student.getObjectContext(), query);
	}

}
