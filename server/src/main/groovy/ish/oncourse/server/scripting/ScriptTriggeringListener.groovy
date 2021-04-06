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

import ish.common.types.TriggerType
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.ISHDataContext
import ish.oncourse.server.cayenne.Preference
import ish.oncourse.server.cayenne.Script
import ish.persistence.Preferences
import ish.scripting.ScriptResult
import org.apache.cayenne.DataChannelSyncFilter
import org.apache.cayenne.DataChannelSyncFilterChain
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.annotation.PostPersist
import org.apache.cayenne.annotation.PostRemove
import org.apache.cayenne.annotation.PostUpdate
import org.apache.cayenne.annotation.PreRemove
import org.apache.cayenne.graph.GraphDiff
import org.apache.cayenne.map.LifecycleEvent
import org.apache.cayenne.query.SelectQuery
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class ScriptTriggeringListener implements DataChannelSyncFilter {

	private static final Logger logger = LogManager.getLogger(ScriptTriggeringListener.class)

	private static final ThreadLocal<Deque<List<EventRecordMapping>>> RECORD_STORAGE = new ThreadLocal<>()

	private GroovyScriptService scriptService
	private ICayenneService cayenneService

	ScriptTriggeringListener(GroovyScriptService scriptService, ICayenneService cayenneService) {
		this.scriptService = scriptService
		this.cayenneService = cayenneService
	}

	@Override
	GraphDiff onSync(ObjectContext originatingContext, GraphDiff changes, int syncType, DataChannelSyncFilterChain filterChain) {

		Deque<List<EventRecordMapping>> stack = RECORD_STORAGE.get()

		if (stack == null) {
			stack = new LinkedList<>()
			RECORD_STORAGE.set(stack)
		}

		List<EventRecordMapping> eventMappings = stack.isEmpty() ? new ArrayList<EventRecordMapping>() : stack.peek()

		try {
			stack.push(eventMappings)
			return filterChain.onSync(originatingContext, changes, syncType)
		} finally {
			stack.pop()

			if (stack.isEmpty()) {
				// if commit is coming from inside a groovy script,
				// do not execute any entity scripts at all to avoid callback cycles
				if (!Boolean.TRUE.equals(originatingContext.getUserProperty(GroovyScriptService.SCRIPT_CONTEXT_PROPERTY))) {
					eventMappings.each { mapping ->
						triggerEntityEvent(mapping.getEvent(), mapping.getRecord())
					}
				}
			}

			if (RECORD_STORAGE.get() != null && RECORD_STORAGE.get().isEmpty()) {
				RECORD_STORAGE.set(null)
			}
		}
	}

	@PostPersist(value = Script.class)
	void postPersist(Script script) {
		scriptService.scriptAdded(script, null)
	}

	@PostUpdate(value = Script.class)
	void postUpdate(Script script) {
		scriptService.scriptChanged(script)
	}

	@PostRemove(value = Script.class)
	void postRemove(Script script) {
		scriptService.scriptDeleted(script)
	}

	@PostPersist(value = Preference.class)
	void postPersist(Preference preference) {
		handleDefaultTimeZonePreferenceChange(preference)
	}

	@PostUpdate(value = Preference.class)
	void postUpdate(Preference preference) {
		handleDefaultTimeZonePreferenceChange(preference)
	}

	@PostPersist
	void postPersist(Object entity) {
		RECORD_STORAGE.get().peek().add(new EventRecordMapping(LifecycleEvent.POST_PERSIST, entity))
	}

	@PreRemove
	void preRemove(Object entity) {
		scriptService.getScriptsForEntity(entity.getClass(), LifecycleEvent.PRE_REMOVE).each {script ->
			scriptService.runAndWait(script, new ScriptParameters().fillDefaultParameters(entity), { ->
				ObjectContext context = (ISHDataContext) cayenneService.getNewContext()
				context.setReadOnly(true)
				return context
			},  { Exception e ->
				logger.catching(e)
				return ScriptResult.failure(e.getMessage())
			})
		}
	}

	private void triggerEntityEvent(LifecycleEvent eventType, Object entity) {
		Set<Script> scripts = scriptService.getScriptsForEntity(entity.getClass(), eventType)

		scripts.each { script->
			if (script != null) {
				scriptService.runScript(script, new ScriptParameters().fillDefaultParameters(entity))
			}
		}
	}

	private void handleDefaultTimeZonePreferenceChange(Preference preference) {
		// reschedule all CRON triggered scripts in case default server time zone preference has changed
		if (Preferences.ONCOURSE_SERVER_DEFAULT_TZ == preference.getName()) {
			List<Script> scripts = preference.getObjectContext().select(SelectQuery.query(
					Script.class, Script.TRIGGER_TYPE.eq(TriggerType.CRON)))

			scripts.each { script -> scriptService.scriptChanged(script)}
		}
	}

	static class EventRecordMapping {
		private LifecycleEvent event
		private Object record

		EventRecordMapping(LifecycleEvent event, Object record) {
			this.event = event
			this.record = record
		}

		LifecycleEvent getEvent() {
			return event
		}

		Object getRecord() {
			return record
		}
	}
}
