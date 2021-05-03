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
package ish.oncourse.server.scripting

import com.google.inject.Inject
import com.google.inject.Injector
import groovy.transform.CompileStatic
import io.bootique.BQRuntime
import ish.common.types.EntityEvent
import ish.common.types.SystemEventType
import ish.common.types.TriggerType
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.IPreferenceController
import ish.oncourse.server.ISHDataContext
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Script
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.document.DocumentService
import ish.oncourse.server.export.ExportService
import ish.oncourse.server.imports.ImportService
import ish.oncourse.server.integration.EventService
import ish.oncourse.server.integration.GroovyScriptEventListener
import ish.oncourse.server.messaging.MessageService
import ish.oncourse.server.print.PrintService
import ish.oncourse.server.querying.QueryService
import ish.oncourse.server.scripting.api.CollegePreferenceService
import ish.oncourse.server.scripting.api.EmailService
import ish.oncourse.server.scripting.api.TemplateService
import ish.oncourse.server.services.AuditService
import ish.oncourse.server.services.ISchedulerService
import ish.oncourse.server.services.ISystemUserService
import ish.oncourse.server.users.SystemUserService
import ish.oncourse.types.AuditAction
import ish.scripting.ScriptResult
import ish.util.TimeZoneUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.Persistent
import org.apache.cayenne.map.LifecycleEvent
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.codehaus.groovy.runtime.MethodClosure
import org.quartz.*
import org.reflections.Reflections

import javax.script.*
import java.util.concurrent.*

import static ish.common.types.TriggerType.*
import static ish.oncourse.server.integration.PluginService.PLUGIN_PACKAGE
import static ish.oncourse.server.lifecycle.ChangeFilter.getAtrAttributeChange
import static ish.scripting.ScriptResult.ResultType.FAILURE
import static ish.scripting.ScriptResult.ResultType.SUCCESS

@CompileStatic
class GroovyScriptService {

    private static final Logger logger = LogManager.getLogger(GroovyScriptService.class)

    // TODO remove useless binding (value, entity)
    public static final String FILE_PARAM_NAME = "file"
    public static final String ENTITY_PARAM_NAME = "entity"
    public static final String RECORD_PARAM_NAME = "record"
    public static final String RECORDS_PARAM_NAME = "records"
    public static final String SCRIPT_CONTEXT_PROPERTY = "script_context"

    private static final String GROOVY_SCRIPT_ENGINE = "groovy"

    private static final String TEMPLATE_SERVICE = "Template"
    private static final String PREFERENCE_SERVICE = "Preferences"
    private static final String EMAIL_SERVICE = "Email"
    private static final String QUERY_SERVICE = "Query"
    private static final String EVENT_SERVICE = "eventService"

    private static final String RUN_QUERY = "query"
    private static final String SEND_EMAIL = "email"
    private static final String SEND_SMTP_EMAIL = "smtp"
    private static final String SEND_SMS = "sms"
    private static final String SEND_MESSAGE = "message"
    private static final String PERFORM_EXPORT = "export"
    private static final String PERFORM_IMPORT = "importData"
    private static final String PRINT_REPORT = "report"
    private static final String RESTRICTED_PORTAL_URL = "restrictedPortalURL"
    private static final String DOCUMENT = "document"
    public static final String SYSTEM_USER = "loggedInUser"

    // integrations
    private static final String SYSTEM_USERS_BY_ROLE = "getSystemUsersByRole"

    private static final String CONTEXT_ARG = "context"
    private static final String ARGS = "args"

    public static final String DEFAULT_IMPORTS =
            "import ish.common.types.*\n" +
                    "import ish.oncourse.types.*\n" +
                    "import ish.messaging.*\n" +
                    "import ish.oncourse.server.cayenne.*\n" +
                    "import org.apache.cayenne.query.*\n" +
                    "import java.text.*\n" +
                    "import ish.util.*\n" +
                    "import java.time.LocalDate\n" +
                    "import ish.math.Money\n" +
                    "import org.apache.logging.log4j.*\n"

    public static final String PREPARE_LOGGER = "logger = LogManager.getLogger(\"%s\")\n"

    // TODO: look into embedding this into compilation customizer
    private static final String PREPARE_API =
            "Contact.metaClass.replacementEmail = null\n" +
                    "\n" +
                    "Contact.metaClass.rightShift = { String email -> \n" +
                    "\tdelegate.replacementEmail = email\n" +
                    "\treturn delegate\n" +
                    "}\n"

    private ScriptEngineManager engineManager

    private ICayenneService cayenneService
    private ISchedulerService schedulerService
    private IPreferenceController preferenceController
    private AuditService auditService
    private SystemUserService systemUserService
    private TemplateService templateService

    private Injector injector

    private ExecutorService executorService
    private Map<String, ScriptClosureFactory> closureFactories

    private Map<LifecycleEvent, Map<Class<?>, Set<Script>>> scriptTriggerMap = new HashMap<>()

    @Inject
    private EventService eventService

    @Inject
    GroovyScriptService(ICayenneService cayenneService, ISchedulerService schedulerService,
                        IPreferenceController preferenceController, Injector injector, SystemUserService systemUserService, TemplateService templateService) {
        GroovySystem.getMetaClassRegistry().getMetaClassCreationHandler().setDisableCustomMetaClassLookup(true)
        this.injector = injector
        auditService = injector.getInstance(AuditService.class)

        this.cayenneService = cayenneService
        this.schedulerService = schedulerService
        this.preferenceController = preferenceController
        this.systemUserService = systemUserService
        this.templateService = templateService
        this.engineManager = new ScriptEngineManager()

        // create single thread executor with FIFO task queue
        this.executorService = Executors.newSingleThreadExecutor()
    }

    GroovyScriptService(ICayenneService iCayenneService, ISchedulerService iSchedulerService, PreferenceController preferenceController, BQRuntime bqRuntime) {}

    void registerThreadInCayenneRuntime() {
        // since executor has just single thread in his pool - it is enough to register this thread to cayenne runtime
        // need to prevent java.lang.IllegalStateException: Transaction must have 'STATUS_ACTIVE' to add a connection. Current status: STATUS_COMMITTED
        // see http://cayenne.195.n3.nabble.com/Transaction-exception-in-concurrent-environment-td4029107.html
        executorService.execute { ObjectSelect.query(SystemUser).selectFirst(cayenneService.newContext) }
    }

    /**
     * @return A map of closure factories with closure keys
     */
    Map<String, ScriptClosureFactory> getClosureFactories() {
        if (!this.closureFactories) {
            Reflections reflections = new Reflections(PLUGIN_PACKAGE)
            this.closureFactories = reflections.getSubTypesOf(ScriptClosureTrait).collectEntries { specClass ->
                [(specClass.getAnnotation(ScriptClosure).key()) : new ScriptClosureFactory(injector:injector, cayenneService: cayenneService, specClass: specClass)]
            }
        }
        return this.closureFactories
    }

    protected Bindings getServiceBindings() {
        QueryService queryService = injector.getInstance(QueryService.class)
        EmailService emailService = injector.getInstance(EmailService.class)
        CollegePreferenceService preferenceService = injector.getInstance(CollegePreferenceService.class)
        ExportService exportService = injector.getInstance(ExportService.class)
        ImportService importService = injector.getInstance(ImportService.class)
        ISystemUserService systemUserService = injector.getInstance(ISystemUserService.class)
        PrintService printService = injector.getInstance(PrintService.class)
        MessageService messageService = injector.getInstance(MessageService.class)
        DocumentService documentService = injector.getInstance(DocumentService.class)

        Bindings bindings = new SimpleBindings()

        bindings.put(TEMPLATE_SERVICE, injector.getInstance(TemplateService.class))
        bindings.put(PREFERENCE_SERVICE, preferenceService)
        bindings.put(EMAIL_SERVICE, emailService)
        bindings.put(QUERY_SERVICE, queryService)
        bindings.put(EVENT_SERVICE, eventService)
        bindings.put(RECORDS_PARAM_NAME, [])
        bindings.put(FILE_PARAM_NAME, null)

        bindings.put(RUN_QUERY, new MethodClosure(queryService, RUN_QUERY))
        bindings.put(SEND_EMAIL, new MethodClosure(emailService, SEND_EMAIL))
        bindings.put(SEND_SMTP_EMAIL, new MethodClosure(emailService, SEND_SMTP_EMAIL))
        bindings.put(SEND_SMS, new MethodClosure(messageService, SEND_SMS))
        bindings.put(SEND_MESSAGE, new MethodClosure(messageService, SEND_MESSAGE))
        bindings.put(CollegePreferenceService.PREFERENCE_ALIAS, preferenceService.getPrefHelper())
        bindings.put(PERFORM_EXPORT, new MethodClosure(exportService, PERFORM_EXPORT))
        bindings.put(PERFORM_IMPORT, new MethodClosure(importService, PERFORM_IMPORT))
        bindings.put(PRINT_REPORT, new MethodClosure(printService, PRINT_REPORT))
        bindings.put(RESTRICTED_PORTAL_URL, new MethodClosure(preferenceService, RESTRICTED_PORTAL_URL))
        bindings.put(DOCUMENT, new MethodClosure(documentService, DOCUMENT))

        getClosureFactories().each { key, factory ->
            bindings.put(key,  new MethodClosure(factory, "execute"))
        }

        bindings.put(SYSTEM_USERS_BY_ROLE, new MethodClosure(systemUserService, SYSTEM_USERS_BY_ROLE))

        return bindings
    }

    void initTriggers() {
        registerThreadInCayenneRuntime()
        loadEntityTriggers()
        scheduleCronScripts()

        eventService.registerListener(new GroovyScriptEventListener(cayenneService, this), SystemEventType.values())
    }

    Set<Script> getScriptsForEntity(Class<?> entityClass, LifecycleEvent event) {
        def scripts = scriptTriggerMap?.get(event)?.get(entityClass)
        if (scripts) {
            return Collections.unmodifiableSet(scripts)
        }

        return Collections.emptySet()
    }

    private void loadEntityTriggers() {
        def context = cayenneService.getNewContext()

        def enabledScripts = ObjectSelect.query(Script)
                .where(Script.TRIGGER_TYPE.eq(ENTITY_EVENT))
                .and(Script.ENABLED.isTrue())
                .and(Script.ENTITY_CLASS.isNotNull())
                .select(context)

        Map<LifecycleEvent, Map<Class<?>, Set<Script>>> triggerMap = new HashMap<>()

        // fill the map with entity events
        EntityEvent.values().each { entityEvent ->
            entityEvent.getLifecycleEvents().each {lifecycleEvent ->
                if (!triggerMap.containsKey(lifecycleEvent)) {
                    triggerMap.put(lifecycleEvent, new HashMap<Class<?>, Set<Script>>())
                }
            }
        }

        // populate the map with enabled scripts
        enabledScripts.each { script ->
            def classDescriptor = context.getEntityResolver().getClassDescriptor(script.getEntityClass())
            def entityClass = classDescriptor.getEntity().getJavaClass()

            for (lifecycleEvent in script.getEntityEventType().getLifecycleEvents()) {
                def entityScriptsMap = triggerMap.get(lifecycleEvent)
                if (!entityScriptsMap.containsKey(entityClass)) {
                    entityScriptsMap.put(entityClass, new HashSet<>())
                }
                entityScriptsMap.get(entityClass).add(script)
            }
        }

        this.scriptTriggerMap = triggerMap
    }

    private void scheduleCronScripts() {
        def scripts = ObjectSelect.query(Script)
                .where(Script.TRIGGER_TYPE.eq(CRON))
                .select(cayenneService.getNewContext())

        scripts.each { script ->
            rescheduleCronScript(script, null)
        }
    }

    private void rescheduleCronScript(Script script, String saveCronExp) {
        try {
            if (script.getEnabled()) {
                def aJob = JobBuilder.newJob(ScriptExecutingJob.class)
                        .withIdentity(script.getName(), ISchedulerService.CUSTOM_SCRIPT_JOBS_GROUP_ID)
                        .usingJobData(ScriptExecutingJob.SCRIPT_NAME_PARAMETER, script.getName())
                        .build()

                def cronExpression = saveCronExp ?: CronExpressionGenerator.generate(script.getCronSchedule())

                def aTrigger = TriggerBuilder.newTrigger()
                        .withIdentity(script.getName() + ISchedulerService.TRIGGER_POSTFIX, ISchedulerService.CUSTOM_SCRIPT_JOBS_GROUP_ID)
                        .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)
                                .inTimeZone(TimeZoneUtil.getTimeZone(preferenceController.getOncourseServerDefaultTimezone())))
                        .build()

                schedulerService.scheduleJob(aJob, aTrigger)
            } else {
                schedulerService.removeJob(JobKey.jobKey(script.getName(), ISchedulerService.CUSTOM_SCRIPT_JOBS_GROUP_ID))
            }
        } catch (Exception e) {
            logger.error("Couldn't schedule script execution for '{}'.", script.getName(), e)
        }
    }

    void scriptAdded(Script script, String saveCronExp) {
        switch (script.getTriggerType()) {
            case ENTITY_EVENT:
                loadEntityTriggers()
                break
            case CRON:
                rescheduleCronScript(script, saveCronExp)
                break
            case ONCOURSE_EVENT:
                // do nothing, on course events are not relying on entity/cron scheduling logic
                break
            case ON_DEMAND:
                // do nothing
                break
            default:
                throw new IllegalStateException("Unknown script trigger type.")
        }
    }

    void scriptDeleted(Script script) {
        switch (script.getTriggerType()) {
            case ENTITY_EVENT:
                loadEntityTriggers()
                break
            case CRON:
                try {
                    schedulerService.removeJob(JobKey.jobKey(script.getName(), ISchedulerService.CUSTOM_SCRIPT_JOBS_GROUP_ID))
                } catch (SchedulerException e) {
                    logger.error("Couldn't stop script execution'{}'.", script.getName(), e)
                }
                break
            default:
                break
        }

    }

    void scriptChanged(Script script) {

        def objectContext = script.getObjectContext()
        def objectId = script.getObjectId()

        def triggerChange = getAtrAttributeChange(objectContext, objectId, Script.TRIGGER_TYPE.getName())
        def nameChange = getAtrAttributeChange(objectContext, objectId, Script.NAME.getName())
        def cronChange = getAtrAttributeChange(objectContext, objectId, Script.CRON_SCHEDULE.getName())

        TriggerType oldTrigger = null
        String oldName = null
        String oldCron = null

        if (triggerChange) {
            oldTrigger = (TriggerType) triggerChange.getOldValue()
        }

        if (nameChange) {
            oldName = (String) nameChange.getOldValue()
        }

        if (cronChange) {
            oldCron = (String) cronChange.getOldValue()
        }

        String saveCronExp = null

        try {
            if (oldTrigger) {
                //trigger type was changed
                switch (oldTrigger) {
                    case ENTITY_EVENT:
                        //remove the script from Entity Triggers list (trigger type was changed from ENTITY_EVENT to some other type)
                        loadEntityTriggers()
                        break
                    case CRON:
                        //check if name was changed in one go with trigger type
                        String name = oldName != null ? oldName : script.getName()
                        //remove the script from cron scheduler (trigger type was changed from CRON to some other type)
                        schedulerService.removeJob(JobKey.jobKey(name, ISchedulerService.CUSTOM_SCRIPT_JOBS_GROUP_ID))
                        break
                    default:
                        break
                }
            } else if (CRON.equals(script.getTriggerType())) {

                if (oldCron == null && script.getEnabled() && CronExpressionGenerator.isDefaultCron(script.getCronSchedule())) {
                    saveCronExp = schedulerService.getCronExp(oldName != null ? oldName : script.getName())
                }

                if (oldName != null) {
                    //only name was changed for cron script
                    //remove old script name from cron scheduler (trigger type was changed from CRON to some other type)
                    schedulerService.removeJob(JobKey.jobKey(oldName, ISchedulerService.CUSTOM_SCRIPT_JOBS_GROUP_ID))
                }

            }
        } catch (SchedulerException e) {
            logger.error("Couldn't stop script execution'{}'.", script.getName(), e)
        }

        scriptAdded(script, saveCronExp)
    }


    Closure<ScriptResult> defaultHandler = { Exception e -> throw new RuntimeException(e) } as Closure<ScriptResult>

    ScriptResult runAndWait(Script script, ScriptParameters parameters) {
        return runAndWait(script, parameters, { -> cayenneService.getNewContext() as ISHDataContext}, defaultHandler)
    }


    ScriptResult runAndWait(Script script, ScriptParameters parameters, ISHDataContext context, Closure<ScriptResult> errorHandler) {
        return runAndWait(script, parameters, {-> context}, errorHandler)
    }

    ScriptResult runAndWait(Script script, ScriptParameters parameters, Closure<ISHDataContext> contextSupplier, Closure<ScriptResult> errorHandler) {
        Future<ScriptResult> future = executorService.submit( new Callable<ScriptResult>() {
            @Override
            ScriptResult call() throws Exception {
                return executeScript(script, parameters, contextSupplier())
            }
        })
        try {
            return future.get()
        } catch (Exception e) {
            return errorHandler(e)
        }
    }


    ScriptResult runScript(Script script, ScriptParameters parameters, ObjectContext context) {
        logger.warn("Running script {}. Parameters: {}", script.getName(), parameters.asMap())
        if (script == null) {
            throw new IllegalArgumentException("Script cannot be null.")
        }

        def engine = engineManager.getEngineByName(GROOVY_SCRIPT_ENGINE)

        def bindings = getServiceBindings()

        engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE)
        script.getOptions().each{option ->
            bindings.put(option.getName(), option.getObjectValue())
        }
        // add context to the script's parameters list
        parameters.add(CONTEXT_ARG, context)
        parameters.asList().each { param->
            bindings.put(param.getName(), param.getValue())
        }

        bindings.put(ARGS, parameters.asMap())
        try {
            def result = engine.eval(DEFAULT_IMPORTS + PREPARE_API +
                    String.format(PREPARE_LOGGER, script.getName()) +
                    script.getScript(), bindings)

            return ScriptResult.success(result)
        } catch (ScriptException e) {
            logger.error("Execution failed for '{}'.", script.getName(), e)
            return ScriptResult.failure(e.getMessage())
        }
    }


    void runScript(Script script) throws ExecutionException {
        runScript(script, ScriptParameters.empty())
    }

    void runScript(Script script, ScriptParameters parameters) {
        parameters.add(SYSTEM_USER, systemUserService.currentUser)
        executorService.execute({ ->
            try {
                executeScript(script, parameters, cayenneService.newContext)
            } catch (Exception e) {
                logger.catching(e)
            }
        })
    }

    private ScriptResult executeScript(Script script,  ScriptParameters parameters, ObjectContext context) {
        // mark context as script context to be able to suppress script callbacks originating from it
        context.setUserProperty(SCRIPT_CONTEXT_PROPERTY, true)

        def result = runScript(context.localObject(script), localizeParameters(context, parameters), context)

        ObjectContext localContext = cayenneService.getNewContext()
        def localScript = localContext.localObject(script)

        switch (result.getType()) {
            case SUCCESS:
                auditService.submit(localScript, AuditAction.SCRIPT_EXECUTED, String.format("Script '%s' executed successfully.", localScript.getName()))
                break
            case FAILURE:
                auditService.submit(localScript, AuditAction.SCRIPT_FAILED, String.format("Script '%s' failed: %s", localScript.getName(), result.getError()))
                break
            default:
                throw new IllegalStateException("Unknown script execution result type.")
        }

        localContext.commitChanges()

        return result
    }

    /**
     * Localizes all Cayenne objects inside parameters in the specified context.
     *
     * @param context - context to localize objects in
     * @param parameters - parameter list
     * @return localized parameters
     */
    private ScriptParameters localizeParameters(ObjectContext context, ScriptParameters parameters) {
        def localizedParameters = ScriptParameters.empty()

        for (parameter in parameters.asList()) {
            if (parameter.getValue() instanceof Persistent) {
                localizedParameters.add(parameter.getName(), context.localObject((Persistent) parameter.getValue()))
            } else {
                localizedParameters.add(parameter.getName(), parameter.getValue())
            }
        }

        return localizedParameters
    }
}
