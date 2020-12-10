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

import groovy.transform.CompileDynamic
import ish.oncourse.API
import ish.oncourse.server.messaging.AttachmentParam


/**
 * Use a script to generate an message.
 *
 * Usage example:
 * ```
 * message {
 *     template "ish.email.simple"
 *     record enrolments or records enrolments
 *     from "support@ish.com.au"
 *     anyBinding_1 anyValue_1
 *     anyBinding_2 anyValue_2
 *     ....
 * }
 * ```
 */
@API
@CompileDynamic
class MessageSpec {
    String templateName
    String fromAddress
    List entityRecords = []

    Map<String, Object> bindings = [:]
    List<AttachmentParam> attachments = []
    List<String> bccList = []
    
    /**
     * Set BCC recipients for the email. Using this method means that the message is not stored inside onCourse.
     *
     * @param recipients email address of the BCC recipients
     */
    @API
    void bcc(String... recipients) {
        this.bccList = recipients.toList()
    }

    /**
     * Add attachment to the email.
     *
     * @param contentType MIME type of the attachment
     * @param content MIME type of the attachment
     */
    void attachment(String contentType, Object content) {
        this.attachments << AttachmentParam.valueOf(null ,contentType, content)
    }

    /**
     * Add attachment to the email.
     *
     * @param fileName attached file name which will appear in the email
     * @param contentType MIME type of the attachment
     * @param content attachment object
     */
    void attachment(String fileName, String contentType, Object content) {
        this.attachments << AttachmentParam.valueOf(fileName, contentType, content)
    }

    /**
     * Add attachment to the email.
     *
     * @param attachment attachment properties map, e.g. [fileName: 'example.txt', type: 'text/plain', content: 'test text']
     */
    void attachment(Map<String, Object> attachment) {
        this.attachments << AttachmentParam.valueOf((String) attachment.fileName, (String) attachment.type, attachment.content)
    }

    /**
     * Set message template to be used for rendering message body.
     * Could be used for Email and SMS sending
     *
     * @param templateName name of the message template
     */
    @API
    void template(String templateName) {
        this.templateName = templateName
    }


    /**
     * Set "from" address for the email. Defaults to "email.from" preference if not set.
     *
     * @param email email from address
     */
    @API
    void from(String email) {
        this.fromAddress = email
    }




    /**
     * A direct record to message.
     *
     * @param record
     */
    @API
    void record(Object record) {
        this.entityRecords.add(record)
    }


    /**
     * A list of records to message. Obviously the message template needs to be
     * built to accept a list of this type of object and produce a message from them.
     *
     * @param records
     */
    @API
    void record(List records) {
        this.entityRecords = records
    }

    /**
     * A list of records to message. Obviously the message template needs to be
     * built to accept a list of this type of object and produce a message from them.
     *
     * @param records
     */
    @API
    void records(List records) {
        this.entityRecords = records
    }


    /**
     * A map of bindings which is specified by one by one
     *
     * @param key name of binding
     * @param value of binding
     */
    void methodMissing(String key, args) {
        def arg = args.find()
        bindings.put(key, arg)
    }
}
