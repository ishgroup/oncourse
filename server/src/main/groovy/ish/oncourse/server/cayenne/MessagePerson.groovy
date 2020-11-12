/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import ish.common.types.MessageStatus
import ish.common.types.MessageType
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._MessagePerson
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable
/**
 * When a Message is sent to a Contact, the MessagePerson object represents the specific delivery attempt of each message
 * to each Contact. Because a Message can be delivered to different media at the same time (eg both SMS and email), there
 * might be more than one MessagePerson object joining the same Contact and Message objects.
 */
@API
@QueueableEntity
class MessagePerson extends _MessagePerson implements Queueable {



	private final static Logger logger = LogManager.getLogger()

	boolean hasMessage() {
		return getMessage() != null
	}

	@Override
	void preUpdate() {
		super.preUpdate()
		if (MessageType.EMAIL == getType()) {
			if (MessageStatus.SENT == getStatus()) {
				getContact().setDeliveryStatusEmail(0)
			} else {
				getContact().setDeliveryStatusEmail(getContact().getDeliveryStatusEmail() + 1)
			}
		}
		if (MessageType.SMS == getType()) {
			if (MessageStatus.SENT == getStatus()) {
				getContact().setDeliveryStatusSms(0)
			} else {
				getContact().setDeliveryStatusSms(getContact().getDeliveryStatusSms() + 1)
			}
		}
	}

	/**
	 * Allowed status changes:<br>
	 * <ul>
	 * <li>QUEUED -> SENT/FAILED</li>
	 * <li>SENT/FAILED -> cannot be modified</li>
	 * </ul>
	 *
	 */
	@Override
	void setStatus(@Nullable MessageStatus status) {

		if (getStatus() != null && getStatus() != status) {
			if (status == null) {
				throw new IllegalArgumentException("Can't change status to null.")
			} else if (MessageStatus.FAILED == getStatus() || MessageStatus.SENT == getStatus()) {
				throw new IllegalArgumentException(String.format("Can't change status from %s to %s", getStatus(), status))
			}
		}

		super.setStatus(status)
	}

	/**
	 * @return the number of times we attempted to deliver the message
	 */
	@Nonnull
	@API
	@Override
	Integer getAttemptCount() {
		return super.getAttemptCount()
	}

	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * This field will be null for postal messages, contain a mobile phone number for SMS or email address for email messages
	 *
	 * @return a destination address
	 */
	@Nonnull
	@API
	@Override
	String getDestinationAddress() {
		return super.getDestinationAddress()
	}


	/**
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return the response from the mail server or SMS gateway, if any
	 */
	@API
	@Override
	String getResponse() {
		return super.getResponse()
	}

	/**
	 * @return the time and date on which the message was delivered
	 */
	@API
	@Override
	Date getSentOn() {
		return super.getSentOn()
	}

	/**
	 * @return the delivery status of this message
	 */
	@Nonnull
	@API
	@Override
	MessageStatus getStatus() {
		return super.getStatus()
	}

	/**
	 * @return a type representing email, SMS or post
	 */
	@Nonnull
	@API
	@Override
	MessageType getType() {
		return super.getType()
	}



	/**
	 * @return the contact to whom the message will be delivered
	 */
	@Nonnull
	@API
	@Override
	Contact getContact() {
		return super.getContact()
	}

	/**
	 * @return the message to deliver
	 */
	@Nonnull
	@API
	@Override
	Message getMessage() {
		return super.getMessage()
	}

	@Override
	String getSummaryDescription() {
		if(getContact() == null) {
			return getType().getDisplayName()
		}
		return getType().getDisplayName() + " message to " + getContact().getName()
	}
}
