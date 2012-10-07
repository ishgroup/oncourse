package ish.oncourse.portal.pages.student;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Survey;
import ish.oncourse.model.TutorRole;
import ish.oncourse.portal.pages.PageNotFound;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.services.courseclass.ICourseClassService;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;

//@UserRole("student")
public class Surveys {

	@Property
	private CourseClass courseClass;

	@Property
	private TutorRole tutorRole;

	@Inject
	private ICourseClassService courseClassService;

	@InjectPage
	private PageNotFound pageNotFound;

	private Survey survey;

	@Inject
	private Request request;

	Object onActivate(String id) {
		if (id != null && id.length() > 0 && id.matches("\\d+")) {
			long idLong = Long.parseLong(id);
			List<CourseClass> list = courseClassService.loadByIds(idLong);
			if (list.isEmpty())
				return pageNotFound;
			this.courseClass =  list.get(0);
			return null;
		} else {
			return pageNotFound;
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

}
