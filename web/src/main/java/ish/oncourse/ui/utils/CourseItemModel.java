package ish.oncourse.ui.utils;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.courseclass.CheckClassAge;
import ish.oncourse.services.search.CourseClassUtils;
import ish.oncourse.solr.query.SearchParams;

import java.util.List;

public class CourseItemModel extends CourseItemModelGeneric<CourseClass> {

	private CourseItemModel(Course course) {
		super(course, CourseItemModelDaoHelper.selectRelatedProducts(course), course.getRelatedCourses());
	}

	public static CourseItemModel valueOf(Course course, SearchParams searchParams) {
		CourseItemModel model = new CourseItemModel(course);
		model.fullFillClasses(searchParams);
		return model;
	}

	private void fullFillClasses(SearchParams searchParams) {
		List<CourseClass> webVisibleClasses = CourseItemModelDaoHelper.getWebVisibleClasses(course);

		CheckClassAge hideClassOnWebAge = CourseItemModelDaoHelper.hideClassOnWebAge(course.getCollege());
		CheckClassAge stopWebEnrolmentsAge = CourseItemModelDaoHelper.stopWebEnrolmentsAge(course.getCollege());

		webVisibleClasses.forEach((cc) -> {
			if (hideClassOnWebAge.courseClass(cc).check()) {

				boolean hasPlaces = cc.isHasAvailableEnrolmentPlaces();

				if (hasPlaces) {
					if (stopWebEnrolmentsAge.courseClass(cc).check())
						enrollableClasses.add(cc);
					if (searchParams == null)
						availableClasses.add(cc);
					else if (CourseClassUtils.focusMatchForClass(cc, searchParams) == 1.0f)
						availableClasses.add(cc);
					else
						otherClasses.add(cc);
				} else
					fullClasses.add(cc);
			}
		});
	}
}