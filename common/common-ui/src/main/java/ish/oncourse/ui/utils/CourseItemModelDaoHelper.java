package ish.oncourse.ui.utils;

import ish.oncourse.model.*;
import ish.oncourse.model.auto._CourseProductRelation;
import ish.oncourse.model.auto._ProductCourseRelation;
import ish.oncourse.services.courseclass.CheckClassAge;
import ish.oncourse.services.courseclass.ClassAge;
import ish.oncourse.services.preference.GetPreference;
import ish.oncourse.services.preference.Preferences;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;

import java.util.*;
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
		List<Product> products = new ArrayList<>();
		products.addAll(ObjectSelect.query(CourseProductRelation.class)
				.where(CourseProductRelation.FROM_COURSE.eq(course))
				.and(CourseCourseRelation.COLLEGE.eq(course.getCollege()))
				.and(CourseProductRelation.TO_PRODUCT.dot(Product.IS_WEB_VISIBLE).eq(TRUE))
				.cacheStrategy(LOCAL_CACHE, CourseProductRelation.class.getSimpleName())
				.select(course.getObjectContext())
				.stream()
				.map(_CourseProductRelation::getToProduct)
				.collect(Collectors.toList())
		);
		products.addAll(ObjectSelect.query(ProductCourseRelation.class)
				.where(ProductCourseRelation.TO_COURSE.eq(course))
				.and(ProductCourseRelation.COLLEGE.eq(course.getCollege()))
				.and(ProductCourseRelation.FROM_PRODUCT.dot(Product.IS_WEB_VISIBLE).eq(TRUE))
				.cacheStrategy(LOCAL_CACHE, ProductCourseRelation.class.getSimpleName())
				.select(course.getObjectContext())
				.stream()
				.map(_ProductCourseRelation::getFromProduct)
				.collect(Collectors.toList())
		);
		return products;
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
