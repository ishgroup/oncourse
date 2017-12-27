package ish.oncourse.ui.utils;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Product;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.search.CourseClassUtils;
import ish.oncourse.solr.query.SearchParams;

import java.util.ArrayList;
import java.util.List;

public class CourseItemModel {

    private List<CourseClass> enrollableClasses;

    private List<CourseClass> availableClasses;

    private List<CourseClass> otherClasses;

    private List<CourseClass> fullClasses;

    private List<Product> relatedProducts;

    private List<Course> relatedCourses;

    private Course course;

    public static CourseItemModel valueOf(Course course, ICourseService courseService, ICourseClassService courseClassService) {
        return valueOf(course, null, courseService, courseClassService);
    }

    public static CourseItemModel valueOf(Course course, SearchParams searchParams, ICourseService courseService, ICourseClassService courseClassService) {
        CourseItemModel courseItemModel = new CourseItemModel();
        courseItemModel.course = course;
        courseItemModel.otherClasses = new ArrayList<>();
        courseItemModel.fullClasses = new ArrayList<>();
        courseItemModel.availableClasses = new ArrayList<>();
        courseItemModel.enrollableClasses = new ArrayList<>();
        courseItemModel.relatedProducts = courseService.getRelatedProductsFor(course);
        courseItemModel.relatedCourses = courseService.getRelatedCoursesFor(course);

        courseItemModel.enrollableClasses.addAll(courseClassService.getEnrollableClasses(course));
        if (searchParams == null) {
            courseItemModel.availableClasses.addAll(courseClassService.getCurrentClasses(course));
            courseItemModel.fullClasses.addAll(courseClassService.getFullClasses(course));
        } else {
            List<CourseClass> currentClasses = courseClassService.getCurrentClasses(course);
                for (CourseClass courseClass : currentClasses) {
                    if (courseClassService.isFullClass(courseClass)) {
                        courseItemModel.fullClasses.add(courseClass);
                    } else if (CourseClassUtils.focusMatchForClass(courseClass, searchParams) == 1.0f) {
                        courseItemModel.availableClasses.add(courseClass);
                    } else {
                        courseItemModel.otherClasses.add(courseClass);
                    }
                }
        }

        CourseClass.START_DATE.asc().orderList(courseItemModel.fullClasses);
        CourseClass.START_DATE.asc().orderList(courseItemModel.availableClasses);
        CourseClass.START_DATE.asc().orderList(courseItemModel.otherClasses);
        return courseItemModel;
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

    public void setCourse(Course course) {
        this.course = course;
    }

    /**
     * The method returns true when the course has qualification or modules
     */
    public boolean isNRT()
    {
       return course.getQualification() != null || !course.getModules().isEmpty();
    }


    public boolean isShowModules()
    {
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
}
