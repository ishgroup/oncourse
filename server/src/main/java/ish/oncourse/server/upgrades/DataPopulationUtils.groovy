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

package ish.oncourse.server.upgrades

import ish.common.types.EntityEvent
import ish.common.types.MessageType
import static ish.oncourse.common.ResourceType.EXPORT
import static ish.oncourse.common.ResourceType.IMPORT
import static ish.oncourse.common.ResourceType.MESSAGING
import ish.oncourse.server.cayenne.AutomationTrait
import ish.oncourse.server.cayenne.EmailTemplate
import ish.oncourse.server.cayenne.EmailTemplateAutomationBinding
import ish.oncourse.server.cayenne.ExportTemplate
import ish.oncourse.server.cayenne.ExportTemplateAutomationBinding
import ish.oncourse.server.cayenne.Import
import ish.oncourse.server.cayenne.ImportAutomationBinding
import ish.oncourse.server.cayenne.Message
import ish.oncourse.types.OutputType
import ish.common.types.SystemEventType
import ish.common.types.TriggerType
import ish.oncourse.common.ResourceProperty
import static ish.oncourse.common.ResourceProperty.*
import ish.oncourse.common.ResourceType
import static ish.oncourse.common.ResourceType.SCRIPT
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.cayenne.Script
import ish.oncourse.server.cayenne.ScriptAutomationBinding
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.io.IOUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.nio.charset.Charset

class DataPopulationUtils {

    private static final Logger logger = LogManager.logger

    private static Boolean getBoolean(Map<String, Object> props, ResourceProperty property) {
        get(props, property, Boolean)
    }

    private static String getBody(Map<String, Object> props, ResourceProperty property, ResourceType type) {
        String path = type.resourcePath + getString(props, property)
        InputStream iStream = ResourcesUtil.getResourceAsInputStream(path)
        return IOUtils.toString(iStream, Charset.defaultCharset())
    }

    static String getString(Map<String, Object> props, ResourceProperty property) {
        get(props, property, String)
    }

    static <T> T get(Map<String, Object> props, ResourceProperty property, Class<T> clazz) {
        Object obj = props[property.displayName]
        if (obj == null) {
            return null
        } else if (clazz.isEnum()) {
            clazz.enumConstants.find { (it as Enum).name().equalsIgnoreCase(obj as String) }
        } else {
            return obj as T
        }
    }


    static void updateScript(ObjectContext context, Map<String, Object> props) {
        boolean keepOldScript = false
        String name = getString(props, NAME)
        String keyCode = getString(props, KEY_CODE)

        Script oldScript = (ObjectSelect.query(Script).where(Script.NAME.eq(name)) & Script.KEY_CODE.ne(keyCode).orExp(Script.KEY_CODE.isNull())).selectOne(context)
        if (oldScript) {
            if (oldScript.enabled != null && oldScript.enabled) {
                oldScript.name = oldScript.name + " (old)"
            } else {
                logger.warn("remove disabled script $oldScript.name, \n $oldScript.body")
                context.deleteObject(oldScript)
            }
            context.commitChanges()
        }

        Script newScript = ObjectSelect.query(Script).where(Script.KEY_CODE.eq(keyCode)).selectOne(context)
        if (!newScript) {
            newScript = context.newObject(Script)
            newScript.keyCode = keyCode
            newScript.enabled = oldScript ? Boolean.FALSE : getBoolean(props, ENABLED)
            newScript.cronSchedule = getString(props, CRON_SCHEDULE)
        }

        newScript.name = name
        newScript.entity = getString(props, ENTITY_CLASS)
        newScript.entityAttribute = getString(props, ENTITY_ATTRIBUTE)

        newScript.triggerType = updateTriggerType(newScript.triggerType, get(props, TRIGGER_TYPE, TriggerType))
        newScript.outputType = get(props, OUTPUT_TYPE, OutputType)
        newScript.entityEventType = get(props, ENTITY_EVENT_TYPE, EntityEvent)
        newScript.systemEventType = get(props, ON_COURSE_EVENT_TYPE, SystemEventType)

        newScript.description = getString(props, DESCRIPTION)
        newScript.body = getBody(props, BODY, SCRIPT)

        context.commitChanges()

        BindingUtils.updateOptions(context, get(props, OPTIONS, List), newScript, ScriptAutomationBinding)
        BindingUtils.updateVariables(context, get(props, VARIABLES, List),newScript, ScriptAutomationBinding)

    }


    static void updateImport(ObjectContext context, Map<String, Object> props) {
        String keyCode = getString(props, KEY_CODE)

        Import dbImport = ObjectSelect.query(Import).where(Script.KEY_CODE.eq(keyCode)).selectOne(context)

        if (!dbImport) {
            dbImport = context.newObject(Import)
            dbImport.keyCode = keyCode
            dbImport.enabled = getBoolean(props, ENABLED)
        }
        dbImport.name = getString(props, NAME)
        dbImport.body = getBody(props, BODY, IMPORT)
        dbImport.description = getString(props, DESCRIPTION)

        context.commitChanges()

        BindingUtils.updateOptions(context, get(props, OPTIONS, List), dbImport, ImportAutomationBinding)
        BindingUtils.updateVariables(context, get(props, VARIABLES, List),dbImport, ImportAutomationBinding)
    }


    static void updateExport(ObjectContext context, Map<String, Object> props) {
        String keyCode = getString(props, KEY_CODE)

        ExportTemplate dbExport = ObjectSelect.query(ExportTemplate).where(ExportTemplate.KEY_CODE.eq(keyCode)).selectOne(context)
        if (!dbExport) {
            dbExport = context.newObject(ExportTemplate)
            dbExport.keyCode = keyCode
            dbExport.enabled = Boolean.TRUE
        }

        dbExport.name = getString(props, NAME)
        dbExport.entity = getString(props, ENTITY_CLASS)
        dbExport.description = getString(props, DESCRIPTION)
        dbExport.outputType = get(props, OUTPUT_TYPE, OutputType)
        dbExport.body = getBody(props, BODY, EXPORT)

        context.commitChanges()

        BindingUtils.updateOptions(context, get(props, OPTIONS, List), dbExport, ExportTemplateAutomationBinding)
        BindingUtils.updateVariables(context, get(props, VARIABLES, List), dbExport, ExportTemplateAutomationBinding)
    }


    static void updateMessage(ObjectContext context, Map<String, Object> props) {
        String keyCode = getString(props, KEY_CODE)

        EmailTemplate dbMessage = ObjectSelect.query(EmailTemplate).where(EmailTemplate.KEY_CODE.eq(keyCode)).selectOne(context)
        if (!dbMessage) {
            dbMessage = context.newObject(EmailTemplate)
            dbMessage.keyCode = keyCode
            dbMessage.enabled = Boolean.TRUE
        }

        dbMessage.name = getString(props, NAME)
        dbMessage.entity = getString(props, ENTITY_CLASS)
        dbMessage.description = getString(props, DESCRIPTION)
        dbMessage.type = get(props, MESSAGE_TYPE, MessageType)
        dbMessage.subject = getString(props, SUBJECT)
        dbMessage.bodyPlain = getBody(props, TXT_TEMPLATE, MESSAGING)
        if (getString(props, HTML_TEMPLATE)) {
            dbMessage.bodyHtml = getBody(props, HTML_TEMPLATE, MESSAGING)
        }

        context.commitChanges()

        BindingUtils.updateOptions(context, get(props, OPTIONS, List), dbMessage, EmailTemplateAutomationBinding.class);
        BindingUtils.updateVariables(context, get(props, VARIABLES, List), dbMessage, EmailTemplateAutomationBinding.class);
    }


    static void removeFromDbDeletedResources(ObjectContext context, List serverResources, ResourceType type) {
        List<? extends AutomationTrait> resourcesToRemove = ObjectSelect.query(type.getCayenneClass()).select(context)
                .findAll { dbResource -> dbResource.keyCode &&
                        dbResource.keyCode.startsWith("ish.") &&
                        !serverResources.collect { it.getAt("keyCode") }.contains(dbResource.keyCode)
                }
        context.deleteObjects(resourcesToRemove)
    }


    private static TriggerType updateTriggerType(TriggerType oldType, TriggerType newType) {
        if (newType == null) {
            return null
        }
        if (newType == oldType) {
            return oldType
        }
        if ([TriggerType.ON_DEMAND, TriggerType.CRON].contains(oldType)) {
            if ([TriggerType.ON_DEMAND, TriggerType.CRON].contains(newType)) {
                return oldType
            }
        }
        return newType
    }
}
