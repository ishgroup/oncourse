package ish.oncourse.model.auto;

import org.apache.cayenne.exp.Property;

import ish.oncourse.model.Course;
import ish.oncourse.model.CustomField;

/**
 * Class _CourseCustomField was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _CourseCustomField extends CustomField {

    private static final long serialVersionUID = 1L; 

    public static final String RELATED_OBJECT_PROPERTY = "relatedObject";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Course> RELATED_OBJECT = new Property<Course>("relatedObject");

    public void setRelatedObject(Course relatedObject) {
        setToOneTarget("relatedObject", relatedObject, true);
    }

    public Course getRelatedObject() {
        return (Course)readProperty("relatedObject");
    }


}
