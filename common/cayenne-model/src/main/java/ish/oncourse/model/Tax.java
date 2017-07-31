package ish.oncourse.model;

import ish.oncourse.model.auto._Tax;
import ish.oncourse.utils.QueueableObjectUtils;

public class Tax extends _Tax implements Queueable {

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
