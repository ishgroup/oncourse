package ish.oncourse.services.mail

import ish.oncourse.configuration.Configuration
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.Transport

import static ish.oncourse.configuration.Configuration.AppProperty.SMTP

class SendEmail {
    
    private final static Logger logger = LogManager.logger
    private EmailBuilder builder
    private boolean asynchronous
    
    private SendEmail() {}
    
    static SendEmail valueOf(EmailBuilder builder, boolean asynchronous) {
        SendEmail sendEmail = new SendEmail()
        sendEmail.builder = builder
        sendEmail.asynchronous = asynchronous
        return sendEmail
    }

    boolean send() {
        final Message message
        try {
            Session session = getSession()
            message = builder.toMessage(session)
        } catch (MessagingException e) {
            logger.warn("Failed to prepare message", e);
            return false
        }

        if (asynchronous) {
            Runnable r = new Runnable() {
                void run() {
                    doSend(message)
                }
            }

            Thread mailThread = new Thread(r, 'email')
            mailThread.daemon = true
            mailThread.start()
            return true

        } else {
            return doSend(message)
        }
    }

    private Session getSession() {
        if (!Configuration.getValue(SMTP)) {
            logger.error('SMTP host is not defined!')
        }
        return Session.getDefaultInstance(System.properties, null)
    }

    private boolean doSend(Message message) {
        try {
            Transport.send(message)
            logger.debug('Email sent successfully')
            return true
        } catch (MessagingException e) {
            logger.warn('Error sending email.', e)
            return false
        }
    }
}
