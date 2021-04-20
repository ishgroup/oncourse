package ish.oncourse.model;

import ish.oncourse.model.auto._GradingItem;
import ish.oncourse.utils.QueueableObjectUtils;

public class GradingItem extends _GradingItem implements Queueable {

    public Long getId() {
        return QueueableObjectUtils.getId(this);
    }

    @Override
    public boolean isAsyncReplicationAllowed() {
        return true;
    }
}
