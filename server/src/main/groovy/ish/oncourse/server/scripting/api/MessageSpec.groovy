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
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.oncourse.API
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.messaging.AttachmentParam
import ish.util.MessageUtils
import org.apache.cayenne.PersistentObject


/**
 * Message sending API. Allows to create email or SMS.
 * You can render email content from the templates which are stored into onCourse or a text string.
 * The recipients are generated from entity records which are specified in closure.
 * But you also can add extra emails which will receive message with attachment.
 * If there is no attachment and specify template identifier, the message is stored inside the onCourse database. Otherwise, no.
 *
 * Simple usage example:
 * ```
 * message {
 *     template keyCodeOfMessageTemplate
 *     record records
 *     from "admin@example.com"
 * }
 * ```
 * The above example will render the template with the specified keyCode and will pass the entity records to that template.
 * It will send that message to the contacts which will be get from the records.
 *
 * You can optionally also pass a from address if you don't want to use the default email.from preference.
 *
 * ```
 * message {
 *     template keyCodeOfMessageTemplate
 *     record records
 *     anyBinding_1 anyValue_1
 *     anyBinding_2 anyValue_2
 * }
 * ```
 * If the template has a variables they must be specified as a list of variables in closure.
 *
 *
 * You can send emails with an attached file or pass your own content of message, without storing this data inside onCourse.
 *
 * Usage example:
 * ```
 *  message {
 *      record records
 *      from "admin@example.com"
 *      to "extrarecipient@example.com"
 *      cc "ccrecipient@example.com"
 *      bcc "bccrecipient@example.com"
 *      subject "test email"
 *      content "test email content"
 *      attachment "accounts.csv", "text/csv", account_csv_data
 *  }
 * ```
 * The only lines which are required are "record", "subject" and "content" or "record", "template" and "bindings"
 *
 *
 * Message collision functionality. Allows to skip sending messages automatically if contact have already received message with specified key.
 *
 * Usage example:
 * ```
 * message {
 *     template keyCodeOfMessageTemplate
 *     record records
 *     key "ABC", records
 *     keyCollision "drop"
 * }
 * ```
 */
@API
@CompileDynamic
class MessageSpec {

    String templateIdentifier
    List entityRecords = []
    Map<String, Object> bindings = [:]
    String fromAddress
    String subject
    String creatorKey
    SystemUser createdBy
    KeyCollision keyCollision = KeyCollision.accept

    List<String> toList = []
    List<String> ccList = []
    List<String> bccList = []
    List<AttachmentParam> attachments = []

    String fromName
    String content


    /**
     * Set message template to be used for rendering message body.
     * Could be used for Email and SMS sending
     *
     * @param templateIdentifier keyCode of the message template or name of the legacy message template
     */
    @API
    void template(String templateIdentifier) {
        this.templateIdentifier = templateIdentifier
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
     * Set "from" address for the email and name of the sender.
     *
     * @param email email from address
     * @param name name of the sender
     */
    @API
    void from(String email, String name) {
        this.fromAddress = email
        this.fromName = name
    }


    /**
     * A direct entity record to message.
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
     * Specify SystemUser who will be the linked to the resulting Message record in onCourse as its creator.
     *
     * @param user user who created the email
     */
    @API
    void createdBy(SystemUser user) {
        this.createdBy = user
    }


    /**
     * Attach a string key to this message and link it to an object. This reference can be used to ensure duplicates
     * aren't sent for the same event.
     *
     * @param key a string key which identifies the script or event which creates this message
     * @param object key is attached to a specific object (for example an enrolment or student)
     */
    @API
    void key(String key, PersistentObject... object){
        this.creatorKey = MessageUtils.generateCreatorKey(key, object)
    }


    /**
     * Defines a rule to prevent duplicates being sent.
     *
     * @param collision check type. Can be 'accept' (this is the default and means the key is ignored), 'drop' (which will silently drop the message) and 'error' (which will drop the message and log an error to the audit log).
     */
    @API
    void keyCollision(String collision){
        this.keyCollision = KeyCollision.valueOf(collision)
    }


    /**
     * Set email subject.
     * Using this method means that the message is not stored inside onCourse.
     *
     * @param subject email subject
     */
    @API
    void subject(String subject) {
        this.subject = subject
    }


    /**
     * Set plain text content of the email.
     * Could be used only for SMTP email sending
     * Using this method means that the message is not stored inside onCourse.
     *
     * @param content plain text content of the email
     */
    @API
    void content(String content) {
        this.content = content
    }


    /**
     * Specify extra contacts which will receive email. By default email specified in contact record will be used,
     * however you can override the default email by using ">>" operator, e.g. contact1 >> "anotheremail@example.com"
     * Using this method means that the message is not stored inside onCourse.
     *
     * @param recipients contact records who will receive the email
     */
    @API
    @CompileStatic(TypeCheckingMode.SKIP)
    void to(Contact... recipients) {
        this.toList.addAll(recipients.each { it.replacementEmail ?: it.email } as List<String>)
    }


    /**
     * Specify extra emails which will receive email.
     * Using this method means that the message is not stored inside onCourse.
     *
     * @param recipients email addresses of the email recipients
     */
    @API
    void to(String... recipients) {
        this.toList.addAll(recipients)
    }


    /**
     * Set CC recipients emails for the message.
     * Using this method means that the message is not stored inside onCourse.
     *
     * @param recipients email addresses of the CC recipients
     */
    @API
    void cc(String... recipients) {
        this.ccList = recipients.toList()
    }


    /**
     * Set BCC recipients emails for the message.
     * Using this method means that the message is not stored inside onCourse.
     *
     * @param recipients email address of the BCC recipients
     */
    @API
    void bcc(String... recipients) {
        this.bccList = recipients.toList()
    }


    /**
     * Add a file as attachment to the email.
     * Using this method means that the message is not stored inside onCourse.
     *
     * @param file a file
     */
    @API
    void attachment(File file) {
        if (file) {
            this.attachments << AttachmentParam.valueOf(file.getName() , null, file )
        }
    }


    /**
     * Add attachment to the email.
     * Using this method means that the message is not stored inside onCourse.
     *
     * @param contentType MIME type of the attachment
     * @param content MIME type of the attachment
     */
    @API
    void attachment(String contentType, Object content) {
        this.attachments << AttachmentParam.valueOf(null ,contentType, content)
    }


    /**
     * Add attachment to the email.
     * Using this method means that the message is not stored inside onCourse.
     *
     * @param fileName attached file name which will appear in the email
     * @param contentType MIME type of the attachment
     * @param content attachment object
     */
    @API
    void attachment(String fileName, String contentType, Object content) {
        this.attachments << AttachmentParam.valueOf(fileName, contentType, content)
    }


    /**
     * Add attachment to the email.
     * Using this method means that the message is not stored inside onCourse.
     *
     * @param attachment attachment properties map, e.g. [fileName: 'example.txt', type: 'text/plain', content: 'test text']
     */
    @API
    void attachment(Map<String, Object> attachment) {
        this.attachments << AttachmentParam.valueOf((String) attachment.fileName, (String) attachment.type, attachment.content)
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
