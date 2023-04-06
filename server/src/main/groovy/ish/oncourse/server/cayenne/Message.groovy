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
class Message extends _Message implements Queueable, ContactActivityTrait {

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
        }
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
    Integer getNumberOfAttempts() {
        return super.getNumberOfAttempts()
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
     * @return the date and time this record was created
     */
    @API
    @Override
    Date getCreatedOn() {
        return super.getCreatedOn()
    }

    @Override
    String getInteractionDescription() {
        return smsText != null ? smsText : emailBody
    }

    @Override
    String getInteractionName() {
        return type.displayName
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
     * @return the SystemUser who created the message
     */
    @Nonnull
    @API
    @Override
    SystemUser getCreatedBy() {
        return super.getCreatedBy()
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
    Date getTimeOfDelivery() {
        return super.getTimeOfDelivery()
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

    @Override
    String getSummaryDescription() {
        String base = getEmailSubject() != null ? getEmailSubject() : getSmsText()
        if(getContact() != null)
            return base + " message to " + getContact().getName()
    }
}
