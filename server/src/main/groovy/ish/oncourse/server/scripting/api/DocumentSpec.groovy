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

import ish.common.types.AttachmentInfoVisibility
import ish.oncourse.API
import ish.oncourse.server.cayenne.AttachableTrait
import ish.oncourse.server.cayenne.Document
import ish.oncourse.server.messaging.DocumentParam


/**
 * Document creation API.
 *
 * Simple usage example:
 *
 * ```
 * output = report {
 *		records someList
 *		keyCode ish.classReport
 *		background myFile
 *		}
 *
 * d = document {
 *      action "create"
 *      content output
 *      name "myreport"
 *      mimeType "image/pdf"
 *		permission AttachmentInfoVisibility.PUBLIC
 *		attach tutor_1, tutor_1
 * }
 * ```
 * Or just attach existing document to any attachable records
 * ```
 *  document {
 *      action "attach"
 *      document d
 *      attach tutor_1, enrolment_1, certificate_1
 * 	}
 * ```
 */
@API
class DocumentSpec {

	/**
	 * Create document action
	 */
	@API
	public final static String CREATE_ACTION = 'create'

	/**
	 * Attach document action
	 */
	@API
	public final static String ATTACH_ACTION = 'attach'

    String action
    byte[] content
    String name
    String mimeType
    AttachmentInfoVisibility permission
    List<AttachableTrait> attach = []
    Document document


	/**
	 * @param document existing document record to attach
	 */
	@API
	void document(Document document) {
		this.document = document
	}

	/**
	 * @param attach existing records to attach a document
	 */
	@API
	void attach(AttachableTrait... attach) {
		this.attach = attach.toList()
	}

	/**
	 * @param permission for newly created document
	 */
	@API
	void permission(AttachmentInfoVisibility permission) {
		this.permission = permission
	}

	/**
	 * @param name for newly created document
	 */
	@API
	void name(String name) {
		this.name = name
	}

	/**
	 * @param mimeType for newly created document
	 */
	@API
	void mimeType(String mimeType) {
		this.mimeType = mimeType
	}

	/**
	 * @param content for newly created document
	 */
	@API
	void content(byte[] content) {
		this.content = content
	}

	/**
	 * @param content as DocumentParam
	 */
	@API
	void content(DocumentParam documentParam) {
		this.content = documentParam.contentInBytes
	}

	/**
	 * Create new document or attach existing document
	 * @param action list action string
	 */
	@API
	void action(String action) {
		this.action = action
	}
}
