package ish.oncourse.model;

import ish.oncourse.model.auto._EntityRelation;
import ish.oncourse.utils.QueueableObjectUtils;

public class EntityRelation extends _EntityRelation implements Queueable {

	private static final long serialVersionUID = 7246162209204173840L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
