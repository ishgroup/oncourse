package ish.oncourse.ui.utils;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Product;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.search.CourseClassUtils;
import ish.oncourse.services.search.SearchParams;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;

import java.util.ArrayList;
import java.util.List;

public class CourseItemModel {

    private static final Ordering ORDERING = new Ordering(CourseClass.START_DATE_PROPERTY, SortOrder.ASCENDING);

    private List<CourseClass> availableClasses;

    private List<CourseClass> otherClasses;

    private List<CourseClass> fullClasses;

    private List<Product> relatedProducts;

    private Course course;

    public static CourseItemModel valueOf(Course course, ICourseService courseService) {
        return valueOf(course, null, courseService);
    }

    public static CourseItemModel valueOf(Course course, SearchParams searchParams, ICourseService courseService) {
        CourseItemModel courseItemModel = new CourseItemModel();
        courseItemModel.course = course;
        courseItemModel.otherClasses = new ArrayList<>();
        courseItemModel.fullClasses = new ArrayList<>();
        courseItemModel.availableClasses = new ArrayList<>();
        courseItemModel.relatedProducts = courseService.getRelatedProductsFor(course);

        if (searchParams == null) {
            courseItemModel.availableClasses.addAll(course.getEnrollableClasses());
            courseItemModel.fullClasses.addAll(course.getFullClasses());
        } else {

            List<CourseClass> currentClasses = course.getCurrentClasses();
                for (CourseClass courseClass : currentClasses) {
                    if (!courseClass.isHasAvailableEnrolmentPlaces()) {
                        courseItemModel.fullClasses.add(courseClass);
                    } else if (CourseClassUtils.focusMatchForClass(courseClass, searchParams) == 1.0f) {
                        courseItemModel.availableClasses.add(courseClass);
                    } else {
                        courseItemModel.otherClasses.add(courseClass);
                    }
                }
        }

        ORDERING.orderList(courseItemModel.fullClasses);
        ORDERING.orderList(courseItemModel.availableClasses);
        ORDERING.orderList(courseItemModel.otherClasses);
        return courseItemModel;
    }


    public List<CourseClass> getAvailableClasses() {
        return availableClasses;
    }

    public void setAvailableClasses(List<CourseClass> availableClasses) {
        this.availableClasses = availableClasses;
    }

    public List<CourseClass> getOtherClasses() {
        return otherClasses;
    }

    public void setOtherClasses(List<CourseClass> otherClasses) {
        this.otherClasses = otherClasses;
    }

    public List<CourseClass> getFullClasses() {
        return fullClasses;
    }

    public void setFullClasses(List<CourseClass> fullClasses) {
        this.fullClasses = fullClasses;
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
    	return !course.getRelatedToCourses().isEmpty();
    }

    public boolean hasRelatedProducts() {
        return !relatedProducts.isEmpty();
    }

    public List<Product> getRelatedProducts() {
        return relatedProducts;
    }
}
