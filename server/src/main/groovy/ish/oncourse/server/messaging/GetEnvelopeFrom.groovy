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
import org.apache.commons.lang3.StringUtils

@CompileDynamic
class GetEnvelopeFrom {

    private static final String AT_SIGN = '@'

    private String fromEmail
    private boolean isBounceProcessing
    private String emailBounceAddress
    private String plusTag

    private GetEnvelopeFrom(){

    }

    static GetEnvelopeFrom valueOf(String fromEmail) {
        valueOf(fromEmail, false, null, null)
    }


    static GetEnvelopeFrom valueOf(String fromEmail, boolean isBounceProcessing, String emailBounceAddress, String plusTag) {
        GetEnvelopeFrom getEnvelopeFrom = new GetEnvelopeFrom()
        getEnvelopeFrom.fromEmail = fromEmail
        getEnvelopeFrom.isBounceProcessing = isBounceProcessing
        getEnvelopeFrom.emailBounceAddress = emailBounceAddress
        getEnvelopeFrom.plusTag = plusTag
        getEnvelopeFrom
    }

    String get() {
        if (isBounceProcessing && StringUtils.isNotBlank(plusTag)) {
            int at = emailBounceAddress.indexOf(AT_SIGN)
            "${emailBounceAddress.substring(0, at)}+${plusTag}${emailBounceAddress.substring(at)}".toString()
        } else {
            fromEmail
        }
    }
}
