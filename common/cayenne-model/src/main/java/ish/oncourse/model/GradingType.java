package ish.oncourse.model;

import ish.oncourse.model.auto._GradingType;
import ish.oncourse.utils.QueueableObjectUtils;

public class GradingType extends _GradingType implements Queueable {

    public Long getId() {
        return QueueableObjectUtils.getId(this);
    }

    @Override
    public boolean isAsyncReplicationAllowed() {
        return true;
    }
}
