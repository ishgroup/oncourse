package ish.oncourse.model;

import ish.common.types.MessageStatus;
import ish.common.types.MessageType;
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
	public void setStatus(final MessageStatus status) {
		if (getStatus() != null && MessageType.SMS.equals(getType())) {
			switch (getStatus()) {
			case SENT:
				if (!MessageStatus.SENT.equals(status)) {
					throw new IllegalStateException("We should not allow to change status from SENT to any other state! But try to change it to " + status);
				}
				break;
			default:
				break;
			}
		}
		super.setStatus(status);
	}
	
	
}
