package ish.oncourse.model;

import ish.oncourse.cayenne.IExpandable;
import ish.oncourse.model.auto._MembershipCustomField;

public class MembershipCustomField extends _MembershipCustomField {
    @Override
    public void setRelatedObject(IExpandable relatedObject) {
        setRelatedObject((Membership) relatedObject);
    }
}
