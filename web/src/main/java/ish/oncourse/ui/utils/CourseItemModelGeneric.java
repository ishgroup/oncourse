package ish.oncourse.ui.utils;

import ish.oncourse.model.Course;
import ish.oncourse.model.Product;

import java.util.LinkedList;
import java.util.List;

abstract public class CourseItemModelGeneric<T> {

	protected List<T> enrollableClasses = new LinkedList<>();
	protected List<T> availableClasses = new LinkedList<>();
	protected List<T> otherClasses = new LinkedList<>();
	protected List<T> fullClasses = new LinkedList<>();
	protected List<Product> relatedProducts = new LinkedList<>();
	protected List<Course> relatedCourses = new LinkedList<>();
	protected Course course;

	public CourseItemModelGeneric(Course course, List<Product> relatedProducts, List<Course> relatedCourses) {
		this.course = course;
		this.relatedCourses.addAll(relatedCourses);
		this.relatedProducts.addAll(relatedProducts);
	}

	public List<T> getAvailableClasses() {
		return availableClasses;
	}

	public List<T> getOtherClasses() {
		return otherClasses;
	}

	public List<T> getFullClasses() {
		return fullClasses;
	}

	public List<T> getEnrollableClasses() {
		return enrollableClasses;
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
}
