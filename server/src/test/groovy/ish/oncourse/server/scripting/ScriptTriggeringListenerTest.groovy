package ish.oncourse.server.scripting

import ish.CayenneIshTestCase
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

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
import org.junit.Test
import org.quartz.JobKey
import org.quartz.SchedulerException

/**
 * Created by Artem on 12/10/2016.
 */
class ScriptTriggeringListenerTest extends CayenneIshTestCase {
    private ICayenneService cayenneService
    private GroovyScriptService scriptService
    private TestSchedulerService schedulerService
    private static final Logger logger = LogManager.getLogger()

    void setup() throws SchedulerException {
        this.cayenneService = injector.getInstance(ICayenneService.class)
        this.scriptService = injector.getInstance(GroovyScriptService.class)
        this.schedulerService = (TestSchedulerService) injector.getInstance(ISchedulerService.class)
        cayenneService.getNewContext().performNonSelectingQuery(new SQLTemplate(Script.class, "DELETE FROM Script"))
        scriptService.initTriggers()
        new ArrayList<>(schedulerService.getJobs()).each{j ->
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

        assertEquals(1, schedulerService.getJobs().size())
        assertEquals(JobKey.jobKey(script.getName(), ISchedulerService.CUSTOM_SCRIPT_JOBS_GROUP_ID), schedulerService.getJobs().get(0).getKey())

        script.setTriggerType(TriggerType.ENTITY_EVENT)

        script.getContext().commitChanges()

        assertEquals(0, schedulerService.getJobs().size())
        assertEquals(1, scriptService.getScriptsForEntity(Contact.class, LifecycleEvent.POST_UPDATE).size())
        assertTrue(script.equalsIgnoreContext(scriptService.getScriptsForEntity(Contact.class, LifecycleEvent.POST_UPDATE).iterator().next()))
    }


    @Test
    void testRenameCronScript() {
        Script script = createScript(TriggerType.CRON)

        assertEquals(1, schedulerService.getJobs().size())
        assertEquals(JobKey.jobKey(script.getName(), ISchedulerService.CUSTOM_SCRIPT_JOBS_GROUP_ID), schedulerService.getJobs().get(0).getKey())

        script.setName("Script1")
        script.getContext().commitChanges()

        assertEquals(1, schedulerService.getJobs().size())
        assertEquals(JobKey.jobKey(script.getName(), ISchedulerService.CUSTOM_SCRIPT_JOBS_GROUP_ID), schedulerService.getJobs().get(0).getKey())
    }


    @Test
    void testRenameAndDisableCronScript() {
        Script script = createScript(TriggerType.CRON)

        assertEquals(1, schedulerService.getJobs().size())
        assertEquals(JobKey.jobKey(script.getName(), ISchedulerService.CUSTOM_SCRIPT_JOBS_GROUP_ID), schedulerService.getJobs().get(0).getKey())

        script.setName("Script1")
        script.setEnabled(false)
        script.getContext().commitChanges()

        assertEquals(0, schedulerService.getJobs().size())
    }


    @Test
    void testEntityTypeChanged() {
        Script script = createScript(TriggerType.ENTITY_EVENT)

        assertEquals(1, scriptService.getScriptsForEntity(Contact.class, LifecycleEvent.POST_UPDATE).size())
        assertTrue(script.equalsIgnoreContext(scriptService.getScriptsForEntity(Contact.class, LifecycleEvent.POST_UPDATE).iterator().next()))

        script.setTriggerType(TriggerType.CRON)
        script.getContext().commitChanges()

        assertEquals(0, scriptService.getScriptsForEntity(Contact.class, LifecycleEvent.POST_UPDATE).size())
        assertEquals(1, schedulerService.getJobs().size())
        assertEquals(JobKey.jobKey(script.getName(), ISchedulerService.CUSTOM_SCRIPT_JOBS_GROUP_ID), schedulerService.getJobs().get(0).getKey())

    }

    @Test
    void testCronScriptRemove() {
        Script script = createScript(TriggerType.CRON)

        assertEquals(1, schedulerService.getJobs().size())
        assertEquals(JobKey.jobKey(script.getName(), ISchedulerService.CUSTOM_SCRIPT_JOBS_GROUP_ID), schedulerService.getJobs().get(0).getKey())

        script.getContext().deleteObject(script)
        script.getContext().commitChanges()

        assertEquals(0, schedulerService.getJobs().size())
    }

    @Test
    void testEntityScriptRemove() {
        Script script = createScript(TriggerType.ENTITY_EVENT)

        assertEquals(1, scriptService.getScriptsForEntity(Contact.class, LifecycleEvent.POST_UPDATE).size())
        assertTrue(script.equalsIgnoreContext(scriptService.getScriptsForEntity(Contact.class, LifecycleEvent.POST_UPDATE).iterator().next()))

        script.getContext().deleteObject(script)
        script.getContext().commitChanges()

        assertEquals(0, scriptService.getScriptsForEntity(Contact.class, LifecycleEvent.POST_UPDATE).size())
    }

}
