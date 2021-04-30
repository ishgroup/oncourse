package ish.oncourse.server.messaging

import ish.CayenneIshTestCase
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.EmailTemplate
import ish.oncourse.server.cayenne.Payslip
import ish.oncourse.server.scripting.GroovyScriptService
import ish.oncourse.server.scripting.api.TemplateService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.Ignore
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import javax.mail.MessagingException

class TutorNotificationVisualTest extends CayenneIshTestCase {
    @BeforeEach
    void setup() throws Exception {
        InputStream st = GroovyScriptService.class.getClassLoader().getResourceAsStream("ish/oncourse/server/messaging/tutorNotificationVisualTestDataSet.xml")
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder()
        builder.setColumnSensing(true)
        FlatXmlDataSet dataSet = builder.build(st)

        executeDatabaseOperation(dataSet)
    }

    /**
     * Creates email template, fills it with data from dataset and sends email.
     * Note: ip-address and port of smtp server is set in dataset (localhost, 2525). To run this test, please, define your ip and port first.
     *
     * @throws FileNotFoundException if there are no html or plain bodies for header, footer and Tutor-Payrun-Notification
     */
    @Ignore
    @Test
    void sendTutorNotification() throws MessagingException, FileNotFoundException {
        ObjectContext context = injector.getInstance(ICayenneService.class).getNewContext()

        fillHeaderTemplateWithActualData(context)
        fillFooterTemplateWithActualData(context)

        EmailTemplate template = fillBodyTemplateWithActualData(context)

        Map<String, Object> map = new HashMap<>()
        map.put("payslip", SelectById.query(Payslip.class, 1).selectOne(context))

        MailDeliveryParam param = prepareDeliveryParam(map, template)

        MailDeliveryService service = injector.getInstance(MailDeliveryService.class)

        service.sendEmail(param)
    }

    /**
     * gets header template from DB and fills it with actual html and plain body
     * @param context
     * @throws FileNotFoundException
     */
    private void fillHeaderTemplateWithActualData (ObjectContext context) throws FileNotFoundException{
        EmailTemplate header = SelectById.query(EmailTemplate.class, 2).selectOne(context)

        String headerBodyPlain = new Scanner(new File("../private-resources/cce/email/CCE-Header.plain")).useDelimiter("\\Z").next()
        String headerBodyHtml = new Scanner(new File("../private-resources/cce/email/CCE-Header.html")).useDelimiter("\\Z").next()

        header.setBodyPlain(headerBodyPlain)
        header.setBodyHtml(headerBodyHtml)

        context.commitChanges()
    }

    /**
     * gets footer template from DB and fills it with actual html and plain body
     * @param context
     * @throws FileNotFoundException
     */
    private void fillFooterTemplateWithActualData (ObjectContext context) throws FileNotFoundException{
        EmailTemplate header = SelectById.query(EmailTemplate.class, 3).selectOne(context)

        String footerBodyPlain = new Scanner(new File("../private-resources/cce/email/CCE-Footer.plain")).useDelimiter("\\Z").next()
        String footerBodyHtml = new Scanner(new File("../private-resources/cce/email/CCE-Footer.html")).useDelimiter("\\Z").next()

        header.setBodyPlain(footerBodyPlain)
        header.setBodyHtml(footerBodyHtml)

        context.commitChanges()
    }

    /**
     * gets Tutor-Payrun-Notification template from DB and fills it with actual html and plain body
     * @param context
     * @throws FileNotFoundException
     */
    private EmailTemplate fillBodyTemplateWithActualData (ObjectContext context) throws FileNotFoundException{
        EmailTemplate template = SelectById.query(EmailTemplate.class, 1).selectOne(context)

        String bodyPlain = new Scanner(new File("../private-resources/cce/email/Tutor-Payrun-Notification.plain")).useDelimiter("\\Z").next()
        String bodyHtml = new Scanner(new File("../private-resources/cce/email/Tutor-Payrun-Notification.html")).useDelimiter("\\Z").next()

        template.setBodyPlain(bodyPlain)
        template.setBodyHtml(bodyHtml)

        context.commitChanges()

        return template
    }

    private MailDeliveryParam prepareDeliveryParam (Map map, EmailTemplate template){
        TemplateService templateService = injector.getInstance(TemplateService.class)

        GetFrom getFrom = GetFrom.valueOf("test@from")
        GetEnvelopeFrom getEnvelopeFrom = GetEnvelopeFrom.valueOf("test@envelopeFrom")
        GetAddresses getAddresses = GetAddresses.valueOf("recipient@address")
        GetSubject getSubject = GetSubject.valueOf("CCE Pay Run Advice")
        GetEmailPlainBody getEmailPlainBody = GetEmailPlainBody.valueOf(templateService, "Tutor Payrun Notification", map, template.getBodyPlain())
        GetEmailHtmlBody getEmailHtmlBody = GetEmailHtmlBody.valueOf(templateService, "Tutor Payrun Notification", map)
        GetContent getContent = GetContent.valueOf(getEmailPlainBody, getEmailHtmlBody)

        MailDeliveryParam param = MailDeliveryParam.valueOf(getFrom, getEnvelopeFrom, getAddresses, getSubject, getContent)

        return param
    }
}
