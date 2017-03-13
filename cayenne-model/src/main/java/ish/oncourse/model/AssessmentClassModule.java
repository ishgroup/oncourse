package ish.oncourse.model;

import ish.oncourse.cayenne.AssessmentClassModuleInterface;
import ish.oncourse.model.auto._AssessmentClassModule;
import ish.oncourse.utils.QueueableObjectUtils;

public class AssessmentClassModule extends _AssessmentClassModule implements Queueable, AssessmentClassModuleInterface {

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
