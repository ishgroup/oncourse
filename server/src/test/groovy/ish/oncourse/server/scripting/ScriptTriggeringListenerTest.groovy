package ish.oncourse.server.scripting

import ish.TestWithDatabase
import ish.common.types.EntityEvent
import ish.common.types.SystemEventType
import ish.common.types.TriggerType
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Script
import ish.oncourse.server.services.ISchedulerService
import ish.oncourse.server.services.TestSchedulerService
import org.apache.cayenne.map.LifecycleEvent
import org.apache.cayenne.query.SQLTemplate
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.quartz.JobDetail
import org.quartz.JobKey
import org.quartz.SchedulerException

class ScriptTriggeringListenerTest extends TestWithDatabase {
    private static ICayenneService cayenneService
    private static GroovyScriptService scriptService
    private static TestSchedulerService schedulerService
    private static final Logger logger = LogManager.getLogger()

    @BeforeAll
    void init() {
        cayenneService = injector.getInstance(ICayenneService.class)
        scriptService = injector.getInstance(GroovyScriptService.class)
        schedulerService = (TestSchedulerService) injector.getInstance(ISchedulerService.class)
    }

    @BeforeEach
    void setup() throws SchedulerException {
        cayenneService.getNewContext().performNonSelectingQuery(new SQLTemplate(Script.class, "DELETE FROM Script"))
        scriptService.initTriggers()
        new ArrayList<>(schedulerService.getJobs()).each { JobDetail j ->
            try {
                schedulerService.removeJob(j.getKey())
            } catch (SchedulerException e) {
                logger.catching(e)
            }
        }
    }

    
    private Script createScript(TriggerType triggerType) {
        Script script = cayenneService.getNewContext().newObject(Script.class)
        script.setName("Script")
        script.setEnabled(true)
        script.setScript("def run(args) { logger.error 'Hello World' }")
        script.setTriggerType(triggerType)
        script.setCronSchedule("0 0 4 * * ?")
        script.setEntityClass(Contact.class.getSimpleName())
        script.setEntityEventType(EntityEvent.UPDATE)
        script.setSystemEventType(SystemEventType.CLASS_CANCELLED)
        script.getContext().commitChanges()
        return script
    }

    
    @Test
    void testCronTypeChanged() {
        Script script = createScript(TriggerType.CRON)

        Assertions.assertEquals(1, schedulerService.getJobs().size())
        Assertions.assertEquals(JobKey.jobKey(script.getName(), ISchedulerService.CUSTOM_SCRIPT_JOBS_GROUP_ID), schedulerService.getJobs().get(0).getKey())

        script.setTriggerType(TriggerType.ENTITY_EVENT)

        script.getContext().commitChanges()

        Assertions.assertEquals(0, schedulerService.getJobs().size())
        Assertions.assertEquals(1, scriptService.getScriptsForEntity(Contact.class, LifecycleEvent.POST_UPDATE).size())
        Assertions.assertTrue(script.equalsIgnoreContext(scriptService.getScriptsForEntity(Contact.class, LifecycleEvent.POST_UPDATE).iterator().next()))
    }


    
    @Test
    void testRenameCronScript() {
        Script script = createScript(TriggerType.CRON)

        Assertions.assertEquals(1, schedulerService.getJobs().size())
        Assertions.assertEquals(JobKey.jobKey(script.getName(), ISchedulerService.CUSTOM_SCRIPT_JOBS_GROUP_ID), schedulerService.getJobs().get(0).getKey())

        script.setName("Script1")
        script.getContext().commitChanges()

        Assertions.assertEquals(1, schedulerService.getJobs().size())
        Assertions.assertEquals(JobKey.jobKey(script.getName(), ISchedulerService.CUSTOM_SCRIPT_JOBS_GROUP_ID), schedulerService.getJobs().get(0).getKey())
    }


    
    @Test
    void testRenameAndDisableCronScript() {
        Script script = createScript(TriggerType.CRON)

        Assertions.assertEquals(1, schedulerService.getJobs().size())
        Assertions.assertEquals(JobKey.jobKey(script.getName(), ISchedulerService.CUSTOM_SCRIPT_JOBS_GROUP_ID), schedulerService.getJobs().get(0).getKey())

        script.setName("Script1")
        script.setEnabled(false)
        script.getContext().commitChanges()

        Assertions.assertEquals(0, schedulerService.getJobs().size())
    }


    
    @Test
    void testEntityTypeChanged() {
        Script script = createScript(TriggerType.ENTITY_EVENT)

        Assertions.assertEquals(1, scriptService.getScriptsForEntity(Contact.class, LifecycleEvent.POST_UPDATE).size())
        Assertions.assertTrue(script.equalsIgnoreContext(scriptService.getScriptsForEntity(Contact.class, LifecycleEvent.POST_UPDATE).iterator().next()))

        script.setTriggerType(TriggerType.CRON)
        script.getContext().commitChanges()

        Assertions.assertEquals(0, scriptService.getScriptsForEntity(Contact.class, LifecycleEvent.POST_UPDATE).size())
        Assertions.assertEquals(1, schedulerService.getJobs().size())
        Assertions.assertEquals(JobKey.jobKey(script.getName(), ISchedulerService.CUSTOM_SCRIPT_JOBS_GROUP_ID), schedulerService.getJobs().get(0).getKey())

    }

    
    @Test
    void testCronScriptRemove() {
        Script script = createScript(TriggerType.CRON)

        Assertions.assertEquals(1, schedulerService.getJobs().size())
        Assertions.assertEquals(JobKey.jobKey(script.getName(), ISchedulerService.CUSTOM_SCRIPT_JOBS_GROUP_ID), schedulerService.getJobs().get(0).getKey())

        script.getContext().deleteObject(script)
        script.getContext().commitChanges()

        Assertions.assertEquals(0, schedulerService.getJobs().size())
    }

    
    @Test
    void testEntityScriptRemove() {
        Script script = createScript(TriggerType.ENTITY_EVENT)

        Assertions.assertEquals(1, scriptService.getScriptsForEntity(Contact.class, LifecycleEvent.POST_UPDATE).size())
        Assertions.assertTrue(script.equalsIgnoreContext(scriptService.getScriptsForEntity(Contact.class, LifecycleEvent.POST_UPDATE).iterator().next()))

        script.getContext().deleteObject(script)
        script.getContext().commitChanges()

        Assertions.assertEquals(0, scriptService.getScriptsForEntity(Contact.class, LifecycleEvent.POST_UPDATE).size())
    }

}
