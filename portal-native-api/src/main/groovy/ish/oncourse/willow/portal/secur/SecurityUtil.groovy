package ish.oncourse.willow.portal.secur

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils

class SecurityUtil {
    
    static String enrciptUrl(String url, String salt) {
        return "$url&key=${sha256encodeBase64(url+salt)}"
    }

    static String sha256encodeBase64(String str) {
        byte[] digest = DigestUtils.sha256(str)
        Base64.encodeBase64String(digest)
    }
}
