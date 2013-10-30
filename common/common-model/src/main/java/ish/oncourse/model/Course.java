package ish.oncourse.model;

import ish.oncourse.model.auto._Course;
import ish.oncourse.utils.QueueableObjectUtils;
import ish.oncourse.utils.TimestampUtilities;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Course extends _Course implements Queueable {
	private static final long serialVersionUID = 254942637990278217L;
	public static final String COURSE_TAG = "courseTag";
	private static final Logger LOGGER = Logger.getLogger(Course.class);

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	public List<CourseClass> getEnrollableClasses() {
		List<CourseClass> currentClasses = getCurrentClasses();
		List<CourseClass> list = new ArrayList<>();

		for (CourseClass courseClass : currentClasses) {
			if (courseClass.isHasAvailableEnrolmentPlaces()) {
				list.add(courseClass);
			}
		}

		return list;
	}

	public List<CourseClass> getCurrentClasses() {
		List<CourseClass> courseClasses = getCourseClasses();
		List<CourseClass> list = new ArrayList<>();

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
		List<CourseClass> list = new ArrayList<>();

		for (CourseClass courseClass : currentClasses) {
			if (!courseClass.isHasAvailableEnrolmentPlaces()) {
				list.add(courseClass);
			}
		}

		return list;
	}

	public List<Module> getModules() {
		final List<Module> result = new ArrayList<>();
		for (final CourseModule courseModule : getCourseModules()) {
			Module module = null;
			try {
				module = courseModule.getModule();
			} catch (Exception e) {
				LOGGER.warn(String.format("Exception occurrs when try to load course module with course id %s for college %s", getId(),
						getCollege().getId()), e);
				module = null;
			}
			if (module != null && module.getPersistenceState() == PersistenceState.HOLLOW) {
				final SelectQuery moduleQuery = new SelectQuery(Module.class);
				moduleQuery.andQualifier(ExpressionFactory.matchDbExp(Module.ID_PK_COLUMN, module.getId()));
				@SuppressWarnings("unchecked")
				List<Module> queryResult = getObjectContext().performQuery(moduleQuery);
				module = queryResult.isEmpty() ? null : queryResult.get(0);
				if (queryResult.size() > 1) {
					LOGGER.warn(String.format("%s objects found for module with id %s to course %s relationship but should be 1",
						queryResult.size(), module.getId(), getId()));
				}
			}
			if (module != null) {
				result.add(module);
			}
		}
		return result;
	}

	@Override
	public Qualification getQualification() {
		Qualification qualification = null;
		try {
			qualification = super.getQualification();
		} catch (Exception e) {
			LOGGER.warn(String.format("Exception occurrs when try to load course qualification with course id %s for college %s", getId(),
				getCollege().getId()), e);
			qualification = null;
		}
		if (qualification != null && qualification.getPersistenceState() == PersistenceState.HOLLOW) {
			final SelectQuery qualificationQuery = new SelectQuery(Qualification.class);
			qualificationQuery.andQualifier(ExpressionFactory.matchDbExp(Qualification.ID_PK_COLUMN, qualification.getId()));
			@SuppressWarnings("unchecked")
			List<Qualification> result = getObjectContext().performQuery(qualificationQuery);
			qualification = result.isEmpty() ? null : result.get(0);
			if (result.size() > 1) {
				LOGGER.warn(String.format("%s objects found for qualification with id %s to course %s relationship but should be 1",
					result.size(), qualification.getId(), getId()));
			}
		}
		return qualification;
	}

	public List<Course> getRelatedToCourses() {
		List<Course> relatedList = new ArrayList<>();
		for (CourseCourseRelation courseRelation : getFromCourses()) {
			Course relatedCourse = courseRelation.getToCourse();
			if (relatedCourse.getIsWebVisible()) {
				relatedList.add(relatedCourse);
			}
		}
		for (CourseCourseRelation courseRelation : getToCourses()) {
			Course relatedCourse = courseRelation.getFromCourse();
			if (relatedCourse.getIsWebVisible()) {
				relatedList.add(relatedCourse);
			}
		}
		return relatedList;
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
