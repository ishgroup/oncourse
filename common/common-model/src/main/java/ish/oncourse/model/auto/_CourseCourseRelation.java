package ish.oncourse.model.auto;

import ish.oncourse.model.Course;
import ish.oncourse.model.EntityRelation;

/**
 * Class _CourseCourseRelation was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _CourseCourseRelation extends EntityRelation {

    public static final String FROM_COURSE_PROPERTY = "fromCourse";
    public static final String TO_COURSE_PROPERTY = "toCourse";

    public static final String ID_PK_COLUMN = "id";

    public void setFromCourse(Course fromCourse) {
        setToOneTarget("fromCourse", fromCourse, true);
    }

    public Course getFromCourse() {
        return (Course)readProperty("fromCourse");
    }


    public void setToCourse(Course toCourse) {
        setToOneTarget("toCourse", toCourse, true);
    }

    public Course getToCourse() {
        return (Course)readProperty("toCourse");
    }


}
