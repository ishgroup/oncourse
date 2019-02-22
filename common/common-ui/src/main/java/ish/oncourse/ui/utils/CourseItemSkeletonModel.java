package ish.oncourse.ui.utils;

import ish.oncourse.model.Course;
import ish.oncourse.model.Product;

import java.util.List;

public class CourseItemSkeletonModel extends CourseItemModelGeneric<CourseItemSkeletonModel.CourseClassProjection> {

	public CourseItemSkeletonModel(Course course, List<Product> relatedProducts, List<Course> relatedCourses) {
		super(course, relatedProducts, relatedCourses);
	}

	public void setAvailableClasses(List<CourseClassProjection> classes){
		this.availableClasses = classes;
	}

	public static class CourseClassProjection {
		private String id;
		private String score;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getScore() {
			return score;
		}

		public void setScore(String score) {
			this.score = score;
		}
	}
}
