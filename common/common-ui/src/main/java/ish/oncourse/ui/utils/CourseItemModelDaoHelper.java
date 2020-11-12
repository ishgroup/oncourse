package ish.oncourse.ui.utils;

import ish.oncourse.model.*;
import ish.oncourse.model.auto._CourseProductRelation;
import ish.oncourse.services.courseclass.CheckClassAge;
import ish.oncourse.services.courseclass.ClassAge;
import ish.oncourse.services.preference.GetPreference;
import ish.oncourse.services.preference.Preferences;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static ish.common.types.EntityRelationIdentifier.COURSE;
import static ish.oncourse.model.auto._EntityRelation.*;
import static java.lang.Boolean.TRUE;
import static org.apache.cayenne.query.QueryCacheStrategy.LOCAL_CACHE;

public class CourseItemModelDaoHelper {

	public static CheckClassAge hideClassOnWebAge(College college) {
		String age = new GetPreference(college, Preferences.HIDE_CLASS_ON_WEB_AGE, college.getObjectContext()).getValue();
		String type = new GetPreference(college, Preferences.HIDE_CLASS_ON_WEB_AGE_TYPE, college.getObjectContext()).getValue();
		return new CheckClassAge().classAge(ClassAge.valueOf(age, type));
	}

	public static CheckClassAge stopWebEnrolmentsAge(College college) {
		String age = new GetPreference(college, Preferences.STOP_WEB_ENROLMENTS_AGE, college.getObjectContext()).getValue();
		String type = new GetPreference(college, Preferences.STOP_WEB_ENROLMENTS_AGE_TYPE, college.getObjectContext()).getValue();
		return new CheckClassAge().classAge(ClassAge.valueOf(age, type));
	}

	public static List<Course> selectRelatedCourses(Course course) {
		List<CourseCourseRelation> relations = new LinkedList<>(
				ObjectSelect.query(CourseCourseRelation.class)
							.or(FROM_ENTITY_IDENTIFIER.eq(COURSE).andExp(FROM_ENTITY_WILLOW_ID.eq(course.getId())),
									TO_ENTITY_IDENTIFIER.eq(COURSE).andExp(TO_ENTITY_WILLOW_ID.eq(course.getId())))
							.and(CourseCourseRelation.COLLEGE.eq(course.getCollege()))
							.cacheStrategy(LOCAL_CACHE, CourseCourseRelation.class.getSimpleName())
							.select(course.getObjectContext()));

		return relations.stream()
						.map((r) -> Arrays.asList(r.getFromCourse(), r.getToCourse()))
						.flatMap(Collection::stream)
						.filter(c -> !c.getId().equals(course.getId()) && c.getIsWebVisible())
						.sorted((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()))
						.collect(Collectors.toList());
	}

	public static List<Product> selectRelatedProducts(Course course) {
		return ObjectSelect.query(CourseProductRelation.class)
							.where(CourseProductRelation.COURSE.eq(course))
							.and(CourseCourseRelation.COLLEGE.eq(course.getCollege()))
							.and(CourseProductRelation.PRODUCT.dot(Product.IS_WEB_VISIBLE).eq(TRUE))
							.cacheStrategy(LOCAL_CACHE, CourseProductRelation.class.getSimpleName())
							.select(course.getObjectContext())
							.stream()
							.map(_CourseProductRelation::getProduct)
							.collect(Collectors.toList());
	}

	static List<CourseClass> getWebVisibleClasses(Course course) {
		return ObjectSelect.query(CourseClass.class)
							.where(CourseClass.COURSE.eq(course))
							.and(CourseClass.IS_WEB_VISIBLE.isTrue())
							.and(CourseClass.CANCELLED.isFalse())
							.orderBy(CourseClass.START_DATE.asc())
							.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, CourseClass.class.getSimpleName())
							.select(course.getObjectContext());
	}
}
