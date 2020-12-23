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

package ish.oncourse.server.scripting.api

import ish.oncourse.API

/**
 * Use a script to generate an export.
 *
 * Usage example:
 * ```
 * export {
 *     template "ish.contact.list"
 *     records overdueDebtors
 * }
 * ```
 */
@API
class ExportSpec {
	String templateKeyCode
    List entityRecords = []

    /**
     * Pass the keycode of the export template you want to use
     *
     * @param template keycode
     */
    @API
	def template(String templateKeyCode) {
		this.templateKeyCode = templateKeyCode
	}

    /**
     * A list of records to export. Obviously the export template needs to be
     * built to accept a list of this type of object and produce an export file from them.
     *
     * @param records
     */
    @API
	def records(List records) {
		this.entityRecords = records
	}

    /**
     * You can instead pass records as vargs if you only have a few to pass.
     *
     * @param records
     */
    @API
	def records(Object... records) {
		this.entityRecords = records.toList()
	}

	/**
	 * A list of records to export.
	 * This is the same as 'records', but this needs to handling a export card which is added in Script API
	 *
	 * @param records
	 */
	@API
	def record(List records) {
		this.entityRecords = records
	}

	/**
	 * You can also pass one record to export.
	 *
	 * @param record
	 */
	@API
	def record(Object record) {
		this.entityRecords.add(record)
	}
}
