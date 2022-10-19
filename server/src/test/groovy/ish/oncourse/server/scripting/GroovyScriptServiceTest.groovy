/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.scripting

import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.DatabaseSetup
import ish.common.types.AutomationStatus
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.services.ISchedulerService
import ish.oncourse.server.services.TestSchedulerService
import ish.scripting.ScriptResult
import ish.util.TimeZoneUtil
import org.apache.cayenne.map.LifecycleEvent
import org.apache.cayenne.query.SelectById
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.quartz.JobDetail
import org.quartz.Trigger
import org.quartz.impl.triggers.CronTriggerImpl

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/scripting/groovyScriptServiceTestDataSet.xml")
class GroovyScriptServiceTest extends TestWithDatabase {
    
    @Test
    void IntegrationTest() {

        IntegrationConfiguration integration = SelectById.query(IntegrationConfiguration, 200l).selectOne(cayenneService.getNewContext())
        integration.setName('Integration')
        Assertions.assertEquals('value', (integration.getProperty('prop') as IntegrationProperty).value)
        Assertions.assertEquals('Integration', integration.getProperty('name'))
        Assertions.assertEquals('Integration', integration.name)
        Assertions.assertEquals('Integration', integration.getName())
        Assertions.assertEquals(200l, integration.id)
    }

    
    @Test
    void testLoggerFieldTest() {
        GroovyScriptService scriptService = injector.getInstance(GroovyScriptService.class)

        Script script = cayenneContext.newObject(Script.class)
        script.setAutomationStatus(AutomationStatus.ENABLED)
        script.setScript("logger.error('script test') \n test()\n " +
                "void test() {logger.error('script method test')}")
        ScriptResult result = scriptService.runScript(script, ScriptParameters.empty(), cayenneService.getNewContext())

        Assertions.assertEquals(ScriptResult.ResultType.SUCCESS, result.getType())
    }
    
    @Test
    void testRunScript() throws Exception {
        GroovyScriptService scriptService = injector.getInstance(GroovyScriptService.class)

        Script script = cayenneContext.newObject(Script.class)
        script.setAutomationStatus(AutomationStatus.ENABLED)
        script.setScript("return args.context != null")

        ScriptResult result = scriptService.runScript(script, ScriptParameters.empty(), cayenneService.getNewContext())

        Assertions.assertEquals(ScriptResult.ResultType.SUCCESS, result.getType())
        Assertions.assertEquals(Boolean.TRUE, result.getResultValue())
    }

    
    @Test
    void testCompilationFailure() throws Exception {
        GroovyScriptService scriptService = injector.getInstance(GroovyScriptService.class)

        Script script = cayenneContext.newObject(Script.class)
        script.setAutomationStatus(AutomationStatus.ENABLED)
        script.setScript("def run(args) { return context != null")

        ScriptResult result = scriptService.runScript(script, ScriptParameters.empty(), cayenneService.getNewContext())

        Assertions.assertEquals(ScriptResult.ResultType.FAILURE, result.getType())
        Assertions.assertNotNull(result.getError())
        Assertions.assertNull(result.getResultValue())
    }

    
    @Test
    void testScriptArguments() throws Exception {
        GroovyScriptService scriptService = injector.getInstance(GroovyScriptService.class)

        Script script = cayenneContext.newObject(Script.class)
        script.setAutomationStatus(AutomationStatus.ENABLED)
        script.setScript("return args.test")

        ScriptResult result = scriptService
                .runScript(script,
                        ScriptParameters.empty().add("test", "testValue"),
                        cayenneService.getNewContext())

        Assertions.assertEquals(ScriptResult.ResultType.SUCCESS, result.getType())
        Assertions.assertEquals("testValue", result.getResultValue())
    }

    @Test
    void testInitTriggers() throws Exception {
        Script script1 = SelectById.query(Script.class, 3).selectOne(cayenneContext)
        Script script2 = SelectById.query(Script.class, 4).selectOne(cayenneContext)

        GroovyScriptService scriptService = injector.getInstance(GroovyScriptService.class)

        scriptService.initTriggers()

        Set<Script> contactUpdateScripts = scriptService.getScriptsForEntity(Contact.class, LifecycleEvent.POST_UPDATE)
        Assertions.assertEquals(1, contactUpdateScripts.size())
        Assertions.assertEquals(script1.getObjectId(), contactUpdateScripts.iterator().next().getObjectId())

        Set<Script> courseCreateScripts = scriptService.getScriptsForEntity(Course.class, LifecycleEvent.POST_PERSIST)
        Assertions.assertEquals(1, courseCreateScripts.size())
        Assertions.assertEquals(script2.getObjectId(), courseCreateScripts.iterator().next().getObjectId())

        Set<Script> enrolmentCreateScripts = scriptService.getScriptsForEntity(Enrolment.class, LifecycleEvent.POST_PERSIST)
        Assertions.assertEquals(0, enrolmentCreateScripts.size())

        TestSchedulerService schedulerService = (TestSchedulerService) injector.getInstance(ISchedulerService.class)

        List<JobDetail> jobs = schedulerService.getJobs()

        Assertions.assertEquals(1, jobs.size())

        JobDetail testJob = jobs.get(0)

        Assertions.assertEquals("script1", testJob.getKey().getName())
        Assertions.assertEquals(ISchedulerService.CUSTOM_SCRIPT_JOBS_GROUP_ID, testJob.getKey().getGroup())
    }

    @Test
    void testCronTrigger() throws Exception {
        PreferenceController preferenceController = (PreferenceController) injector.getInstance(PreferenceController.class)
        preferenceController.setOncourseServerDefaultTimezone('Europe/Minsk')

        GroovyScriptService scriptService = injector.getInstance(GroovyScriptService.class)
        Script script1 = SelectById.query(Script.class, 3).selectOne(cayenneContext)

        scriptService.scriptAdded(script1, '0 0 7 1/1 * ? *')
        TestSchedulerService schedulerService = (TestSchedulerService) injector.getInstance(ISchedulerService.class)
        Assertions.assertEquals(1,  schedulerService.jobs.size())

        JobDetail detail = schedulerService.jobs.get(0)
        Trigger trigger = schedulerService.getTrigger(detail)
        Assertions.assertTrue(trigger instanceof CronTriggerImpl)

        Assertions.assertEquals('Europe/Minsk', (trigger as CronTriggerImpl).timeZone.ID)


        preferenceController.setOncourseServerDefaultTimezone(null)
        scriptService.scriptAdded(script1, '0 0 7 1/1 * ? *')
        detail = schedulerService.jobs.get(0)
        trigger = schedulerService.getTrigger(detail)
        Assertions.assertEquals(TimeZoneUtil.DEFAULT_SERVER_TIME_ZONE, (trigger as CronTriggerImpl).timeZone.ID)


    }
}
