package ish.oncourse.server.script

import ish.CayenneIshTestCase
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Script
import ish.oncourse.server.scripting.ScriptParameters
import ish.oncourse.server.scripting.ScriptRun
import ish.oncourse.server.scripting.api.EmailService
import ish.oncourse.server.scripting.api.EmailSpec
import ish.scripting.ScriptResult
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.time.DateUtils
import org.codehaus.groovy.runtime.MethodClosure
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.Assert
import org.junit.Ignore
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor

import javax.mail.MessagingException
import javax.script.Bindings
import javax.script.SimpleBindings
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.WeekFields

import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.*

//TODO replace or delete
@Ignore
class CceSendWaitingListClassPublished extends CayenneIshTestCase {

    private ObjectContext context
    private EmailService emailService = mock(EmailService.class)

    @BeforeEach
    void setup() throws Exception {
        wipeTables()
        this.context = injector.getInstance(ICayenneService.class).getNewContext()

        InputStream st = CceSendWaitingListClassPublished.class.getClassLoader().getResourceAsStream("ish/oncourse/server/script/CceSendWaitingListClassPublished.xml")

        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder()
        builder.setColumnSensing(true)
        FlatXmlDataSet dataSet = builder.build(st)

        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
        rDataSet.addReplacementObject("[endDate]", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(DateUtils.addYears(new Date(), 1)))

        executeDatabaseOperation(rDataSet)

        super.setup()
    }

    /**
     * classes on start:
     * id 1, 2, 6: actual classes
     * id 3: not actual - end date before now
     * id 4: not actual - cancelled
     * id 5: not actual - not shown on web
     *
     * @throws IOException
     * @throws MessagingException
     */
    @Test
    void testSendWaitingListClassPublish() throws IOException, MessagingException {
        doCallRealMethod().when(emailService).email(any())
        doNothing().when(emailService).createEmail(any())

        ScriptParameters parameters = ScriptParameters.empty()
        ScriptResult result
        Bindings bindings = prepareBindings()
        Script script = prepareScript("../private-resources/cce/scripts/CCE-send-waitingList-class-published-CRON.groovy")

        result = ScriptRun.valueOf(script, bindings, parameters, context).run()
        Assert.assertEquals(ScriptResult.ResultType.SUCCESS, result.getType())
        checkEmailBindings()
    }

    private Bindings prepareBindings() throws MessagingException {

        Bindings bindings = new SimpleBindings()
        bindings.put("Email", emailService)
        bindings.put("email", new MethodClosure(emailService, "email"))

        return bindings
    }

    private Script prepareScript(String url) throws IOException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(url)))
        StringBuilder builder = new StringBuilder(reader.readLine())

        while (reader.ready()) {
            builder.append("\n" + reader.readLine())
        }

        Script script = new Script()
        script.setName("ScriptTest")
        script.setScript(builder.toString())

        return script
    }

    private void checkEmailBindings() throws MessagingException {
        ArgumentCaptor<EmailSpec> emailCaptor = ArgumentCaptor.forClass(EmailSpec.class)
        verify(emailService, times(3)).createEmail(emailCaptor.capture())

        emailCaptor.getAllValues().each{EmailSpec s ->

            Assert.assertEquals(String.format("Classes published %s_Course_1", LocalDate.now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1)), s.getCreatorKey())
            List<CourseClass> bindedClasses = (List) s.getBindings().get("classes")
            Assert.assertEquals(3, bindedClasses.size())
            Assert.assertEquals(new Long(100), bindedClasses.get(0).getId())
            Assert.assertEquals(new Long(200), bindedClasses.get(1).getId())
            Assert.assertEquals(new Long(600), bindedClasses.get(2).getId())
        }
    }
}
