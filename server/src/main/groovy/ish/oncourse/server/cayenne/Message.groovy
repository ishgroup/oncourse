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


import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._Message
import ish.validation.ValidationFailure
import org.apache.cayenne.validation.ValidationResult
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable
/**
 * A message sent to a Contact record can represent an SMS, email or postal document. A Message might be sent as email, SMS or post all at the same time and be delivered by two or more of those means to the same users. It is possible for a Message to
 * sent to a single Contact or to thousands.
 *
 * Email and SMS messages are queued on the onCourse server and delivered in the background in a batch process every minute
 * or so.
 */
@API
@QueueableEntity
class Message extends _Message implements Queueable {

	public static final String IS_EMAIL_PROPERTY = "isEmail"
	public static final String IS_SMS_PROPERTY = "isSms"
	public static final String IS_POST_PROPERTY = "isPost"
	public static final String RECIPIENTS_STRING_PROPERTY = "recipientsString"


	private static final Logger logger = LogManager.getLogger()

	@Override
	void validateForSave(@Nonnull ValidationResult result) {
		super.validateForSave(result)

		// at least one of the fields have to be filled in
		if ((getEmailSubject() == null || getEmailSubject().isEmpty() || (getEmailBody() == null || getEmailBody().isEmpty()) &&
				(getEmailHtmlBody() == null || getEmailHtmlBody().isEmpty())) &&
				(getSmsText() == null || getSmsText().isEmpty()) && (getPostDescription() == null || getPostDescription().isEmpty())) {

			if (getEmailSubject() == null || getEmailSubject().isEmpty()) {
				result.addFailure(ValidationFailure.validationFailure(this, EMAIL_SUBJECT.getName(), "The email subject cannot be blank."))
			}
			if ((getEmailBody() == null || getEmailBody().isEmpty()) && (getEmailHtmlBody() == null || getEmailHtmlBody().isEmpty())) {
				result.addFailure(ValidationFailure.validationFailure(this, EMAIL_BODY.getName(), "The email message cannot be blank."))
				result.addFailure(ValidationFailure.validationFailure(this, EMAIL_HTML_BODY.getName(), "The email message cannot be blank."))
			}
			if (getSmsText() == null || getSmsText().isEmpty()) {
				result.addFailure(ValidationFailure.validationFailure(this, SMS_TEXT.getName(), "The sms text cannot be blank."))
			}
			if (getPostDescription() == null || getPostDescription().isEmpty()) {
				result.addFailure(ValidationFailure.validationFailure(this, POST_DESCRIPTION.getName(), "The post description cannot be blank."))
			}
		}
	}

	/**
	 *
	 * @return
	 */
	boolean hasMessagePerson() {
		return getMessagePersons() != null && !getMessagePersons().isEmpty()
	}

	/**
	 * @return true if message linked to mailing list which has willow id
	 */
	boolean isMailingListMessage() {
		if (!getTaggingRelations().isEmpty()) {
			MessageTagRelation tagRelation = getTaggingRelations().get(0)
			return tagRelation != null && tagRelation.getTag() != null && tagRelation.getTag().getWillowId() != null
		}
		return false
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
	 * If this message is an email, this returns the plaintext body. Although it is possible to send an email with an
	 * html part only, it is recommended that you always send a plaintext part as well in order to support all users
	 * with different devices.
	 *
	 * @return plaintext message
	 */
	@API
	@Override
	String getEmailBody() {
		return super.getEmailBody()
	}

	/**
	 * @return a properly formatted email address
	 */
	@API
	@Override
	String getEmailFrom() {
		return super.getEmailFrom()
	}

	/**
	 * If this message is an email, this returns the html body. It is possible for this part to be null or empty even if
	 * the message is an email, in which case only the plaintext message will be sent.
	 *
	 * @return html message
	 */
	@API
	@Override
	String getEmailHtmlBody() {
		return super.getEmailHtmlBody()
	}

	/**
	 * If this message is an email, this must contain a not empty subject string
	 *
	 * @return subject
	 */
	@API
	@Override
	String getEmailSubject() {
		return super.getEmailSubject()
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
	 * If this message is sent by snail mail (post), this contains a description of what was sent
	 *
	 * @return a human entered description of the message
	 */
	@API
	@Override
	String getPostDescription() {
		return super.getPostDescription()
	}

	/**
	 * If this message is an SMS, this contains the text. If the text is longer than a standard SMS length, it will be
	 * delivered as two or more SMS messages, incurring additional charges
	 *
	 * @return SMS content
	 */
	@API
	@Override
	String getSmsText() {
		return super.getSmsText()
	}

	/**
	 * If some contacts don't have email addresses or mobile phone numbers at the time of creation, then some messages
	 * may fail to be delivered. If the Message contains two or more of SMS, email or postal components then some of the
	 * Contacts in this list may only receive some those components. For example, some contacts may have a mobile phone
	 * number and not an email; others have email and no phone, and others have both.
	 *
	 * @return the list of contacts to whom the message is sent
	 */
	@Nonnull
	@API
	@Override
	List<Contact> getContacts() {
		return super.getContacts()
	}

	/**
	 * @return the SystemUser who created the message
	 */
	@Nonnull
	@API
	@Override
	SystemUser getCreatedBy() {
		return super.getCreatedBy()
	}

	/**
	 * @return the intermediate table linking to Contacts
	 */
	@Nonnull
	@API
	@Override
	List<MessagePerson> getMessagePersons() {
		return super.getMessagePersons()
	}

	/**
	 * @return The list of tags assigned to message
	 */
	@Nonnull
	@API
	List<Tag> getTags() {
		List<Tag> tagList = new ArrayList<>(getTaggingRelations().size())
		for (MessageTagRelation relation : getTaggingRelations()) {
			tagList.add(relation.getTag())
		}
		return tagList
	}

	/**
	 * @return specific key of the message. It can be set in scripts.
	 */
	@API
	@Nullable
	@Override
	String getCreatorKey() {
		return super.getCreatorKey()
	}

	@Override
	String getSummaryDescription() {
		if (getEmailSubject() != null) {
			return getEmailSubject()
		}
		return getSmsText()
	}
}
