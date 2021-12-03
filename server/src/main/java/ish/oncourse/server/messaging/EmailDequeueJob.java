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

package ish.oncourse.server.messaging;

import com.google.inject.Inject;
import com.sun.mail.smtp.SMTPAddressFailedException;
import ish.common.types.MessageStatus;
import ish.common.types.MessageType;
import ish.oncourse.server.AngelServerFactory;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.PreferenceController;
import ish.oncourse.server.cayenne.Contact;
import ish.oncourse.server.cayenne.Message;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.SendFailedException;
import javax.mail.internet.AddressException;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@DisallowConcurrentExecution
public class EmailDequeueJob implements Job {

	private static final Logger logger = LogManager.getLogger(EmailDequeueJob.class);

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private SMTPService smtpService;

	@Inject
	private PreferenceController preferenceController;

	@Inject
	private MailDeliveryService mailDeliveryService;

	public EmailDequeueJob() {
		super();
	}

	public MessageStatus sendMessageToRecipient(final Message message, final Contact aReceiver, final StringBuffer theResponse) {
		MessageStatus returnStatus;

		try {
			var getFrom = GetFrom.valueOf(message.getEmailFrom(), preferenceController.getEmailFromName());
			var getEnvelopeFrom = GetEnvelopeFrom.valueOf(message.getEmailFrom(), preferenceController.getEmailBounceEnabled(), preferenceController.getEmailBounceAddress(), message.getId().toString());
			var getAddressesTO = GetAddresses.valueOf(message);
			var getSubject = GetSubject.valueOf(message.getEmailSubject());
			var getEmailPlainBody = GetEmailPlainBody.valueOf(message);
			var getEmailHtmlBody = GetEmailHtmlBody.valueOf(message);
			var getContent = GetContent.valueOf(getEmailPlainBody, getEmailHtmlBody);

			var mailDeliveryParam = MailDeliveryParam.valueOf(getFrom, getEnvelopeFrom, getAddressesTO, getSubject, getContent);

			mailDeliveryService.sendEmail(mailDeliveryParam);

			logger.debug("successfully sent message");
			returnStatus =  MessageStatus.SENT;

		} catch (final SendFailedException e) {
			if (e.getCause() instanceof SMTPAddressFailedException && ((SMTPAddressFailedException)e.getCause()).getReturnCode() == 551) {
				returnStatus = MessageStatus.FAILED;
				logger.error("SMTPAddressFailedException(551) occured");
				logger.catching(e);
				theResponse.append(e.getMessage() + String.format(". Email address: %s", aReceiver.getEmail()));
			} else {
				//requeued message if any configuration exception occurs - authentication is required for SMTP SERVER
				//if recipient's mail box just not exist - SendFailedException occurs (depends on smtp relay configuratio)
				returnStatus = MessageStatus.FAILED;
				logger.error("SendFailedException occured");
				logger.catching(e);
				theResponse.append("Send failed exception occured. ");
				theResponse.append(e.getMessage());
			}
		} catch (final AddressException e) {
			// should we allow this to be sent when and if the
			// recipient address is fixed?
			returnStatus = MessageStatus.FAILED;
			logger.error("AddressException occured");
			logger.catching(e);
			theResponse.append("Invalid email address or email setup.");
		} catch (final MessagingException e) {
			// connection is either dead or
			// the underlying message cannot be properly constructed
			// we could try again later.
			returnStatus = MessageStatus.QUEUED;
			logger.error("Messaging exception occured");
			logger.catching(e);
			theResponse.append("Messaging exception occured. ");
			theResponse.append(e.getMessage());
		} catch (final Exception e) {
			// java.lang.IllegalStateException,
			// javax.mail.IllegalWriteException,
			returnStatus = MessageStatus.QUEUED;
			logger.error("Email delivery failed, target email :{} using mail server :{}",
					aReceiver, smtpService.getHost(), e);
			logger.catching(e);
			theResponse.append("Exception occured. ");
			theResponse.append(e.getMessage());
		}
		return returnStatus;
	}

	/**
	 * This method is called for receiving and processing bounce mails using VERP. If a email was bounced this method sets the status of the original sent
	 * message to failed, increments the number of bounces for that contact email address and sends a notifications email to the original sender.
	 */
	public void receiveBounceMessages() {
		try {

			var bounceInfo = MailDelivery.receiveBouncedEmailsVERP();

			// primary keys of MessagePerson
			Collection<Long> ids = bounceInfo.keySet();

			var query = SelectQuery.query(Message.class, Message.ID.in(ids));
			final ObjectContext context = this.cayenneService.getNewContext();

			for (var messPers : context.select(query)) {

				/*
				 * 1. mark original message as failed / undelivered
				 */
				messPers.setStatus(MessageStatus.FAILED);

				/*
				 * 2. increment number of bounces for that contact email address
				 */
				var contact = messPers.getContact();
				var delStat = contact.getDeliveryStatusEmail();
				if (delStat == null) {
					contact.setDeliveryStatusEmail(1);
				} else {
					contact.setDeliveryStatusEmail(delStat + 1);
				}

				/*
				 * 3. notify the person who sent the original email
				 */

				var getFrom = GetFrom.valueOf(preferenceController.getEmailBounceAddress());

				var getEnvelopeFrom = GetEnvelopeFrom.valueOf(preferenceController.getEmailBounceAddress());

				var getAddressesTO = GetAddresses.valueOf(messPers.getEmailFrom());

				var subject = String.format("\"%s\" was bounced", messPers.getEmailSubject());
				var getSubject = GetSubject.valueOf(subject);

				var body = String.format("This message was unable to be delivered to the recipient. onCourse has already noted it as undeliverable " +
						"against this recipient. Please check to see if any action is required on your part. " +
						"\n \n ----------------------------------------------------------------------- \n \n%s", bounceInfo.get(messPers.getId()));
				var getEmailPlainBody = GetEmailPlainBody.valueOf(body);
				var getContent = GetContent.valueOf(getEmailPlainBody);

				var mailDeliveryParam = MailDeliveryParam.valueOf(getFrom, getEnvelopeFrom, getAddressesTO, getSubject, getContent);

				mailDeliveryService.sendEmail(mailDeliveryParam);
			}

			context.commitChanges();

		} catch (NoSuchProviderException e) {
			logger.debug("Error processing bounce mail, mail provider does not exist ", e);
		} catch (AuthenticationFailedException e) {
			logger.debug("Error processing bounce mail. Authentication failure: bad user name or password ", e);
		} catch (MessagingException e) {
			logger.debug("Error processing bounce mail, cannot read message", e);
		} catch (IOException e) {
			logger.debug("Error processing bounce mail, cannot get content of message: ", e);
		}

	}

	/**
	 * @return the list of currently queued MessagePerson's
	 */
	protected List<Message> currentQueue(Expression dequeueingQualifier, final DataContext context) {
		var aQuery = SelectQuery.query(Message.class, dequeueingQualifier);
		aQuery.setCacheStrategy(QueryCacheStrategy.NO_CACHE);
		aQuery.setPageSize(25);
		aQuery.setFetchLimit(500);

		return context.select(aQuery);
	}

	/**
	 * Implementation of the Job interface. Fetches the current queue of messages and passes each message to sendMessageToRecipient( Message, MessagingReceiver,
	 * StringBuffer ).
	 *
	 * @throws JobExecutionException
	 */
	@Override
	public synchronized void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			logger.debug("email dequeue process started");

			if (StringUtils.isBlank(smtpService.getHost())) {
				logger.debug("no smtp host specified, service wont continue");
				return;
			}

			var dequeueingQualifier = ExpressionFactory.matchExp(Message.TYPE.getName(), MessageType.EMAIL);
			dequeueingQualifier = dequeueingQualifier.andExp(ExpressionFactory.matchExp(Message.STATUS.getName(), MessageStatus.QUEUED));

			final var aContext = this.cayenneService.getNewContext();
			var messages = currentQueue(dequeueingQualifier, aContext);

			if (messages.size() > 0) {

				var count = messages.size();

				for (var i = 0; i < count && !AngelServerFactory.QUIT_SIGNAL_CAUGHT; i++) {
					var message = messages.get(i);
					if (MessageStatus.QUEUED.equals(message.getStatus())) {
						var aReceiver = message.getContact();

						if (aReceiver != null) {
							var responseBuffer = new StringBuffer();

							MessageStatus newStatus = null;
							try {
								newStatus = sendMessageToRecipient(message, aReceiver, responseBuffer);
								logger.debug("setting status:{}", newStatus);

							} catch (IllegalStateException e) {
								logger.error("failed to send message", e);
							}

							if (newStatus != null) {
								try {
									message.setStatus(newStatus);
									message.setResponse(responseBuffer.toString());
									if (MessageStatus.SENT.equals(newStatus)) {
										message.setTimeOfDelivery(new Date());
									}
									aContext.commitChanges();
								} catch (Exception e) {
									logger.error("Failed to update message status to:{}", newStatus, e);
								}
							}
						}
					}
				}
			}
		} catch (Exception t) {
			logger.warn("Email dequequeing process failed", t);
		}

		try {
			if (preferenceController.getEmailBounceEnabled()) {
				receiveBounceMessages();
			}
		} catch (Exception e) {}
		logger.debug("email dequeue process finished");
	}
}
