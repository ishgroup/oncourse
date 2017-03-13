package ish.oncourse.model;

import ish.oncourse.model.auto._QueuedStatistic;
import ish.oncourse.utils.QueueableObjectUtils;

import java.util.Date;

public class QueuedStatistic extends _QueuedStatistic implements Queueable {

	private static final long serialVersionUID = 3739064287130031898L;

	@Override
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public void setAngelId(Long angelId) {}

	@Override
	public Long getAngelId() {
		return null;
	}

	@Override
	public void setCreated(Date created) {}

	@Override
	public void setModified(Date modified) {}

	@Override
	public Date getCreated() {
		return null;
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
