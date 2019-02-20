package ish.oncourse.ui.utils;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Product;
import ish.oncourse.services.courseclass.CheckClassAge;
import ish.oncourse.services.search.CourseClassUtils;
import ish.oncourse.solr.query.SearchParams;

import java.util.LinkedList;
import java.util.List;

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

		model.relatedProducts.addAll(CourseItemModelDaoHelper.selectRelatedProducts(course));
		model.relatedCourses.addAll(CourseItemModelDaoHelper.selectRelatedCourses(course));

		List<CourseClass> webVisibleClasses = CourseItemModelDaoHelper.getWebVisibleClasses(course);

		CheckClassAge hideClassOnWebAge = CourseItemModelDaoHelper.hideClassOnWebAge(course.getCollege());
		CheckClassAge stopWebEnrolmentsAge = CourseItemModelDaoHelper.stopWebEnrolmentsAge(course.getCollege());


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

	public List<Course> getRelatedCourses() {
		return relatedCourses;
	}

	public List<CourseClass> getEnrollableClasses() {
		return enrollableClasses;
	}
}