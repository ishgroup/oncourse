package ish.oncourse.model;

import ish.oncourse.cayenne.IExpandable;
import ish.oncourse.model.auto._CourseClassCustomField;

public class CourseClassCustomField extends _CourseClassCustomField {

    private static final long serialVersionUID = 1L;

    @Override
    public void setRelatedObject(IExpandable relatedObject) {
        setRelatedObject((CourseClass) relatedObject);
    }
}
