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

import groovy.transform.CompileStatic
import ish.export.ExportTemplateInterface
import ish.oncourse.API
import ish.oncourse.server.cayenne.glue._ExportTemplate
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable
/**
 * Export template describes the way records from onCourse are transformed into specific export format (XML, CSV, etc.)
 * Export templates in onCourse rely on Groovy script for defining transformation logic.
 */
@API
@CompileStatic
class ExportTemplate extends _ExportTemplate implements ExportTemplateInterface, AutomationTrait {


	private static final Logger logger = LogManager.getLogger()

	@Override
	void setExportTemplate(@Nullable final String template) {
		if (template != null) {
			setScript(template)
		} else {
			setScript(null)
		}
	}

	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * @return type of entity this export template can be applied to
	 */
	@Nonnull
	@API
	@Override
	String getEntity() {
		return super.getEntity()
	}

	/**
	 * @return unique key code identifier of this export template
	 */
	@Nonnull
	@API

    String getKeyCode() {
		return super.getKeyCode()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return name of this export template
	 */
	@Nonnull
	@API
	@Override
	String getName() {
		return super.getName()
	}

	/**
	 * @return body of this export template
	 */
	@Nonnull
	@API
	@Override
	String getBody() {
		return super.getScript()
	}

	void setBody(String body) {
		super.setScript(body)
	}

    /**
     * @return all the automation bindings for this record, including both variables and options
     */
    @Nonnull
	@Override
	List<ExportTemplateAutomationBinding> getAutomationBindings() {
		return super.automationBindings
	}

	Class<? extends AutomationBinding> getAutomationBindingClass() {
		return ExportTemplateAutomationBinding
	}
}
