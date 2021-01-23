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
import ish.oncourse.server.PreferenceController
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

    @Inject
    MailDeliveryService(SMTPService smtpService,
                        MailSession mailSession,
                        @Named(AngelModule.ANGEL_VERSION) String angelVersion) {
        this.smtpService = smtpService
        this.mailSession = mailSession
        this.angelVersion = angelVersion
    }

    void sendEmail(MailDeliveryParam param) throws MessagingException {
        if (StringUtils.isBlank(smtpService.getHost())) {
            throw new IllegalStateException('smtp server has to be specified')
        }

        SMTPMessage message = new SMTPMessage(mailSession.session)

        param.getFrom.get() ? message.from = param.getFrom.get() : message.setFrom()
        message.envelopeFrom = param.getEnvelopeFrom.get()
        message.setRecipients(RecipientType.TO, param.getAddressesTO.get())
        message.setRecipients(RecipientType.CC, param.getAddressesCC.get())
        message.setRecipients(RecipientType.BCC, param.getAddressesBCC.get())
        message.subject = param.getSubject.get()
        message.setHeader(EMAIL_HEADER, "onCourse ${angelVersion}".toString())
        message.sentDate = param.sentDate
        message.content = param.getContent.get()

        Transport.send(message)
    }
}
