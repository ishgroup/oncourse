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

package ish.oncourse.server.messaging

import groovy.transform.CompileDynamic

@CompileDynamic
class MailDeliveryParam {

    private GetFrom getFrom
    private GetEnvelopeFrom getEnvelopeFrom
    private GetAddresses getAddressesTO
    private GetAddresses getAddressesCC
    private GetAddresses getAddressesBCC
    private GetSubject getSubject
    private GetContent getContent
    private GetAddresses getAddressesReplyTo
    private Date sentDate = new Date()

    private MailDeliveryParam() {

    }

    static MailDeliveryParam valueOf(GetFrom getFrom, GetEnvelopeFrom getEnvelopeFrom, GetAddresses getAddressesTO, GetSubject getSubject, GetContent getContent) {
        valueOf(getFrom, getEnvelopeFrom, getAddressesTO, GetAddresses.empty(), GetAddresses.empty(), getSubject, getContent, GetAddresses.empty())
    }

    static MailDeliveryParam valueOf(GetFrom getFrom,
                                     GetEnvelopeFrom getEnvelopeFrom,
                                     GetAddresses getAddressesTO,
                                     GetAddresses getAddressesCC,
                                     GetAddresses getAddressesBCC,
                                     GetSubject getSubject,
                                     GetContent getContent,
                                     GetAddresses getAddressesReplyTo) {
        MailDeliveryParam param = new MailDeliveryParam()
        param.getFrom = getFrom
        param.getEnvelopeFrom = getEnvelopeFrom
        param.getAddressesTO = getAddressesTO
        param.getAddressesCC = getAddressesCC
        param.getAddressesBCC = getAddressesBCC
        param.getSubject = getSubject
        param.getContent = getContent
        param.getAddressesReplyTo = getAddressesReplyTo
        param
    }

    GetFrom getGetFrom() {
        return getFrom
    }

    Date getSentDate() {
        return sentDate
    }

    GetContent getGetContent() {
        return getContent
    }

    GetSubject getGetSubject() {
        return getSubject
    }

    GetAddresses getGetAddressesBCC() {
        return getAddressesBCC
    }

    GetAddresses getGetAddressesCC() {
        return getAddressesCC
    }

    GetAddresses getGetAddressesTO() {
        return getAddressesTO
    }

    GetEnvelopeFrom getGetEnvelopeFrom() {
        return getEnvelopeFrom
    }

    GetAddresses getGetAddressesReplyTo() {
        return getAddressesReplyTo
    }
}
