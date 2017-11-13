package ish.oncourse.model;

import ish.oncourse.model.auto._SurveyType;
import ish.oncourse.utils.QueueableObjectUtils;

public class SurveyType extends _SurveyType implements Queueable {

    private static final long serialVersionUID = 1L;

    @Override
    public Long getId() {
        return QueueableObjectUtils.getId(this);
    }

    @Override
    public boolean isAsyncReplicationAllowed() {
        return true;
    }
}
