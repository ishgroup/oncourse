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
import groovy.transform.CompileDynamic
import ish.oncourse.server.PreferenceController
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import javax.mail.Session

@CompileDynamic
class MailSession {

    private static final Logger logger = LogManager.getLogger(MailSession)

    //mail SMTP keys
    private static final String SMTP_HOST = 'mail.smtp.host'
    private static final String SMTP_CONNECTION_TIMEOUT = 'mail.smtp.connectiontimeout'
    private static final String SMTP_IO_TIMEOUT = 'mail.smtp.timeout'
    private static final String SMTP_AUTH = 'mail.smtp.auth'
    private static final String SMTP_START_TLS = 'mail.smtp.starttls.enable'
    private static final String SMTP_PORT = 'mail.smtp.port'

    //mail SMTP values
    private static final int SMTP_CONNECTION_TIMEOUT_VALUE = 300000
    private static final int SMTP_IO_TIMEOUT_VALUE = 300000

    SMTPService smtpService

    @Inject
    MailSession(SMTPService smtpService) {
        this.smtpService = smtpService
    }

    Session getSession() {
        Properties properties = new Properties()
        properties.put(SMTP_CONNECTION_TIMEOUT, SMTP_CONNECTION_TIMEOUT_VALUE)
        properties.put(SMTP_IO_TIMEOUT, SMTP_IO_TIMEOUT_VALUE)
        properties.put(SMTP_HOST, smtpService.host)
        properties.put(SMTP_PORT, smtpService.port)
        properties.put(SMTP_START_TLS, Boolean.TRUE)
        if (authNeed) {
            properties.put(SMTP_AUTH, Boolean.TRUE)
        }

        Session session = Session.getInstance(properties, authNeed ? authenticator : null)
        session.debug = logger.debugEnabled
        session
    }

    private boolean isAuthNeed() {
        StringUtils.isNotBlank(smtpService.userName) && StringUtils.isNotBlank(smtpService.password)
    }

    private Authenticator getAuthenticator() {
        new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpService.userName, smtpService.password)
            }
        }
    }
}
