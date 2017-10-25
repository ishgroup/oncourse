package ish.oncourse.model;

import ish.oncourse.cayenne.IExpandable;
import ish.oncourse.model.auto._EnrolmentCustomField;

public class EnrolmentCustomField extends _EnrolmentCustomField {

    private static final long serialVersionUID = 1L;

    @Override
    public void setRelatedObject(IExpandable relatedObject) {
        setRelatedObject((Enrolment) relatedObject);
    }
    
    @Override
    public boolean isAsyncReplicationAllowed() {
        return getRelatedObject().isAsyncReplicationAllowed();
    }
}
