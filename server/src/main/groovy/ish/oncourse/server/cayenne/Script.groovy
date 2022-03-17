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

package ish.oncourse.server.cayenne

import ish.common.types.AutomationStatus
import ish.common.types.EntityEvent
import ish.common.types.TriggerType
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._Script

import javax.annotation.Nonnull
import java.util.Date

/**
 * Definition of script instance. onCourse supports scripts written in Groovy programming language.
 * Scripts can be executed on demand or scheduled to be executed at certain time (cron schedule) or
 * when certain event happens (record creation/removal, new enrolment etc.)
 * onCourse has variety of pre defined scripts to satisfy common needs but also allows users to add
 * their own scripts to solve specific problems.
 */
@API
@QueueableEntity
class Script extends _Script implements Queueable, AutomationTrait {
	public static final String ENABLED_KEY = "enabled"

	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
    Date getCreatedOn() {
		return super.getCreatedOn()
    }

	/**
	 * @return cron schedule for this script
	 */
	@API
	@Override
    String getCronSchedule() {
		return super.getCronSchedule()
    }

	/**
	 * @return true if this script is enabled
	 */
	@Nonnull
	@API
    Boolean getEnabled() {
		return super.getAutomationStatus().equals(AutomationStatus.ENABLED)
    }

	/**
	 * @return status of automation: not installed, installed but disabled, enabled
	 */
	@Nonnull
	@Override
	AutomationStatus getAutomationStatus() {
		return super.getAutomationStatus()
	}

	/**
	 * @return name of entity whose change triggers this script
	 */
	@API
	@Override
    String getEntityClass() {
		return super.getEntityClass()
    }

	/**
	 * @return entoty event type which triggers this script: "create", "update", "remove" or "create or update"
	 */
	@Override
    EntityEvent getEntityEventType() {
		return super.getEntityEventType()
    }

	/**
	 * @return the date and time this record was modified
	 */
	@API
	@Override
    Date getModifiedOn() {
		return super.getModifiedOn()
    }

	@Override
	Class<? extends AutomationBinding> getAutomationBindingClass() {
		return ScriptAutomationBinding
	}

	@Override
	void setEntity(String entity) {
		super.setEntityClass(entity)
	}

	@Override
	void setBody(String script) {
		super.setScript(script)
	}
/**
	 * @return name of this script
	 */
	@Nonnull
	@API
	@Override
    String getName() {
		return super.getName()
    }

	@Override
	String getEntity() {
		return super.getEntityClass()
	}

	@Override
	String getBody() {
		return super.getScript()
	}
/**
	 * @return script content
	 */
	@Nonnull
	@API
	@Override
    String getScript() {
		return super.getScript()
    }

	/**
	 * @return this script's trigger type: on demand, cron, entity evet or onCourse event
	 */
	@Nonnull
	@Override
    TriggerType getTriggerType() {
		return super.getTriggerType()
    }


}
