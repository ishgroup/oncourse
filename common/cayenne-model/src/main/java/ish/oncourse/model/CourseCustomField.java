package ish.oncourse.model;

import ish.oncourse.cayenne.IExpandable;
import ish.oncourse.model.auto._CourseCustomField;

public class CourseCustomField extends _CourseCustomField {

    private static final long serialVersionUID = 1L;

    @Override
    public String getRelatedEntityName() {
        return Course.class.getSimpleName();
    }

    @Override
    public void setRelatedObject(IExpandable relatedObject) {
        setRelatedObject((Course) relatedObject);
    }
}
