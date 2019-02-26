package ish.oncourse.ui.utils;

import ish.oncourse.model.Course;
import ish.oncourse.model.Product;

import java.util.List;

public class CourseItemSkeletonModel extends CourseItemModelGeneric<CourseItemSkeletonModel.CourseClassProjection> {

	public CourseItemSkeletonModel(Course course, List<Product> relatedProducts, List<Course> relatedCourses) {
		super(course, relatedProducts, relatedCourses);
	}

	public void setAvailableClasses(List<CourseClassProjection> projections){
		this.availableClasses = projections;
	}

	public void setOtherCLasses(List<CourseClassProjection> projections){
		this.otherClasses = projections;
	}

	public static class CourseClassProjection {
		private String id;
		private Float score;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public Float getScore() {
			return score;
		}

		public void setScore(Float score) {
			this.score = score;
		}
	}
}
