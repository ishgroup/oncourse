package ish.oncourse.model;

import ish.oncourse.cayenne.IExpandable;
import ish.oncourse.model.auto._SurveyCustomField;

public class SurveyCustomField extends _SurveyCustomField {

    private static final long serialVersionUID = 1L;

    @Override
    public void setRelatedObject(IExpandable relatedObject) {
        setRelatedObject((Survey) relatedObject);
    }

}
