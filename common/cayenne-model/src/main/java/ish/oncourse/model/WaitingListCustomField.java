package ish.oncourse.model;

import ish.oncourse.cayenne.IExpandable;
import ish.oncourse.model.auto._WaitingListCustomField;

public class WaitingListCustomField extends _WaitingListCustomField {

    private static final long serialVersionUID = 1L;

    @Override
    public void setRelatedObject(IExpandable relatedObject) {
        setRelatedObject((WaitingList) relatedObject);
    }
}
