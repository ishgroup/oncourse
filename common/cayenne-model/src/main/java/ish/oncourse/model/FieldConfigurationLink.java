package ish.oncourse.model;

import ish.oncourse.model.auto._FieldConfigurationLink;
import ish.oncourse.utils.QueueableObjectUtils;

public class FieldConfigurationLink extends _FieldConfigurationLink implements Queueable {

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
