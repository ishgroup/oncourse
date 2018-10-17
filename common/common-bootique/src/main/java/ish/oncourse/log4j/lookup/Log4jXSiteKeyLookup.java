package ish.oncourse.log4j.lookup;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.StrLookup;

/**
 * Log4j2 lookup for HTTP headers
 */
@Plugin(name = "header", category = "Lookup")
public class Log4jXSiteKeyLookup implements StrLookup {

    private static final String DEFAULT_LOOKUP_VALUE = "undefined";

    @Override
    public String lookup(String key) {
        return lookup(null, key);
    }

    @Override
    public String lookup(LogEvent event, String key) {
        String header = XSiteKeyStorage.get();
        return header != null ? header : DEFAULT_LOOKUP_VALUE;
    }
}
