package ish.oncourse.model.auto;

import ish.oncourse.model.Course;
import ish.oncourse.model.EntityRelation;
import org.apache.cayenne.exp.Property;

/**
 * Class _CourseCourseRelation was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _CourseCourseRelation extends EntityRelation {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String FROM_COURSE_PROPERTY = "fromCourse";
    @Deprecated
    public static final String TO_COURSE_PROPERTY = "toCourse";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Course> FROM_COURSE = new Property<Course>("fromCourse");
    public static final Property<Course> TO_COURSE = new Property<Course>("toCourse");

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
