package ish.oncourse.server.messaging

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.MessagePerson
import org.junit.Ignore
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito

import javax.mail.MessagingException

/**
 * Manual test of sending email through our implementation
 */
@CompileStatic
class MailDeliveryServiceTest {

    //need to set all these parameters(could be extended with additional parameters)
    private static final String SMTP_HOST = 'smtp.gmail.com'
    private static final String SMTP_USERNAME = 'example@mail.com'
    private static final String SMTP_PASSWORD = 'example'
    private static final boolean SMTP_START_TLS = true
    private static final int SMTP_PORT = 25

    private static final String ANGEL_VERSION = 'test_development'

    private static final String TO_EMAIL = 'example@mail.com'
    private static final boolean IS_COMPANY = true
    private static final String TO_PERSONAL_LASTNAME = 'Higginson , and Higginsons'
    private static final String TO_PERSONAL_FIRSTNAME = 'Benedict'

    private static final String FROM_EMAIL = 'example@mail.com'
    private static final String FROM_PERSONAL = 'Centre for Continuing Education (TEST)'
    private static final String ENVELOP_FROM = FROM_EMAIL

    private static final String SUBJECT = 'Test Subject'
    private static final String CONTENT_PLAIN = 'content plain'

    //------------------------------------------------------------------------------


    private MailDeliveryService service
    private MailDeliveryParam param

    @BeforeEach
    void setup() throws Exception {
        SMTPService smtpService = Mockito.mock(SMTPService)
        Mockito.when(smtpService.host).thenReturn(SMTP_HOST)
        Mockito.when(smtpService.userName).thenReturn(SMTP_USERNAME)
        Mockito.when(smtpService.password).thenReturn(SMTP_PASSWORD)
        Mockito.when(smtpService.port).thenReturn(SMTP_PORT)
        MailSession mailSession = new MailSession(smtpService)
        service = new MailDeliveryService(smtpService, mailSession, ANGEL_VERSION)
        fillTestParameters()
    }

    private void fillTestParameters() {
        MessagePerson messagePerson = Mockito.mock(MessagePerson)
        Mockito.when(messagePerson.destinationAddress).thenReturn(TO_EMAIL)
        Contact contact = Mockito.mock(Contact)
        Mockito.when(messagePerson.contact).thenReturn(contact)
        Mockito.when(contact.isCompany).thenReturn(IS_COMPANY)
        Mockito.when(contact.lastName).thenReturn(TO_PERSONAL_LASTNAME)
        Mockito.when(contact.firstName).thenReturn(TO_PERSONAL_FIRSTNAME)

        GetFrom getFrom = GetFrom.valueOf(FROM_EMAIL, FROM_PERSONAL)
        GetEnvelopeFrom getEnvelopeFrom = GetEnvelopeFrom.valueOf(ENVELOP_FROM)
        GetAddresses getAddressesTO = GetAddresses.valueOf(messagePerson)
        GetAddresses getAddressesCC = GetAddresses.empty()
        GetAddresses getAddressesBCC = GetAddresses.empty()
        GetSubject getSubject = GetSubject.valueOf(SUBJECT)
        GetContent getContent = GetContent.valueOf(GetEmailPlainBody.valueOf(CONTENT_PLAIN))
        param = MailDeliveryParam.valueOf(getFrom, getEnvelopeFrom, getAddressesTO, getAddressesCC, getAddressesBCC, getSubject, getContent)
    }

    @Ignore
    @Test
    void testSendEmail() throws MessagingException {
        service.sendEmail(param)
    }


}
