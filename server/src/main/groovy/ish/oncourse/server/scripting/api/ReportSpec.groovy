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
 * Run a report and create a PDF file. This file can then be stored in the document management system or sent attached to an email.
 *
 * Simple example to produce a single certificate PDF:
 *
 * ```
 * report {
 *     keycode "ish.onCourse.certificate"
 *     records c
 * }
 * ```
 *
 * Usage example with sending an email:
 * ```
 * email {
 *      to preference.email.admin
 *      subject "onCourse transaction summary ${startDate.format("dd/MM/yy")} to ${endDate.format("dd/MM/yy")}"
 *      content "'Trial Balance' report for the previous 7 days."
 *      attachment "Trial_Balance.pdf", "application/pdf", report {
 *          keycode "ish.onCourse.trialBalance"
 *          records accounts
 *          param 'localdateRange_from' : startDate, 'localdateRange_to' : endDate
 *      }
 * }
 * ```
 */
@API
class ReportSpec {

	String fileName
	String keyCode
	String entity
	List entityRecords = []
	String background
	Map param  = [:]
	Boolean generatePreview = false

	/**
	 * Specify the filename of the final file with a fulfilled report
	 *
	 * @param fileName fileName of the file with the report
	 */
	@API
	void fileName(String fileName) {
		this.fileName = fileName
	}

	/**
	 * Pass the keycode of the report template in onCourse you want to run.
     *
	 * @param keyCode keyCode of the report passed to the report engine
	 */
	@API
	void keycode(String keyCode) {
		this.keyCode = keyCode
	}

	void entity(String entity) {
		this.entity = entity
	}

	/**
	 * Pass a record to the report engine. This record will be iterated through in the report engine
	 * to produce the report output.
	 *
	 * @param record record which is included in the report
	 */
	@API
	void record(Object record) {
		this.entityRecords.add(record)
	}

	/**
	 * Pass a record to the report engine. This record will be iterated through in the report engine
	 * to produce the report output.
	 *
	 * @param record record which is included in the report
	 */
	@API
	void record(List records) {
		this.entityRecords = records
	}

	/**
	 * Pass a list of records to the report engine. These records will be iterated through in the report engine
     * to produce the report output.
     *
	 * @param records records which are included in the report
	 */
	@API
	void records(List records) {
		this.entityRecords = records
	}

	/**
	 * Pass a record to the report engine.
     *
	 * @param record record which is included in the report
	 */
	@API
	void records(Object... records) {
		this.entityRecords = Arrays.asList(records)
	}

	/**
	 * The name of the background the report engine will use to render the output PDF file. The background sits behind
     * the components printed on the report. This is useful for a header or certificate background.
     *
	 * @param background name of background. You can skip this option for a white background.
	 */
	@API
	void background(String background) {
		this.background = background
	}

	/**
	 * Additional optional parameters passed to the report engine.
	 * Reports must be provided with the correct number and types they accept to run. For example:
     *
     * ```
     * report {
     *     keycode "ish.onCourse.trialBalance"
     *     records accounts
     *     param 'localdateRange_from' : startDate, 'localdateRange_to' : endDate
     * }
     * ```
     *
	 * @param param parameters passed to the report
	 */
	@API
	void param(Map param) {
		this.param = param
	}

	@API
	void generatePreview(Boolean generatePreview) {
		this.generatePreview = generatePreview
	}
}
