/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.function

import groovy.transform.CompileStatic
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.messaging.MailDeliveryParam
import ish.oncourse.server.messaging.MailDeliveryService
import ish.oncourse.server.messaging.MailSession
import ish.oncourse.server.messaging.SMTPService
import org.junit.jupiter.api.Test
import org.mockito.Mockito

import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

import static org.junit.jupiter.api.Assertions.*

@CompileStatic
class UserFunctionsTest {

    private static final String SERVER_ADDRESS = 'https://test.cloud.oncourse.cc'
    private static final String FROM_EMAIL = 'college@example.com'
    private static final String FROM_NAME = 'Test College'
    private static final String TO_EMAIL = 'new.user@example.com'

    @Test
    void sendInvitationEmailToNewSystemUser() {
        SMTPService smtpService = Mockito.mock(SMTPService)
        Mockito.when(smtpService.host).thenReturn('localhost')
        Mockito.when(smtpService.mode).thenReturn(SMTPService.Mode.mock)

        List<MailDeliveryParam> sent = []
        MailDeliveryService mailDeliveryService = new MailDeliveryService(smtpService, new MailSession(smtpService), 'test', null) {
            @Override
            void sendEmail(MailDeliveryParam param) {
                sent << param
                super.sendEmail(param)
            }
        }

        PreferenceController preferenceController = Mockito.mock(PreferenceController)
        Mockito.when(preferenceController.emailFromAddress).thenReturn(FROM_EMAIL)
        Mockito.when(preferenceController.emailFromName).thenReturn(FROM_NAME)

        SystemUser newUser = Mockito.mock(SystemUser)
        Mockito.when(newUser.email).thenReturn(TO_EMAIL)

        String token = UserFunctions.sendInvitationEmailToNewSystemUser(null, newUser, preferenceController, mailDeliveryService, SERVER_ADDRESS, null, null)

        assertEquals(1, sent.size())
        MailDeliveryParam param = sent[0]
        assertEquals('Welcome to onCourse!', param.getSubject.get())
        assertEquals(new InternetAddress(TO_EMAIL), param.getAddressesTO.get()[0])
        assertEquals(new InternetAddress(FROM_EMAIL, FROM_NAME), param.getFrom.get())

        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()))
        message.setContent(param.getContent.get())
        message.saveChanges()

        MimeMultipart multipart = (MimeMultipart) message.content
        assertEquals(1, multipart.count)
        assertTrue(multipart.getBodyPart(0).isMimeType('text/html'))

        String html = multipart.getBodyPart(0).content as String
        String invitationLink = "${SERVER_ADDRESS}/invite/${token}"
        assertTrue(html.contains("<b>Accept your invitation and set your password:</b> <a href=\"${invitationLink}\">${invitationLink}</a>"))
        assertTrue(html.contains("<b>Please note:</b> This invitation will expire in 7 days."))
        assertTrue(html.contains("<a href=\"${SERVER_ADDRESS}\">${SERVER_ADDRESS}</a>"))
    }
}
