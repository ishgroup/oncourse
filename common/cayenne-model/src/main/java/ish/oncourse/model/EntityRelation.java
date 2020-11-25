package ish.oncourse.model;

import ish.oncourse.model.auto._EntityRelation;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.query.SelectById;

public class EntityRelation extends _EntityRelation implements Queueable {

	private static final long serialVersionUID = 7246162209204173840L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}

	@Override
	protected void onPrePersist() {
		if (super.getRelationType() == null) {
			super.setRelationType(SelectById.query(EntityRelationType.class, EntityRelationType.DEFAULT_SYSTEM_TYPE_ID).selectOne(objectContext));
		}
	}
}
