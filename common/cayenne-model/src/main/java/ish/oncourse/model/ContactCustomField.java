package ish.oncourse.model;

import ish.oncourse.cayenne.IExpandable;
import ish.oncourse.model.auto._ContactCustomField;

public class ContactCustomField extends _ContactCustomField {

    private static final long serialVersionUID = 1L;

    @Override
    public String getEntityIdentifier() {
        return Contact.class.getName();
    }

    @Override
    public void setRelatedObject(IExpandable relatedObject) {
        setRelatedObject((Contact) relatedObject);
    }

}
