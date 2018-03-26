package ish.oncourse.ui.utils;

import ish.oncourse.model.*;
import ish.oncourse.model.auto._CourseProductRelation;
import ish.oncourse.services.courseclass.CheckClassAge;
import ish.oncourse.services.courseclass.ClassAge;
import ish.oncourse.services.preference.GetPreference;
import ish.oncourse.services.preference.Preferences;
import ish.oncourse.services.search.CourseClassUtils;
import ish.oncourse.solr.query.SearchParams;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static ish.common.types.EntityRelationType.COURSE;
import static ish.oncourse.model.auto._EntityRelation.*;
import static java.lang.Boolean.TRUE;
import static org.apache.cayenne.query.QueryCacheStrategy.LOCAL_CACHE;

public class CourseItemModel {

	private List<CourseClass> enrollableClasses = new LinkedList<>();

	private List<CourseClass> availableClasses = new LinkedList<>();

	private List<CourseClass> otherClasses = new LinkedList<>();

	private List<CourseClass> fullClasses = new LinkedList<>();

	private List<Product> relatedProducts = new LinkedList<>();

	private List<Course> relatedCourses = new LinkedList<>();

	private Course course;

	public static CourseItemModel valueOf(Course course, SearchParams searchParams) {
		CourseItemModel model = new CourseItemModel();
		model.course = course;

		model.relatedProducts.addAll(selectRelatedProducts(course));
		model.relatedCourses.addAll(selectRelatedCourses(course));

		List<CourseClass> webVisibleClasses = getWebVisibleClasses(course);

		CheckClassAge hideClassOnWebAge = hideClassOnWebAge(course.getCollege());
		CheckClassAge stopWebEnrolmentsAge = stopWebEnrolmentsAge(course.getCollege());


		webVisibleClasses.forEach((cc) -> {
			if (hideClassOnWebAge.courseClass(cc).check()) {

				boolean hasPlaces = cc.isHasAvailableEnrolmentPlaces();

				if (hasPlaces) {
					if (stopWebEnrolmentsAge.courseClass(cc).check())
						model.enrollableClasses.add(cc);
					if (searchParams == null)
						model.availableClasses.add(cc);
					else if (CourseClassUtils.focusMatchForClass(cc, searchParams) == 1.0f)
						model.availableClasses.add(cc);
					else
						model.otherClasses.add(cc);
				} else
					model.fullClasses.add(cc);


			}
		});
		return model;
	}

	public List<CourseClass> getAvailableClasses() {
		return availableClasses;
	}

	public List<CourseClass> getOtherClasses() {
		return otherClasses;
	}

	public List<CourseClass> getFullClasses() {
		return fullClasses;
	}

	public Course getCourse() {
		return course;
	}

	/**
	 * The method returns true when the course has qualification or modules
	 */
	public boolean isNRT() {
		return course.getQualification() != null || !course.getModules().isEmpty();
	}


	public boolean isShowModules() {
		return !course.getModules().isEmpty();
	}

	public boolean isHaveRelatedCourses() {
		return !relatedCourses.isEmpty();
	}

	public boolean hasRelatedProducts() {
		return !relatedProducts.isEmpty();
	}

	public List<Product> getRelatedProducts() {
		return relatedProducts;
	}

	public List<CourseClass> getEnrollableClasses() {
		return enrollableClasses;
	}


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

	static List<Course> selectRelatedCourses(Course course) {
		List<CourseCourseRelation> relations = new LinkedList<>(
				ObjectSelect.query(CourseCourseRelation.class)
						.or(FROM_ENTITY_IDENTIFIER.eq(COURSE).andExp(FROM_ENTITY_WILLOW_ID.eq(course.getId())),
								TO_ENTITY_IDENTIFIER.eq(COURSE).andExp(TO_ENTITY_WILLOW_ID.eq(course.getId())))
						.and(CourseCourseRelation.COLLEGE.eq(course.getCollege()))
						.cacheStrategy(LOCAL_CACHE, CourseCourseRelation.class.getSimpleName()).select(course.getObjectContext()));
		return relations.stream()
				.map((r) -> Arrays.asList(r.getFromCourse(), r.getToCourse()))
				.flatMap(Collection::stream)
				.filter(c -> !c.getId().equals(course.getId()) && c.getIsWebVisible())
				.sorted((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()))
				.collect(Collectors.toList());
	}

	static List<Product> selectRelatedProducts(Course course) {
		return ObjectSelect.query(CourseProductRelation.class)
				.where(CourseProductRelation.COURSE.eq(course))
				.and(CourseCourseRelation.COLLEGE.eq(course.getCollege()))
				.and(CourseProductRelation.PRODUCT.dot(Product.IS_WEB_VISIBLE).eq(TRUE))
				.cacheStrategy(LOCAL_CACHE, CourseProductRelation.class.getSimpleName()).select(course.getObjectContext())
				.stream()
				.map(_CourseProductRelation::getProduct).collect(Collectors.toList());
	}


	private static List<CourseClass> getWebVisibleClasses(Course course) {
		return ObjectSelect.query(CourseClass.class).where(CourseClass.COURSE.eq(course))
				.and(CourseClass.IS_WEB_VISIBLE.isTrue())
				.and(CourseClass.CANCELLED.isFalse())
				.orderBy(CourseClass.START_DATE.asc())
				.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, CourseClass.class.getSimpleName())
				.select(course.getObjectContext());
	}
}