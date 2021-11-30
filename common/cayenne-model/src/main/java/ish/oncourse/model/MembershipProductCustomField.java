package ish.oncourse.model;

import ish.oncourse.cayenne.IExpandable;
import ish.oncourse.model.auto._MembershipProductCustomField;

public class MembershipProductCustomField extends _MembershipProductCustomField {
    @Override
    public void setRelatedObject(IExpandable relatedObject) {
        setRelatedObject((MembershipProduct) relatedObject);
    }
}
