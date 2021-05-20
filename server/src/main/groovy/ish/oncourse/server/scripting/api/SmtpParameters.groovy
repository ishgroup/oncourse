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

import ish.oncourse.server.messaging.DocumentParam

/**
 * Created by anarut on 8/17/16.
 */
class SmtpParameters {

    String fromAddress
    String fromName
    String subject
    String content
    String creatorKey
    KeyCollision keyCollision = KeyCollision.accept
    List<String> toList = []
    List<String> ccList = []
    List<String> bccList = []
    String multipartType
    List<DocumentParam> attachments
    String templateIdentifier
    Map<String, Object> bindings

     SmtpParameters(EmailSpec spec) {
        fromAddress = spec.fromAddress
        fromName = spec.fromName
        subject = spec.subject
        content = spec.content
        creatorKey = spec.creatorKey
        keyCollision = spec.keyCollision
        toList = spec.toList
        ccList = spec.ccList
        bccList = spec.bccList
        multipartType = spec.multipartType
        attachments = spec.attachments
        templateIdentifier = spec.templateName
        bindings = spec.bindings
    }

    SmtpParameters(MessageSpec spec) {
        fromAddress = spec.fromAddress
        fromName = spec.fromName
        subject = spec.subject
        content = spec.content
        creatorKey = spec.creatorKey
        keyCollision = spec.keyCollision
        spec.toList.each { toList.add(it) }
        spec.ccList.each { ccList.add(it) }
        spec.bccList.each { bccList.add(it) }
        attachments = spec.attachments
        templateIdentifier = spec.templateIdentifier
        bindings = spec.bindings
    }

     SmtpParameters(SmtpSpec spec) {
   
        subject = spec.subject
        content = spec.content
        
        multipartType = spec.multipartType
        attachments = spec.attachments
    }


    SmtpParameters(String fromAddress, String fromName, String to, String subject, String content) {
        this.fromAddress = fromAddress
        this.fromName = fromName
        this.toList = [to]
        this.subject = subject
        this.content = content
    }
}
