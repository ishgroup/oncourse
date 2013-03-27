package ish.oncourse.model;

import ish.common.types.MessageStatus;
import ish.oncourse.model.auto._MessagePerson;
import ish.oncourse.utils.QueueableObjectUtils;

public class MessagePerson extends _MessagePerson implements Queueable {
	private static final long serialVersionUID = -6078394599873247812L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	/** 
	 * {@inheritDoc}
	 */
	@Override
	public void setStatus(MessageStatus status) {
		if (getStatus() == null) {
			//nothing to check
		} else {
			switch (getStatus()) {
			case QUEUED:
				if (status == null) {
					throw new IllegalArgumentException("Can't update to empty MessagePerson status from queued!");
				}
				break;
			case SENT:
			case FAILED:
				if (!(getStatus().equals(status))) {
					throw new IllegalArgumentException(String.format("Can't set the %s status for MessagePerson with %s status!", status, getStatus()));
				}
				break;
			default:
				throw new IllegalArgumentException(String.format("Unsupported status %s found for MessagePerson", getStatus()));
			}
		}
		super.setStatus(status);
	}
	
}
