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

package ish.oncourse.server.quality.api

import groovy.transform.CompileDynamic
import groovy.xml.MarkupBuilder
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import ish.oncourse.types.Severity
/**
 * QualityResult building API.
 *
 * Usage example:
 *
 * ```
 * result {
 *   message "${enrolmentsToFix.size()} outcomes have completed more than 7 days ago and don't yet have a result entered"
 *   records enrolmentsToFix
 *   severity Severity.WARNING
 * }
 * ```
 *
 * or if you don't want to specify records, the message will be associated with the entity in general
 *
 * ```
 * result {
 *   message "You have not yet created any sites."
 *   entity "Site"
 *   severity Severity.WARNING
 * }
 * ```
 *
 */
@CompileDynamic
class QualityResultSpec {

	Map<String, Collection<Long>> recordsToFix
	String messageResult
	int severity

	/**
	 * Sets result HTML message using MarkupBuilder object passed to it. E.g.
	 *
     * ```
	 * message builder.html {
	 * 	p("${enrolmentsToFix.size()} outcomes have completed more than 7 days ago and don't yet have a result entered")
	 * }
     * ```
     *
     * Typically you don't need to specify html in the message unless you have particular styling you need to apply.
	 *
	 * @param markupBuilder a helper class for creating XML or HTML markup
	 */
	void message(MarkupBuilder markupBuilder) {
		this.messageResult = markupBuilder.writer.toString()
	}

	/**
	 * Sets plain text result message. Make this short enough to display in a small user interface widget and
     * alert the user to the problem they need to rectify
     *
     * @param message Plain text message
	 */
	void message(String message) {
		this.messageResult = message
	}

    /**
     * For granular severity levels you can pass an integer here. This allows you to carefully
     * prioritise different rules against each other.
     *
     * @param severity an integer between 0 (lowest severity) and 100 (most severe)
     */
	void severity(int severity) {
		this.severity = severity
	}

    /**
     * If you just want to use one of the built-in severity levels, then this will do the trick.
     * Severity constants correspond to colouring in the UI.
     *
     * @param severity a severity constant
     */
	void severity(Severity severity) {
		this.severity = severity.level
	}

	void records(Map recordsToFix) {
		this.recordsToFix = recordsToFix
	}

    /**
     * If this rule defines particular records to fix, then pass them here. Those records will have the
     * message you created shown in the user interface against each record.
     *
     * @param listOfRecords list of records need to be fixed
     */
	void records(List<CayenneDataObject> listOfRecords) {
		this.recordsToFix = listOfRecords.inject([:]) { map, CayenneDataObject record ->
			map[record.entityName] = (map[record.entityName] ?: []) << record.id
			return map
		}
	}

    /**
     * If the rule doesn't specify certain records which need fixing, then just pass the entity name.
     * This is not very common.
     *
     * @param entity the case sensitive name of the entity as text
     */
	void entity(String entity) {
		this.recordsToFix = [entity: []]
	}
}
