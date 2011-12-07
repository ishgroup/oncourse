package ish.oncourse.model;

import ish.oncourse.model.auto._Discussion;
import ish.oncourse.utils.QueueableObjectUtils;

public class Discussion extends _Discussion {
	private static final long serialVersionUID = -470905275802782735L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}
}
