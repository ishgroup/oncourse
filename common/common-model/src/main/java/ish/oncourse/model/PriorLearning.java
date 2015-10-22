package ish.oncourse.model;

import ish.oncourse.model.auto._PriorLearning;
import ish.oncourse.utils.QueueableObjectUtils;

public class PriorLearning extends _PriorLearning  implements Queueable {

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
