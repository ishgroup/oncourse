package ish.oncourse.enrol.pages;

import ish.oncourse.enrol.services.concessions.IConcessionsService;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Student;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.preference.IPreferenceService;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class EnrolCourses {

	@Inject
	private ICookiesService cookiesService;

	@Inject
	private ICourseClassService courseClassService;

	@Inject
	private IConcessionsService concessionsService;

	@Inject
	private IPreferenceService preferenceService;

	@Inject
	private Request request;

	@Property
	private List<CourseClass> classesToEnrol;

	@Property
	private CourseClass courseClass;

	/**
	 * studentsSet.allObjects.@sort.contact.fullName
	 */
	@Property
	private List<Student> students;

	@Property
	private Student student;

	@Property
	@Parameter
	private boolean hadPreviousPaymentFailure;

	@SetupRender
	void beforeRender() {
		String[] orderedClassesIds = cookiesService
				.getCookieCollectionValue("shortlist");
		if (orderedClassesIds != null && orderedClassesIds.length != 0) {
			classesToEnrol = courseClassService.loadByIds(orderedClassesIds);
			List<Ordering> orderings = new ArrayList<Ordering>();
			orderings.add(new Ordering(CourseClass.COURSE_PROPERTY + "."
					+ Course.CODE_PROPERTY, SortOrder.ASCENDING));
			orderings.add(new Ordering(CourseClass.CODE_PROPERTY,
					SortOrder.DESCENDING));
			Ordering.orderList(classesToEnrol, orderings);
		}
	}

	public boolean isShowConcessionsArea() {
		return (!concessionsService.getActiveConcessionTypes().isEmpty())
				&& Boolean.valueOf(preferenceService.getPreferenceByKey(
						"feature.concessionsInEnrolment").getValueString());
	}

	public String getCoursesListLink() {
		return "http://" + request.getServerName() + "/courses";
	}

}
