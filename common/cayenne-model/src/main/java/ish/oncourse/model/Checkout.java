package ish.oncourse.model;

import ish.oncourse.model.auto._Checkout;
import ish.oncourse.utils.QueueableObjectUtils;

public class Checkout extends _Checkout implements Queueable {

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
