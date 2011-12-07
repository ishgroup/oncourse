package ish.oncourse.model;

import ish.oncourse.model.auto._Taggable;
import ish.oncourse.utils.QueueableObjectUtils;

public class Taggable extends _Taggable implements Queueable {
	private static final long serialVersionUID = 6153917245616586209L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}
}
