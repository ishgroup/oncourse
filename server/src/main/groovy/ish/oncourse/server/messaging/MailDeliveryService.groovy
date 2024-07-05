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

package ish.oncourse.server.messaging

import com.google.inject.Inject
import com.google.inject.name.Named
import com.sun.mail.smtp.SMTPMessage
import groovy.transform.CompileDynamic
import ish.oncourse.server.AngelModule
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Contact
import org.apache.cayenne.query.ObjectSelect

import static javax.mail.Message.RecipientType
import org.apache.commons.lang3.StringUtils

import javax.mail.MessagingException
import javax.mail.Transport

@CompileDynamic
class MailDeliveryService {

    private static final String EMAIL_HEADER = 'X-Mailer'

    SMTPService smtpService
    MailSession mailSession
    String angelVersion
    ICayenneService cayenneService
    UnsubscribeService unsubscribeService

    @Inject
    MailDeliveryService(SMTPService smtpService,
                        MailSession mailSession,
                        ICayenneService cayenneService,
                        UnsubscribeService unsubscribeService,
                        @Named(AngelModule.ANGEL_VERSION) String angelVersion) {
        this.smtpService = smtpService
        this.mailSession = mailSession
        this.angelVersion = angelVersion
        this.cayenneService = cayenneService
        this.unsubscribeService = unsubscribeService
    }

    void sendEmail(MailDeliveryParam param) throws MessagingException {
        if (StringUtils.isBlank(smtpService.getHost())) {
            throw new IllegalStateException('smtp server has to be specified')
        }

        SMTPMessage message = new SMTPMessage(mailSession.session)

        param.getFrom.get() ? message.from = param.getFrom.get() : message.setFrom()
        message.envelopeFrom = param.getEnvelopeFrom.get()

        def toAddresses = param.getAddressesTO.get()
        def ccAddresses = param.getAddressesCC.get()
        def bccAddresses = param.getAddressesBCC.get()

        if (toAddresses.length + ccAddresses.length + bccAddresses.length > mailSession.smtpService.email_batch)
            throw new IllegalArgumentException("Number of recipients was more, than max allowed email batch, so message cannot be sent. Contact ish support")

        message.setRecipients(RecipientType.CC, ccAddresses)
        message.setRecipients(RecipientType.BCC, bccAddresses)
        message.subject = param.getSubject.get()
        message.setHeader(EMAIL_HEADER, "onCourse ${angelVersion}".toString())
        message.sentDate = param.sentDate

        for(def address: toAddresses) {
            message.setRecipients(RecipientType.TO, address)
            var contacts = ObjectSelect.query(Contact.class).where(Contact.EMAIL.eq(address.toString()))
                    .select(cayenneService.newContext)
            if(contacts.stream().any {it.isUndeliverable})
                continue

            def content = param.getContent.get()
            def unsubscribeLink = unsubscribeService.generateLinkFor(address.toString())
            message.content = content + "\n\nPress to unsubscribe from all messages: $unsubscribeLink"

            if (SMTPService.Mode.mock != smtpService.mode) {
                Transport.send(message)
            }
        }
    }
}
