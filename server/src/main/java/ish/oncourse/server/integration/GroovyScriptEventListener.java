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
package ish.oncourse.server.integration;

import com.google.inject.Inject;
import ish.common.types.SystemEventType;
import ish.common.types.TriggerType;
import ish.oncourse.common.SystemEvent;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.Script;
import ish.oncourse.server.scripting.GroovyScriptService;
import ish.oncourse.server.scripting.ScriptParameters;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;

import java.util.List;

/**
 * {@link SystemEvent} listener triggering script execution.
 */
public class GroovyScriptEventListener implements OnCourseEventListener {


	public static final String VALUE_BINDING_NAME = "value";

	private ICayenneService cayenneService;
	private GroovyScriptService groovyScriptService;

	@Inject
	public GroovyScriptEventListener(ICayenneService cayenneService, GroovyScriptService groovyScriptService) {
		this.cayenneService = cayenneService;
		this.groovyScriptService = groovyScriptService;
	}

	@Override
	public void dispatchEvent(SystemEvent event) {
		var scriptsToExecute = getScriptsForEventType(event.getEventType());

		for (var script : scriptsToExecute) {
			var value = transformEventValue(event.getValue());
			groovyScriptService.runScript(script,
					ScriptParameters.from(VALUE_BINDING_NAME, value)
									.add(GroovyScriptService.RECORD_PARAM_NAME, value)
									.add(GroovyScriptService.ENTITY_PARAM_NAME, value)
			);
		}
	}

	private List<Script> getScriptsForEventType(SystemEventType eventType) {
		ObjectContext context = cayenneService.getNewContext();

		return ObjectSelect.query(Script.class).where(Script.ENABLED.eq(true)
				.andExp(Script.TRIGGER_TYPE.eq(TriggerType.ONCOURSE_EVENT).andExp(Script.SYSTEM_EVENT_TYPE.eq(eventType))))
				.select(context);
	}

	private Object transformEventValue(Object value) {
		if (value != null && value instanceof ObjectId) {
			return SelectById.query(Object.class, (ObjectId) value).selectOne(cayenneService.getNewContext());
		}

		return value;
	}
}
