package ish.oncourse.willow.portal.secur

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils

import java.util.regex.Matcher

class SecurityUtil {
    
    static String enrciptUrl(String url, String salt) {
        return "$url&key=${sha256encodeBase64(url+salt)}"
    }

    static String sha256encodeBase64(String str) {
        byte[] digest = DigestUtils.sha256(str)
        Base64.encodeBase64String(digest)
    }
    
    
    static Boolean verifyUrl(String signedUrl, String salt) {

        Matcher linkMatcher = (~/(.*)&key=(.*)/).matcher(signedUrl)

        if (linkMatcher.find()) {
            String url =  linkMatcher.group(1)
            String hash = linkMatcher.group(2)
            return hash == sha256encodeBase64(url+salt)
        } else {
            return null
        }
    }
}
