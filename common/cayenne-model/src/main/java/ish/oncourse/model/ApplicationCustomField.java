package ish.oncourse.model;

import ish.oncourse.cayenne.IExpandable;
import ish.oncourse.model.auto._ApplicationCustomField;

public class ApplicationCustomField extends _ApplicationCustomField {

    private static final long serialVersionUID = 1L;

    @Override
    public void setRelatedObject(IExpandable relatedObject) {
        setRelatedObject((Application) relatedObject);
    }
}
