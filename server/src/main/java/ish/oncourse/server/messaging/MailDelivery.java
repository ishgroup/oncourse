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

import ish.oncourse.server.PreferenceController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This class provides functionality for sending and receiving emails.
 */
public class MailDelivery {

	private static final Logger logger = LogManager.getLogger();

	//mail POP3 keys
	private static final String POP3_PROTOCOL = "pop3";
	private static final String POP3_HOST = "mail.pop3.host";
	private static final String POP3_USER = "mail.pop3.user";
	private static final String POP3_PASSWORD = "mail.pop3.password";
	private static final String POP3_PORT = "mail.pop3.port";
	//mail POP3 values
	private static final String POP3_PORT_VALUE = "110";

	protected MailDelivery() {}

	/**
	 * This method receives all bounce mails using POP3. All bounce mail must have a different TO address which is achieved by originally sending out emails
	 * with different envelope sender addresses (using VERP).
	 *
	 * @return all MessagePerson ids and their bounce messages
	 */
	public static Map<Long, Object> receiveBouncedEmailsVERP() throws MessagingException, IOException {

		// Map<MessagePerson id, bounce message> of all bounce mails
		Map<Long, Object> bounceInfo = new HashMap<>();

		var session = initPOP3Session();

		var store = session.getStore(POP3_PROTOCOL);
		store.connect();

		var folder = store.getFolder("INBOX");
		folder.open(Folder.READ_WRITE);

		var message = folder.getMessages();
		for (var bounceMail : message) {
			var address = bounceMail.getRecipients(Message.RecipientType.TO);
			if (address.length > 0) {
				if (address[0].getType().equalsIgnoreCase("rfc822")) {
					var addr = ((InternetAddress) address[0]).getAddress();
					var plus = addr.indexOf('+');
					var at = addr.indexOf('@');
					if (plus > -1 && plus < at) {
						bounceInfo.put(new Long(addr.substring(plus + 1, at)), bounceMail.getContent());
					}
				}
			}

			// delete fetched mails on server
			bounceMail.setFlag(Flags.Flag.DELETED, true);
		}

		folder.close(true);
		store.close();

		return bounceInfo;
	}

	private static Session initPOP3Session() {
		var mailServer = PreferenceController.getController().getEmailPOP3Host();
		var account = PreferenceController.getController().getEmailPOP3Account();
		var password = PreferenceController.getController().getEmailPOP3Password();

		logger.debug("receive bounced mails using VERP on {} ,Port 110", mailServer);

		final var props = new Properties();
		props.setProperty(POP3_HOST, mailServer);
		props.setProperty(POP3_USER, account);
		props.setProperty(POP3_PASSWORD, password);
		props.setProperty(POP3_PORT, POP3_PORT_VALUE);

		return Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(props.getProperty(POP3_USER), props.getProperty(POP3_PASSWORD));
			}
		});


	}
}
