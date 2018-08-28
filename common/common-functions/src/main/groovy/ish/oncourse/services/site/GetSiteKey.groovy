package ish.oncourse.services.site

import java.util.regex.Matcher
import java.util.regex.Pattern

class GetSiteKey {
    
    public static final String TECHNICAL_DOMAIN = "oncourse.cc"
    
    private static final Pattern TECHNICAL_SITES_DOMAIN_PATTERN = Pattern
            .compile("([a-z0-9,-]+)([.].+[.]oncourse[.]net[.]au)")

    private static final Pattern SHORT_SITES_DOMAIN_PATTERN = Pattern
            .compile("([a-z0-9,-]+)([.]oncourse[.]cc)")

    private String serverName
    
    GetSiteKey(String serverName) {
        this.serverName = serverName.toLowerCase()
    }
    
    String get() {
        String siteKey = null
        
        Matcher matcher = SHORT_SITES_DOMAIN_PATTERN.matcher(serverName)

        boolean siteKeyFound = matcher.matches()
        if (siteKeyFound) {
            siteKey = matcher.group(1)
        } else {
            matcher = TECHNICAL_SITES_DOMAIN_PATTERN.matcher(serverName)
            if (matcher.matches()) {
                siteKey = matcher.group(1)
            }
        }
        siteKey
    }
    
}
