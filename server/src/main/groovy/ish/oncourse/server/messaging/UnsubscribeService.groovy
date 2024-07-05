/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.messaging

import com.google.inject.Inject
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.validation.EntityValidator
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.license.LicenseService
import ish.util.UrlUtil
import org.apache.cayenne.query.ObjectSelect
import org.apache.http.client.utils.URLEncodedUtils

import java.nio.charset.Charset
import java.util.regex.Pattern

class UnsubscribeService {
    private static final String LINK_PATTERN = "https://%s.cloud.oncourse.cc/unsubscribe?email=%s"
    private static final Pattern ANGEL_LINK_PATTERN = Pattern.compile("http(s?)://(.*).cloud.oncourse.cc/unsubscribe(.*)&key=");

    private static final int DAYS_EXPIRE = 7

    @Inject
    LicenseService licenseService

    @Inject
    ICayenneService cayenneService

    @Inject
    EntityValidator validator

    String generateLinkFor(String email){
        def url = String.format(LINK_PATTERN, licenseService.college_key, email)
        UrlUtil.createSignedUrl(url, new Date().plus(DAYS_EXPIRE), licenseService.getSecurity_key())
    }

    void unsubscribe(String link) {
        if(!UrlUtil.validateSignedAngelUrl(link, licenseService.security_key, new Date())){
            validator.throwClientErrorException("link", "Link to unsubscribe was incorrect or expired")
        }

        def parseResult = URLEncodedUtils.parse(new URI(link), Charset.forName("UTF-8"))
        def emailParam = parseResult.find {it.name == "email"}
        if(!emailParam)
            validator.throwClientErrorException("link", "Link to unsubscribe was incorrect or expired")

        def context = cayenneService.newContext
        def contacts = ObjectSelect.query(Contact).where(Contact.EMAIL.eq(emailParam.value)).select(context)

        contacts.each {it -> it.setIsUndeliverable(true)}
        context.commitChanges()
    }
}
