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

package ish.oncourse.server.api.function

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils

class SecurityFunctions {

    static String generateHashString(String str, String salt) {
        sha256encodeBase64(str + salt)
    }

    static String sha256encodeBase64(String str) {
        byte[] digest = DigestUtils.sha256(str)
        Base64.encodeBase64String(digest)
    }
}
