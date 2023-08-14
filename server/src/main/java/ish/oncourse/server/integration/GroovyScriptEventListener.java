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
import ish.common.types.AutomationStatus;
import ish.common.types.SystemEventType;
import ish.common.types.TriggerType;
import ish.oncourse.common.SystemEvent;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.Script;
import ish.oncourse.server.cayenne.Tag;
import ish.oncourse.server.cayenne.TagRelation;
import ish.oncourse.server.scripting.GroovyScriptService;
import ish.oncourse.server.scripting.ScriptParameters;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;

import java.util.List;

import static ish.common.types.SystemEventType.*;

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

		for (var script : scriptsToExecute){
			var value = transformEventValue(event.getValue());
			var eventType = event.getEventType();
			if(eventType.equals(CHECKLIST_TASK_CHECKED) || eventType.equals(CHECKLIST_COMPLETED)){
				if(!correctTagPinned((TagRelation) value, script, true)){
					continue;
				}
			}

			if(eventType.equals(TAG_ADDED) || eventType.equals(TAG_REMOVED)) {
				if(!correctTagPinned((TagRelation) value, script, false)){
					continue;
				}
			}
			groovyScriptService.runScript(script, ScriptParameters.from(VALUE_BINDING_NAME, value).fillDefaultParameters(value));
		}
	}

	private boolean correctTagPinned(TagRelation value, Script script, boolean parentTag){
		if(!checkRelationClass(value, script))
			return false;

		if(script.getEntityAttribute() != null){
			Tag tag;
			if(parentTag && value.getTag().getParentTag() != null)
				tag = value.getTag().getParentTag();
			else
				tag = value.getTag();

			if(tag != null)
				return checkTagId(tag, script);
		}
		return true;
	}

	private boolean checkRelationClass(TagRelation value, Script script){
		if(script.getEntityClass() != null && !script.getEntityClass().isEmpty() && value.getTaggedRelation() != null){
			return value.getTaggedRelation().getClass().getSimpleName().equals(script.getEntityClass());
		}
		return true;
	}

	private boolean checkTagId(Tag tag, Script script){
		return tag.getId().equals(Long.parseLong(script.getEntityAttribute()));
	}

	private List<Script> getScriptsForEventType(SystemEventType eventType) {
		ObjectContext context = cayenneService.getNewContext();

		return ObjectSelect.query(Script.class).where(Script.AUTOMATION_STATUS.eq(AutomationStatus.ENABLED)
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
