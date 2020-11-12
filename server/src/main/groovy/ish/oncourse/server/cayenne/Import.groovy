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

import ish.oncourse.API
import ish.oncourse.server.cayenne.glue._Import

import javax.annotation.Nonnull
import java.util.Date

/**
 * Import describes the way some external format (XML, CSV, etc.) can be transformed into onCourse records.
 * Imports in onCourse rely on Groovy script for defining transformation logic.
 */
@API
class Import extends _Import implements AutomationTrait {



	/**
	 *
	 * @return The groovy script which processes the import
	 */
	@Nonnull
	@API
	@Override
	String getScript() {
		return super.getScript()
	}

	/**
	 *
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 *
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	@Override
	Class<? extends AutomationBinding> getAutomationBindingClass() {
		return ImportAutomationBinding
	}

	@Override
	void setBody(String body) {
		super.setScript(body)
	}
/**
	 *
	 * @return the unique keycode for this import
	 */
	@Nonnull
	@API
	@Override
	String getKeyCode() {
		return super.getKeyCode()
	}

	/**
	 *
	 * @return the visible name for this import in the UI
	 */
	@Nonnull
	@API
	@Override
	String getName() {
		return super.getName()
	}

	@Override
	String getBody() {
		return super.getScript()
	}
}



