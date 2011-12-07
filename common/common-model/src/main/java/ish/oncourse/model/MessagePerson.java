package ish.oncourse.model;

import ish.oncourse.model.auto._MessagePerson;
import ish.oncourse.utils.QueueableObjectUtils;

public class MessagePerson extends _MessagePerson implements Queueable {
	private static final long serialVersionUID = -6078394599873247812L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}
}
