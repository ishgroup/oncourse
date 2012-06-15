package ish.oncourse.model;

import ish.oncourse.model.auto._Course;
import ish.oncourse.utils.QueueableObjectUtils;
import ish.oncourse.utils.TimestampUtilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

public class Course extends _Course implements Queueable {
	private static final long serialVersionUID = 254942637990278217L;
	public static final String COURSE_TAG = "courseTag";

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	public List<CourseClass> getEnrollableClasses() {
		List<CourseClass> currentClasses = getCurrentClasses();
		List<CourseClass> list = new ArrayList<CourseClass>();

		for (CourseClass courseClass : currentClasses) {
			if (courseClass.isHasAvailableEnrolmentPlaces()) {
				list.add(courseClass);
			}
		}

		return list;
	}

	public List<CourseClass> getCurrentClasses() {
		List<CourseClass> courseClasses = getCourseClasses();
		List<CourseClass> list = new ArrayList<CourseClass>();

		for (CourseClass courseClass : courseClasses) {
			if (Boolean.TRUE.equals(courseClass.getIsWebVisible())
					&& !courseClass.isCancelled()
					&& (courseClass.getEndDate() == null || courseClass.getEndDate().after(
							TimestampUtilities.normalisedDate(new Date())))) {
				list.add(courseClass);
			}

		}

		return list;
	}
	
	public List<CourseClass> getFullClasses() {
		List<CourseClass> currentClasses = getCurrentClasses();
		List<CourseClass> list = new ArrayList<CourseClass>();

		for (CourseClass courseClass : currentClasses) {
			if (!courseClass.isHasAvailableEnrolmentPlaces()) {
				list.add(courseClass);
			}
		}

		return list;
	}

	public List<Module> getModules() {
		final List<Module> result = new ArrayList<Module>();
		for (final CourseModule courseModule : getCourseModules()) {
			Module module = courseModule.getModule();
			if (module != null && module.getPersistenceState() == PersistenceState.HOLLOW) {
				final SelectQuery moduleQuery = new SelectQuery(Module.class);
				moduleQuery.andQualifier(ExpressionFactory.matchDbExp(Module.ID_PK_COLUMN, module.getId()));
				@SuppressWarnings("unchecked")
				List<Module> queryResult = getObjectContext().performQuery(moduleQuery);
				module = queryResult.isEmpty() ? null : queryResult.get(0); 
			}
			if (module != null) {
				result.add(module);
			}
		}
		return result;
	}

	@Override
	public Qualification getQualification() {
		Qualification qualification = super.getQualification();
		if (qualification != null && qualification.getPersistenceState() == PersistenceState.HOLLOW) {
			final SelectQuery qualificationQuery = new SelectQuery(Qualification.class);
			qualificationQuery.andQualifier(ExpressionFactory.matchDbExp(Qualification.ID_PK_COLUMN, qualification.getId()));
			@SuppressWarnings("unchecked")
			List<Qualification> result = getObjectContext().performQuery(qualificationQuery);
			qualification = result.isEmpty() ? null : result.get(0);
		}
		return qualification;
	}
	
}
