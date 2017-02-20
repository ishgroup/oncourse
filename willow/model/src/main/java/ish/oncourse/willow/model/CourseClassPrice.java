package ish.oncourse.willow.model;

import java.util.ArrayList;
import java.util.List;

public class CourseClassPrice {
    private CourseClass courseClass;
    private List<Discount> discounts = new ArrayList<>();

    public CourseClass getCourseClass() {
        return courseClass;
    }

    public void setCourseClass(CourseClass courseClass) {
        this.courseClass = courseClass;
    }

    public List<Discount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<Discount> discounts) {
        this.discounts = discounts;
    }
}
