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

import javax.mail.internet.InternetAddress

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
    private PreferenceController preferenceController

    @Inject
    MailDeliveryService(SMTPService smtpService,
                        MailSession mailSession,
                        @Named(AngelModule.ANGEL_VERSION) String angelVersion,
                        PreferenceController preferenceController) {
        this.smtpService = smtpService
        this.mailSession = mailSession
        this.angelVersion = angelVersion
        this.preferenceController = preferenceController
    }

    void sendEmail(MailDeliveryParam param) throws MessagingException {
        if (StringUtils.isBlank(smtpService.getHost())) {
            throw new IllegalStateException('smtp server has to be specified')
        }

        SMTPMessage message = new SMTPMessage(mailSession.session)

        if(param.getFrom.get() == null) {
            def emailFrom = preferenceController.getEmailFromAddress()
            if(emailFrom == null)
                throw new IllegalArgumentException("email.from preference must be specified for scripts message sending")

            String emailDomain = emailFrom.substring(emailFrom.lastIndexOf("@") + 1)
            message.from = new InternetAddress(MailSession.SMTP_DEFAULT_USER_NAME_VALUE+"@"+emailDomain)
        } else
            message.from = param.getFrom.get()

        message.envelopeFrom = param.getEnvelopeFrom.get()
        message.setRecipients(RecipientType.TO, param.getAddressesTO.get())
        message.setRecipients(RecipientType.CC, param.getAddressesCC.get())
        message.setRecipients(RecipientType.BCC, param.getAddressesBCC.get())
        message.subject = param.getSubject.get()
        message.setHeader(EMAIL_HEADER, "onCourse ${angelVersion}".toString())
        message.sentDate = param.sentDate
        message.content = param.getContent.get()
        
        if (SMTPService.Mode.mock != smtpService.mode) {
            Transport.send(message)
        }
    }
}
