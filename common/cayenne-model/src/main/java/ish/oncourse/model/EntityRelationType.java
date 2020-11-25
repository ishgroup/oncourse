package ish.oncourse.model;

import ish.oncourse.model.auto._EntityRelationType;
import ish.oncourse.utils.QueueableObjectUtils;

public class EntityRelationType extends _EntityRelationType implements Queueable {
    private static final long serialVersionUID = 2631582158834820283L;

    public final static Long DEFAULT_SYSTEM_TYPE_ID = -1L;


    @Override
    public Long getId() {
        return QueueableObjectUtils.getId(this);
    }

    @Override
    public boolean isAsyncReplicationAllowed() {
        return false;
    }
}
